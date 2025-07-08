package com.sideproject.parking_java.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sideproject.parking_java.redis.RedisSubscriber;
import com.sideproject.parking_java.utility.MemberIdUtil;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;


@Service
@Slf4j
public class AwsS3Service {
    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    Logger logger = LoggerFactory.getLogger(AwsS3Service.class);

    @Bean
    public S3TransferManager createCustomTm() {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        S3AsyncClient s3AsyncClient = S3AsyncClient.crtBuilder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region))
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(8 * 1024 * 1024L)
                .build();

        S3TransferManager transferManager = S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();

        return transferManager;
    }

    public String uploadFile(MultipartFile img) throws IOException {
        logger.info("img", img);
        int memberId = MemberIdUtil.getMemberIdUtil();
        Path temp = Files.createTempFile(
            "upload-", 
            "-" + img.getOriginalFilename()
        );
        img.transferTo(temp.toFile());
        URI filePathURI = temp.toUri();
        String fileName = Integer.toString(memberId) + "-" + img.getOriginalFilename() + "-" + new Date().toString();

        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
            .putObjectRequest(b -> b.bucket(bucketName).key(fileName))
            .source(Paths.get(filePathURI))
            .build();

        FileUpload fileUpload = createCustomTm().uploadFile(uploadFileRequest);
        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
        Files.delete(temp);
        System.out.println(uploadResult.response().eTag());

        return fileName;
    }

    public String returnUrl(String fileName) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }
}
