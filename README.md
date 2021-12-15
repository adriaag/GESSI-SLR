# GESSI-SLR

## Developers cheat sheet

### Makefile
1. Create docker volume:
```shell
make build-volume
```

2. Create docker image:
```shell
make build
```

3. Running it
```shell
make up
```

4. Delete containers
```shell
make down
```

### Without Makefile
1. Create docker volume:
```shell
docker volume create --name db-volume --opt type=local --opt device=/home/slrgessi/db-volume --opt o=bind
```

2. Create docker image:
```shell
docker build -t com.webapp.gessi/gessi-slr:1.0.1 .
```

3. Running it
**This can fail because it use the name of the derby container in the datasource.url you have to check the name and replace it**
```shell
docker-compose -p gessi-slr up -d
```

4. Delete containers
```shell
docker-compose -p gessi-slr down
``` 

## Check result
- [Tomcat Webapp](http://localhost:1025/gessi-slr)
- [Derby DB](http://localhost:1024/DOCKERDB)
- Connect to DB: `jdbc:derby://localhost:1024/DOCKERDB`