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
# Docker and k8s support grafana - prom-operator

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

k8s_apply:
	kubectl apply -f k8s/microservice/templates

k8s_delete:
	kubectl delete -f k8s/microservice/templates

helm_install:
	kubens default
	helm install -f k8s/microservice/values.yaml microservices k8s/microservice

helm_uninstall:
	kubens default
	helm uninstall microservices

helm_install_all:
	helm repo update
	kubectl create namespace monitoring
	helm install monitoring prometheus-community/kube-prometheus-stack -n monitoring
	kubens default
	helm install -f k8s/microservice/values.yaml microservices k8s/microservice

helm_uninstall_all:
	kubens monitoring
	helm uninstall monitoring
	kubens default
	helm uninstall microservices
	kubectl delete namespace monitoring

wrk:
	wrk -t20 -c200 -d60s --latency http://localhost:8000/api/v1/bank/0b661cb6-9cc7-41a5-b891-1d05d00fd49a