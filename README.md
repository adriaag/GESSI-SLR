1. Create docker image:
```shell
docker build -t gessi-slr .
```
2. Setting up the volume
```shell
docker volume create --d local-persist --opt mountpoint=/d/DockerVolumes/gessi-slr --name gessi-slr
```
Note: You will need to create that folder structure beforehand, otherwise the volume won’t be created.

3. Running it locally on `http://localhost:1031/gessi-slr`
```shell
docker run --name gessi-slr --mount source=gessi-slr,target=/var/lib/gessi-slr -p 1031:8080
```