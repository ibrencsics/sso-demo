info: http://tech-meanderings.blogspot.de/2009/07/using-openssl-to-create-certificate.html

1) create private key
openssl genrsa -des3 -out keys\ca.key 1024
ca1pass

2) create public key + X.509
openssl req -config openssl.conf -new -x509 -days 1001 -key keys\ca.key -out certs\ca.cer
CN = www.ca1.com
OU = CA1
O = CA1
L = Budapest
S = Budapest
C = HU

3) export DER
openssl x509 -in certs\ca.cer -outform DER -out certs\ca.der