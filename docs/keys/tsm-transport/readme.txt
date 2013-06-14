1) create private key / cert
keytool -genkey -keyalg RSA -dname "CN=www.tsm-transport.com, OU=TSM-TRANSPORT, O=TSM-TRANSPORT, ST=Budapest, C=HU" -validity 1000 -alias tsmtransportkey -keypass tsmtransportpass -keystore tsm-transport.jks -storepass tsmtransportpass

2) create a CSR
keytool -certreq -alias tsmtransportkey -file tsm-transport_csr.pem -keypass tsmtransportpass -keystore tsm-transport.jks -storepass tsmtransportpass

3) issue cert with CA1
ca_create_server_cert.bat tsm-transport_csr.pem tsm-transport

4) concatenate the issued cert and the CA cert
copy tsm-transport.cer + ca.cer tsm-transport_chain.cer

5) update keystore
keytool -import -file tsm-transport_chain.cer -keypass tsmtransportpass -keystore tsm-transport.jks -storepass tsmtransportpass -alias tsmtransportkey
keytool -import -file ca.cer -keystore tsm-transport.jks -storepass tsmtransportpass -alias ca1
keytool -import -file ca.cer -keystore tsm-transport.jks -storepass tsmtransportpass -alias ca2
keytool -import -file client.cer -keystore tsm-transport.jks -storepass tsmtransportpass -alias client