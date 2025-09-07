FROM openjdk:17-jdk
EXPOSE 8080:8080
RUN microdnf install findutils
RUN mkdir /app
COPY ./build/install/kottage/ /app/
WORKDIR /app/bin
CMD ["./kottage"]
