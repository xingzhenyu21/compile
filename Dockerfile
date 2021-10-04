FROM gcc:10
WORKDIR /app/
COPY test.c  ./
RUN gcc test.c -o test
RUN chmod +x test
