FROM eclipse-temurin:21.0.10_7-jdk-ubi9-minimal
EXPOSE 8080:8080
RUN microdnf install -y findutils
RUN mkdir /app
COPY ./build/install/kottage/ /app/
WORKDIR /app/bin
CMD ["./kottage"]
