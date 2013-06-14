1) create private key / cert
keytool -genkey -keyalg RSA -dname "CN=www.client.com, OU=Client, O=Client, ST=Budapest, C=HU" -validity 1000 -alias clientkey -keypass clientpass -keystore client.jks -storepass clientpass

2) create a CSR
keytool -certreq -alias clientkey -file client_csr.pem -keypass clientpass -keystore client.jks -storepass clientpass

3) issue cert with CA1
ca_create_server_cert.bat client_csr.pem client

4) concatenate the issued cert and the CA cert
copy client.cer + ca.cer client_chain.cer

5) update keystore
keytool -import -file client_chain.cer -keypass clientpass -keystore client.jks -storepass clientpass -alias clientkey
keytool -import -file ca.cer -keypass ca1pass -keystore client.jks -storepass clientpass -alias ca1
keytool -import -file ca.cer -keypass ca2pass -keystore client.jks -storepass clientpass -alias ca2
keytool -import -file comm.cer -keystore client.jks -storepass clientpass -alias comm