podman run -d --name sonar-db \
-e POSTGRES_USER=sonar \
-e POSTGRES_PASSWORD=sonar \
-e POSTGRES_DB=sonarqube \
-p 5432:5432 \
docker.io/library/postgres:latest