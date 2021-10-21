FROM openjdk:15
COPY . /myapp/
WORKDIR /myapp/
RUN javac -cp src/ src/Main.java -d dst/