cd ../sso-demo-comm/sso-demo-comm-war
mvn clean install -DskipTests
cp target/sso-demo-comm-war.war /opt/apache-tomcat-7.0.40-sso-demo-tsm/webapps

