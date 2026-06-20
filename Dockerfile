FROM payara/micro:5.2022.2-jdk11

USER root

# Instalar certificados CA actualizados de Alpine
RUN apk add --no-cache ca-certificates openssl

# Copiar CA de Aiven e importarla al truststore de Java
COPY ca.pem /tmp/aiven-ca.pem
RUN keytool -importcert -trustcacerts -noprompt \
    -alias aiven-ca \
    -file /tmp/aiven-ca.pem \
    -keystore /usr/lib/jvm/zulu11-ca/lib/security/cacerts \
    -storepass changeit

USER payara

ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"

COPY ROOT.war /app/ROOT.war
COPY mariadb.jar /opt/payara/mariadb.jar

ENTRYPOINT ["java", "-jar", "/opt/payara/payara-micro.jar", "--addLibs", "/opt/payara/mariadb.jar", "--deploy", "/app/ROOT.war", "--contextroot", "/", "--port", "8080", "--noCluster"]
