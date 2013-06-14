1) create private key / cert
keytool -genkey -keyalg RSA -dname "CN=www.service1.com, OU=Service1, O=Service1, ST=Budapest, C=HU" -validity 1000 -alias service1key -keypass service1pass -keystore service1.jks -storepass service1pass

2) create a CSR
keytool -certreq -alias service1key -file service1_csr.pem -keypass service1pass -keystore service1.jks -storepass service1pass

3) issue cert with CA1
ca_create_server_cert.bat service1_csr.pem service1

4) concatenate the issued cert and the CA cert
copy service1.cer + ca.cer service1_chain.cer

5) update keystore
keytool -import -file service1_chain.cer -keypass service1pass -keystore service1.jks -storepass service1pass -alias service1key
keytool -import -file ca.cer -keystore service1.jks -storepass service1pass -alias ca1
keytool -import -file ca.cer -keystore service1.jks -storepass service1pass -alias ca2
keytool -import -file ca.cer -keystore client.jks -storepass service1pass -alias client