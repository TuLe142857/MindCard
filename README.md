# MindCard

> [!NOTE]  
> Backend API document: [./backend/docs/API_DESC.md](./backend/docs/API_DESC.md)

## Requirements
- JDK 25
- Node.js 24
- Docker & Docker Compose

## Development

Copy `.env.example` to `.env`. In most cases, you don't need to modify this file for the development environment:
```shell
cp .env.example .env
```

Run the infrastructure services (Database, Redis, S3, MailHog) using Docker:
```shell
make build-dev
```

Run the frontend and backend on your host machine for easier debugging and hot reloading:

**Frontend:**
```shell
cd frontend
npm install
npm run dev
```

**Backend:**
```shell
# You can also run this using an IDE like IntelliJ IDEA
cd backend
./mvnw spring-boot:run
```

> [!NOTE]  
> Default external ports (host machine ports mapped from Docker containers). You can modify these in the `.env` file if port conflicts occur:  
> - **PostgreSQL**: `5432`
> - **MinIO API (S3)**: `9000`
> - **MinIO Console**: http://localhost:9001
> - **Redis**: `6379`
> - **Redis Insight (Web UI)**: http://localhost:5540
> - **MailHog SMTP Server**: `1025`
> - **MailHog Web UI**: http://localhost:8025

## Deploy
Coming Soon :)