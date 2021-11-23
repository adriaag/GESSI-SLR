# GESSI-SLR

## Developers cheat sheet

### Makefile

1. Create docker image:
```shell
make build
```

2. Running it locally on `http://localhost:1031/gessi-slr`
```shell
make up
```

### Without Makefile

1. Create docker image:
```shell
mvn -Dversioning.disable=true clean install
# build docker image
docker build -t com.example.tfgdefinitivo/gessi-slr:1.0.0-SNAPSHOT .
```

2. Running it locally on `http://localhost:1031/gessi-slr`
```shell
docker-compose -p gessi-slr up -d
```