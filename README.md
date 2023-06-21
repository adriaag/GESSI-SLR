# GESSI-SLR

## Developers cheat sheet

## Preliminary steps
1. Go to src/main/resources/
2. Create application-dev.properties from application-dev.properties.template providing necessary information:
```xml
spring.datasource.url = jdbc:derby://localhost:1033/GESSISLR
spring.datasource.username = #Usuari de la Base de Dades
spring.datasource.password = #Contrasenya usuari Base de Dades
security.tokenSecret = #Secret del token d'autenticació
spring.mail.username = #Correu forgot password (en el cas de Google,
					   #ha de tenir l'autenticació en dos passos acitvat)
spring.mail.password = #Constrasenya correu forgot password (en el cas de Google,
					   #no és la contranenya del compte, sinó que és una app password)
webapp.frontUrl = http://localhost:4200/
```

3. Create application-prod.properties from application-prod.properties.template providing necessary information:
```xml
spring.datasource.url = jdbc:derby://gessi-derby:1527/GESSISLR;
spring.datasource.username = #Usuari de la Base de Dades
spring.datasource.password = #Contrasenya usuari Base de Dades
security.tokenSecret = ${random.value} #Secret del token d'autenticació
spring.mail.username = #Correu forgot password (en el cas de Google,
					   #ha de tenir l'autenticació en dos passos acitvat)
spring.mail.password = #Constrasenya correu forgot password (en el cas de Google,
					   #no és la contranenya del compte, sinó que és una app password)
webapp.frontUrl = http://gessi3.essi.upc.edu:1034/gessi-slr/ui/ #URL del frontend, canviar si cal
```

## Production deployment

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
docker build -t com.webapp.gessi/gessi-slr:1.3.0 .
```

3. Running it
```shell
docker-compose -p gessi-slr up -d
```

4. Shut down containers
```shell
docker-compose -p gessi-slr down
``` 

### Check result
- [Webapp](http://gessi3.essi.upc.edu:1034/gessi-slr/ui/)
- [Derby DB](http://gessi3.essi.upc.edu:1033/GESSISLR)
- Connect to DB: `jdbc:derby://localhost:1033/GESSISLR`


## Develop deployent
1. Follow the steps for a production deployment.
2. Run src/main/java/com/webapp/gessi/GESSIApplication.java as java application (normal execution)
3. Go to src/main/ui/ and execute:
   ```shell
   ng serve
   ```
### Check result
- [Webapp](http://localhost:4200/)
- [Derby DB](http://localhost:1033/GESSISLR)
- Connect to DB: `jdbc:derby://localhost:1033/GESSISLR;`

## How I register new users?
1. With a database manages software connect to http://gessi3.essi.upc.edu:1033/GESSISLR.
2. Log in with database credentials. (Tip: if you don't know them, you can find the credentials on the running docker image. More details below)
3. Go to GESSISLR schema.
4. Exequte the following sql query:
```sql
INSERT INTO USERS (USERNAME, PASSWORD) VALUES ('username','password');
```
NOTE: password inserted must be encrypted with BCrypt encoder. If not, login for the inserted user wonn't be possible.

## How I recover server credentials?
1. Establish connection with the server (ssh)
2. Execute:
   ```shell
   sudo docker ps
   ```
3. Look for gessi-webapp container (port 1034)
4. Copy CONTAINER ID
5. Execute:
   ```shell
   sudo docker container exec -it CONTAINER_ID /bin/bash
   ```
6. Now you have opened a shell to docker container. Execute:
   ```shell
   more gessi-slr/WEB-INF/classes/application-prod.properties
   ```
7. Database user and password are spring.datasource.username and spring.datasource.password
   
