cd ../sso-demo-security/sso-demo-security-sts
mvn clean install -DskipTests
cp target/sso-demo-security-sts.war /opt/apache-tomcat-7.0.40-sso-demo-sts/webapps
