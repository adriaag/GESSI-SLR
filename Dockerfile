FROM maven:3.8 as maven
LABEL MAINTAINER="adria.aumatell@estudiantat.upc.edu"
LABEL APPLICATION="Sample Application"

WORKDIR /usr/src/app
COPY . /usr/src/app

RUN mvn package

FROM tomcat:9.0

ENV CATALINA_OPTS="-Xms1024m -Xmx4096m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -Xss512k"

#Move over the War file from previous build step
WORKDIR /usr/local/tomcat/webapps/
COPY --from=maven /usr/src/app/target/gessi-slr.war /usr/local/tomcat/webapps/

ENTRYPOINT ["catalina.sh", "run"]
