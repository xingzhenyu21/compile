#FROM openjdk:15
#COPY ./ /app/
#WORKDIR /app/
#RUN javac -d ./output ./src/*.java
#WORKDIR /app/output
FROM openjdk:15
WORKDIR /app/
COPY ./src/* ./
RUN javac *.java