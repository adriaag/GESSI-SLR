include makefile.env
PROJECT=$(DOCKER_IMAGE_NAME)
DOCKER_NAME_FULL=$(DOCKER_IMAGE_NAME):$(DOCKER_IMAGE_TAG)
DOCKER_LOCALHOST=$(shell /sbin/ifconfig docker0 | pcregrep 'inet addr:' | cut -d: -f2 | awk '{ print $$1}')

up:
	@docker-compose -p $(DOCKER_CONTAINER_NAME) up -d

down:
	@docker-compose -p $(DOCKER_CONTAINER_NAME) down

ver:
	echo version: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}

build-volume:
	# build docker volume
	@docker volume create --name db-volume --opt type=local --opt device=/home/usuario/slrgessi/db-volume --opt o=bind

build:
	# build docker image
	@docker build -t $(DOCKER_NAME_FULL) .

save-image:
	@mkdir -p docker-images
	@docker save com.webapp.gessi/gessi-slr:1.0.1 | gzip > "./docker-images/gessi-slr.tar.gz"
	@docker save opavlova/db-derby:latest | gzip > "./docker-images/db-derby.tar.gz"