
DC_DEV = docker compose -f docker-compose.yml -f docker-compose-dev.yml
DC_PROD = docker compose -f docker-compose.yml -f docker-compose-prod.yml

build-dev:
	${DC_DEV} up -d
down-dev:
	${DC_DEV} down
clean-dev:
	${DC_DEV} down --rmi local -v
