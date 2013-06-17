1) create private key / cert
keytool -genkey -keyalg RSA -dname "CN=www.client.com, OU=Client, O=Client, ST=Budapest, C=HU" -validity 1000 -alias clientkey -keypass clientkeypass -keystore client.jks -storepass clientstorepass

2) create a CSR
keytool -certreq -alias clientkey -file client_csr.pem -keypass clientkeypass -keystore client.jks -storepass clientstorepass

3) issue cert with CA1
ca_create_server_cert.bat client_csr.pem client

4) concatenate the issued cert and the CA cert
copy client.cer + ca.cer client_chain.cer

5) update keystore
keytool -import -file client_chain.cer -keypass clientkeypass -keystore client.jks -storepass clientstorepass -alias clientkey
keytool -import -file ca.cer -keypass ca1keypass -keystore client.jks -storepass clientstorepass -alias ca1
keytool -import -file ca.cer -keypass ca2keypass -keystore client.jks -storepass clientstorepass -alias ca2
keytool -import -file comm.cer -keypass commkeypass -keystore client.jks -storepass clientstorepass -alias comm