FROM payara/micro:5.2022.2-jdk11

ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"

COPY ROOT.war /app/ROOT.war
COPY mariadb.jar /opt/payara/mariadb.jar
COPY ca.pem /app/ca.pem

ENTRYPOINT ["java", "-jar", "/opt/payara/payara-micro.jar", "--addLibs", "/opt/payara/mariadb.jar", "--deploy", "/app/ROOT.war", "--contextroot", "/", "--port", "8080", "--noCluster"]
