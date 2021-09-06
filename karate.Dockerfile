FROM gradle:7.2-jdk8
COPY . /home/gradle/project
WORKDIR /home/gradle/project
