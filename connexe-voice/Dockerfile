FROM ubuntu:latest
RUN apt-get update && \
    apt-get install -y build-essential pip net-tools iputils-ping iproute2 curl

RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
RUN apt-get install -y nodejs
RUN npm install -g watchify

EXPOSE 3000
EXPOSE 2000-2020
EXPOSE 10000-10100