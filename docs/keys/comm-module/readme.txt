1) create private key / cert
keytool -genkey -dname "CN=www.comm.com, OU=Comm, O=Comm, ST=Budapest, C=HU" -validity 1000 -alias commkey -keypass commpass -keystore comm.jks -storepass commpass

2) create a CSR
keytool -certreq -alias commkey -file comm_csr.pem -keypass commpass -keystore comm.jks -storepass commpass

3) issue cert with CA1
ca_create_server_cert.bat comm_csr.pem comm

4) concatenate the issued cert and the CA cert
copy comm.cer + ca.cer comm_chain.cer

5) update keystore
keytool -import -file comm_chain.cer -keypass commpass -keystore comm.jks -storepass commpass -alias commkey
keytool -import -file ca.cer -keypass ca1pass -keystore comm.jks -storepass commpass -alias ca1
keytool -import -file ca.cer -keypass ca2pass -keystore comm.jks -storepass commpass -alias ca2