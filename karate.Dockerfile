FROM gradle:7.3-jdk8
COPY . /home/gradle/project
WORKDIR /home/gradle/project
