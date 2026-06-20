FROM payara/micro:5.2022.2-jdk11

ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"

COPY ROOT.war /app/ROOT.war

# Extraemos el driver de MySQL del WAR para agregarlo al classloader de Payara Micro
RUN mkdir /app/libs && cd /app/libs && jar xf /app/ROOT.war WEB-INF/lib/mysql-connector-java-8.0.17.jar

ENTRYPOINT ["java", "-jar", "/opt/payara/payara-micro.jar", "--addLibs", "/app/libs/WEB-INF/lib/mysql-connector-java-8.0.17.jar", "--deploy", "/app/ROOT.war", "--contextroot", "/", "--port", "8080", "--noCluster"]
