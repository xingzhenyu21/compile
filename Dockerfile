#FROM openjdk:15
#COPY ./ /app/
#WORKDIR /app/
#RUN javac -d ./output ./src/*.java
#WORKDIR /app/output
FROM openjdk:15
WORKDIR /app/
COPY ./src/* ./
COPY ./2.txt ./
RUN javac *.java