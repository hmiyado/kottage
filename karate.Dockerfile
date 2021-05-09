FROM gradle:7.0-jdk8
COPY . /home/gradle/project
WORKDIR /home/gradle/project
