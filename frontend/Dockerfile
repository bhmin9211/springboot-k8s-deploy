FROM node:18-alpine AS build-stage
WORKDIR /frontend

ARG VITE_API_BASE_URL=http://localhost:8081/api
ARG VITE_AUTH_LOGIN_PATH=/auth/login/keycloak
ARG VITE_AUTH_LOGOUT_PATH=/auth/logout

ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_AUTH_LOGIN_PATH=${VITE_AUTH_LOGIN_PATH}
ENV VITE_AUTH_LOGOUT_PATH=${VITE_AUTH_LOGOUT_PATH}

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:stable-alpine AS production-stage
COPY --from=build-stage /frontend/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
