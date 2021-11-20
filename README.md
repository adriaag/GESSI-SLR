1. Create docker image:
```shell
docker build -t gessi-slr .
```
2. Setting up the volume
```shell
docker volume create --driver local --opt device=/d/DockerVolumes/gessi-slr --opt type=none --opt o=bind gessi-slr
```
Note: You will need to create that folder structure beforehand, otherwise the volume won’t be created.

3. Running it locally on `http://localhost:1031/gessi-slr`
```shell
docker run -d --mount source=gessi-slr,target=/var/lib/gessi-slr -p 1031:8080 gessi-slr
```