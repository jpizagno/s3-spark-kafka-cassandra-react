FROM node:8

RUN mkdir /usr/app/

COPY lib /usr/app/lib/
COPY samples /usr/app/samples/
COPY index.js /usr/app/
COPY package.json /usr/app/

WORKDIR /usr/app/
RUN npm install

CMD ["node","samples/server.js"]