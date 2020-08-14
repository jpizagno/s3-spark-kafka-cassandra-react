FROM node:latest

COPY ./ /app/

COPY entrypoint.sh /app/

WORKDIR /app/

ENTRYPOINT ["/app/entrypoint.sh"]