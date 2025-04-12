# Build stage
FROM node:18-alpine AS build-stage
WORKDIR /frontend
COPY . .
RUN npm install && npm run build

# Production stage
FROM nginx:stable-alpine AS production-stage
COPY --from=build-stage /frontend/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

