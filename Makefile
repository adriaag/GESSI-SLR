include makefile.env
PROJECT=dlyte-machine-manager-backend
DOCKER_NAME_FULL=$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)
DOCKER_LOCALHOST=$(shell /sbin/ifconfig docker0 | pcregrep 'inet addr:' | cut -d: -f2 | awk '{ print $$1}')

up:
	@docker run -d --mount source=gessi-slr,target=/var/lib/gessi-slr -p 1031:8080 $(PROJECT)

down:
	@docker-compose -p $(PROJECT) down

ver:
	echo version: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}

build:
	mvn -Dversioning.disable=true clean install
	# build docker image
	@docker build --build-arg APP_VERSION=${APP_VERSION} -t $(DOCKER_NAME_FULL) .
	# build docker volume
	@docker volume create --driver local --opt device=/d/DockerVolumes/gessi-slr --opt type=none --opt o=bind gessi-slr

save-image:
	@docker save $(DOCKER_IMAGE_NAME):${DOCKER_IMAGE_TAG} | gzip > "../deployment/docker-images/${DOCKER_CONTAINER_NAME}_${DOCKER_IMAGE_TAG}.tar.gz"