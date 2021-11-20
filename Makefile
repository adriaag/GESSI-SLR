include makefile.env
PROJECT=$(DOCKER_CONTAINER_NAME)
DOCKER_NAME_FULL=$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)
DOCKER_LOCALHOST=$(shell /sbin/ifconfig docker0 | pcregrep 'inet addr:' | cut -d: -f2 | awk '{ print $$1}')

up:
	@docker run --name $(PROJECT) --mount source=gessi-slr,target=/var/lib/gessi-slr -p 1031:8080

down:
	@docker-compose -p $(PROJECT) down

ver:
	echo version: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}

build:
	mvn -Dversioning.disable=true clean install
	# build docker image
	@docker build --build-arg APP_VERSION=${APP_VERSION} -t $(DOCKER_NAME_FULL) .
	# build docker volume
	@docker volume create --d local-persist --opt mountpoint=/d/DockerVolumes/$(PROJECT) --name $(PROJECT)

save-image:
	@docker save $(DOCKER_IMAGE_NAME):${DOCKER_IMAGE_TAG} | gzip > "../deployment/docker-images/${DOCKER_CONTAINER_NAME}_${DOCKER_IMAGE_TAG}.tar.gz"