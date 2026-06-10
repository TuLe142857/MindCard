# MindCard

> [!NOTE]  
> Backend API document: [./backend/docs/API_DESC.md](./backend/docs/API_DESC.md)

# Requirement:
- JDK-25
- Node-24
- Docker

# Development

```shell
# run FE and Backend Infrastructure
make build-dev
# docker compose -f docker-compose.yml -f docker-compose-dev.yml up -d --build

# run backend server with wrapped mvn of spring boot
cd ./backend
./mvnw spring-boot:run
```

# Deploy

