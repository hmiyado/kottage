FROM eclipse-temurin:17.0.16_8-jdk-ubi9-minimal
EXPOSE 8080:8080
RUN microdnf install -y findutils
RUN mkdir /app
COPY ./build/install/kottage/ /app/
WORKDIR /app/bin
CMD ["./kottage"]
