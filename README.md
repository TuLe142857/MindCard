# MindCard

# Requirement:
- JDK-25
- Node-24
- Docker

# Development

```shell
# run FE and Backend Infrastructure
docker compose -f docker-compose.yml -f docker-compose-dev.yml up -d --build

# run backend with wrapped mvn of spring boot
cd ./backend
./mvnw spring-boot:run
```

# Deploy

