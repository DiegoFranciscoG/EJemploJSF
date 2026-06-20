# Usamos Payara Micro que es mucho mas liviano y funciona perfecto con Render Free (512MB)
FROM payara/micro:5.2022.2-jdk11

# Limitamos estrictamente la memoria RAM para evitar que Render lo mate (Out of Memory)
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"

# Copiamos la aplicacion al directorio de despliegue de Payara Micro
COPY ROOT.war $DEPLOY_DIR

# Forzamos a que el contexto sea /
CMD ["--deploy", "/opt/payara/deployments/ROOT.war", "--contextroot", "/"]
