cd ../sso-demo-business/sso-demo-business-service2
mvn clean install -DskipTests
cp target/sso-demo-business-service2.war /opt/apache-tomcat-7.0.40-sso-demo-tsm/webapps
