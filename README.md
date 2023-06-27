# RMI-RSA-Implementation
An implementation of Remote Method Invocation using java. This project also contains a folder that implements Remote Method Invocation using RSA algorithm.


## DEPENDENCIES {POWER SHELL / CMD / TERMINAL, JAVA JDK 17, OPEN SSL, GIT} 

**Run These Commands First Before starting the rmiregistry.**

### Generate a self-signed certificate and a private key using OpenSSL.  

you can follow these steps: 

 

### Private_key.pem and certificate.csr file 

openssl req -newkey rsa:2048 -nodes -keyout private_key.pem -out certificate.csr 
// The Inputs you provide for this should match the one provided in you KeyStoreGenerator class file.
 

### Generate Certificate.pem 

openssl x509 -req -in certificate.csr -signkey private_key.pem -out certificate.pem 

 

### Generate keystore.p12 

**[Structure]**

keytool -importcert -file certificate.pem -alias myalias -keystore keystore.p12 -storetype PKCS12 

**[run this]**

keytool -importcert -file certificate.pem -alias _certificate_ -keystore keystore.p12 -storetype PKCS12 -storepass _Group2_  
**Replace -alias [your value] -storepass [your value]**

__Ensure your working directory matches with the path to where the program files will be run from. Edit paths stated in the code to conform to yours.__
