FROM node:18.17.1-alpine
LABEL authors="bogdanjovanovic"

ARG API_BASE_URL
ARG WS_BASE_URL

WORKDIR /app

COPY package*.json .
RUN npm install --silent
COPY . .
RUN echo "VITE_API_BASE_URL=${API_BASE_URL}" > .env
RUN echo "VITE_WS_BASE_URL=${WS_BASE_URL}" > .env
EXPOSE 3000
CMD ["npm", "run", "start:dev"]
