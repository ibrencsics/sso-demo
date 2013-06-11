1) create private key / cert
keytool -genkey -dname "CN=www.sts-transport.com, OU=STS-TRANSPORT, O=STS-TRANSPORT, ST=Budapest, C=HU" -validity 1000 -alias ststransportkey -keypass ststransportpass -keystore sts-transport.jks -storepass ststransportpass

2) create a CSR
keytool -certreq -alias ststransportkey -file sts-transport_csr.pem -keypass ststransportpass -keystore sts-transport.jks -storepass ststransportpass

3) issue cert with CA1
ca_create_server_cert.bat sts-transport_csr.pem sts-transport

4) concatenate the issued cert and the CA cert
copy sts-transport.cer + ca.cer sts-transport_chain.cer

5) update keystore
keytool -import -file sts-transport_chain.cer -keypass ststransportpass -keystore sts-transport.jks -storepass ststransportpass -alias ststransportkey
keytool -import -file ca.cer -keypass ca1pass -keystore sts-transport.jks -storepass ststransportpass -alias ca1
keytool -import -file ca.cer -keypass ca2pass -keystore sts-transport.jks -storepass ststransportpass -alias ca2