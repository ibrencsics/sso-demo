info: http://fusesource.com/docs/broker/5.3/security/i382664.html

1) create private key / cert
keytool -genkey -dname "CN=www.sts.com, OU=STS, O=STS, ST=Budapest, C=HU" -validity 1000 -alias stskey -keypass stspass -keystore sts.jks -storepass stspass

2) create a CSR
keytool -certreq -alias stskey -file sts_csr.pem -keypass stspass -keystore sts.jks -storepass stspass

3) issue cert with CA1
ca_create_server_cert.bat sts_csr.pem sts

4) concatenate the issued cert and the CA cert
copy sts.cer + ca.cer sts_chain.cer

5) update keystore
keytool -import -file sts_chain.cer -keypass stspass -keystore sts.jks -storepass stspass -alias stskey