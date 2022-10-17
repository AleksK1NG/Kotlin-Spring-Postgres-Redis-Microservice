### Kotlin, Spring WebFlux, R2DBC and Redisson microservice 👋✨💫

#### 👨‍💻 Full list what has been used:
[Spring](https://spring.io/) Spring web framework <br/>
[Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) Reactive REST Services <br/>
[Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) a specification to integrate SQL databases using reactive drivers <br/>
[Redisson](https://redisson.org/) Redis Java Client <br/>
[Zipkin](https://zipkin.io/) open source, end-to-end distributed [tracing](https://opentracing.io/) <br/>
[Spring Cloud Sleuth](https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/index.html) auto-configuration for distributed tracing <br/>
[Prometheus](https://prometheus.io/) monitoring and alerting <br/>
[Grafana](https://grafana.com/) for to compose observability dashboards with everything from Prometheus <br/>
[Kubernetes](https://kubernetes.io/) automating deployment, scaling, and management of containerized applications <br/>
[Docker](https://www.docker.com/) and docker-compose <br/>
[Helm](https://helm.sh/) The package manager for Kubernetes <br/>
[Flywaydb](https://flywaydb.org/) for migrations<br/>

All UI interfaces will be available on ports:

#### Swagger UI: http://localhost:8000/webjars/swagger-ui/index.html
<img src="https://i.postimg.cc/y6PNgrfr/Swagger-UI-2022-10-08-12-17-39.png" alt="Swagger"/>

#### Grafana UI: http://localhost:3000
<img src="https://i.postimg.cc/dVngVPQz/Spring-Boot-2-1-System-Monitor-Dashboards-Grafana-2022-10-15-15-17-52.png" alt="Grafana"/>

#### Zipkin UI: http://localhost:9411
<img src="https://i.postimg.cc/v83QHxBJ/Zipkin-2022-10-08-11-18-08.png" alt="Zipkin"/>

#### Prometheus UI: http://localhost:9090
<img src="https://i.postimg.cc/tTDHT8X3/Prometheus-Time-Series-Collection-and-Processing-Server-2022-10-08-11-19-05.png" alt="Prometheus"/>


For local development 🙌👨‍💻🚀:

```
make local // for run docker compose
```
or
```
make develop // run all in docker compose
```