# ParkingShare

A mobile-friendly website focused on backend and database usage, offering services to store parking space rental information in MySQL database and renders it on google map for users, every parking data and transaction would be rendered at front-end immediately. It features a cash point storage system based on TapPay, and calculates the parking fees at the end of the service. Additionally, it provides historical usage information, including details of rentals, cash point storage, and transaction.

<img src=readmefile/img.png width=80% />

## Test Address

https://parkinglotshare.online/

## Create Account

Guests could use map functions without logging in. 
To use parking space rental and parking service, needing to sign up first.
Please follow the rules to input account and password. 

## Test Payment information

| Card Number | Valid Date | CVV |
|-----|--------|--------|
| 4242 4242 4242 4242 | 12/31 | 123 |

## Core Features
* Parking rental service
* Map Service

## Tech Stack
* Spring Boot
* WebSocket
* Redis
* Docker
* AWS EC2, Cloud Front, Nginx, S3, RDS, ElastiCache
* JSON Web Token(JWT)
* MySQL, Connection pool
* Google Map API
* HTML
* CSS
* Javascript

## Website architecture

<img src=readmefile/parkingabcST.png width=80% />

The backend of the website is built by ```Spring Boot``` framework, initially encapsulated in ```Docker``` and then deployed on ```Amazon EC2``` servers, while also configured with ```NGINX``` as a ```reverse proxy```, and utilizing SSL/TLS certificates from ```Let’s Encrypt``` to ensure secure ```HTTPS``` connections. Data is stored in a ```MySQL``` database managed by ```Amazon RDS```. In order to show the information changing immediately, ```Redis``` and ```Websocket``` are used. Static resources such as images and style sheets are stored on ```Amazon S3```, with content distribution accelerated through ```Amazon CloudFront``` for enhanced access speed.

## Database

Designing by MySQL relational database that conforms to the second normal form and utilizes foreign key constraints to enhance stability.
Usung Redis as a temperary database.

<img src=readmefile/DBS.png width=80% />

## Demo GIF

### Car information input (new member has to resgister a car.)

* Users need to input at least one car information for parking service.

<p align="center">
  <img src="readmefile/input car infor.gif" alt="Car Information Input" width="40%"/>
</p>
<br/>

### Deposit (Need to deposit money for parking)

* Users need to deposit. If the cash point is 0 or negative, parking service is forbidden.

<p align="center">
  <img src="readmefile/deposit.gif" alt="Deposit" width="40%"/>
</p>
<br/>

### Location Search

Using the Google Map APIs to provide destination searching.

<p align="center">
  <img src="readmefile/location search.gif" alt="Location Search" width="40%"/>
</p>
<br/>

### Navigation

Google Map APIs to provide navigation service.

<p align="center">
  <img src="readmefile/navigation.gif" alt="Navigation" width="40%"/>
</p>
<br/>

### Parking space rental input

Data input into MySQL to provide rental information for users.

<p align="center">
  <img src="readmefile/input parking space.gif" alt="Parking Space Rental Input" width="40%"/>
</p>
<br/>

### Parking Demo

If you arrive at the parking lot, follow the steps to start parking.

<p align="center">
  <img src="readmefile/parking demo.gif" alt="Parking Demo" width="40%"/>
</p>
<br/>

## Contact