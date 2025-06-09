package com.sideproject.parking_java.utility;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3Util {
    public String uploadToS3(MultipartFile img) {
        String fileName = img.getOriginalFilename();
        String imgUrl ="";

        if (fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") || 
        fileName.endsWith("png") || fileName.endsWith("jfif"))) {
            String UniquefileName = UUID.randomUUID().toString() + fileName;
            // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
            imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;  
        }
        return imgUrl;
    }
}
