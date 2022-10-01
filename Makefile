.PHONY:

# ==============================================================================
# Docker

local:
	@echo Starting local docker compose
	docker-compose -f docker-compose.local.yaml up -d --build

develop:
	mvn clean package -Dmaven.test.skip
	@echo Starting docker compose
	docker-compose -f docker-compose.yaml up -d --build


# ==============================================================================
# Docker support

FILES := $(shell docker ps -aq)

down-local:
	docker stop $(FILES)
	docker rm $(FILES)

clean:
	docker system prune -f

logs-local:
	docker logs -f $(FILES)

upload:
	mvn clean package -Dmaven.test.skip
	docker build -t alexanderbryksin/kotlin_spring_microservice:latest --platform=linux/arm64 -f ./Dockerfile .
	docker push alexanderbryksin/kotlin_spring_microservice:latest