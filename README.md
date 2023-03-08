## Shareit - backend service for sharing items.

Shareit allows you to book item for certain dates, close access to items at the time of booking,
add new item to share on request, if the desired item is not available.

### Functional:

1. User can add new items and will be considered its owner, also can add name, description to this item.
2. You can search for item you need. The item is booked for certain dates. The owner of the item must confirm the booking.
3. If the desired item is not found in the search, the user can create a request. The other users can add desire item.
4. After using the item, users can leave feedback.

### Project structure: 
gateway (controller, validates input data) + server (main logic).
![Schema DB](server/src/main/resources/schema.png)

### Technology stack:
<a href="https://spring.io/">
  <img src="server/src/main/resources/logos/Spring.png" title="Spring" alt="Spring" width="80" height="40"/>&nbsp;
</a>
<a href="https://maven.apache.org/">
  <img src="server/src/main/resources/logos/Maven.png" title="Maven" alt="Maven" width="80" height="40"/>&nbsp;
</a>
<a href="https://www.postgresql.org/">
  <img src="server/src/main/resources/logos/Postgresql.png" title="postgreSQL" alt="postgreSQL" width="40" height="40"/>&nbsp;
</a>
<a href="https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa">
  <img src="server/src/main/resources/logos/JPA.png" title="JPA" alt="JPA" width="70" height="40"/>&nbsp;
</a>
<a href="https://hibernate.org/">
  <img src="server/src/main/resources/logos/Hibernate.png" title="Hibernate" alt="Hibernate" width="70" height="40"/>&nbsp;
</a>
<a href="https://projectlombok.org/">
  <img src="server/src/main/resources/logos/Lombok.png" title="Lombok" alt="Lombok" width="40" height="40"/>&nbsp;
</a>


[//]: # (<a href="https://junit.org/junit5/">)

[//]: # (  <img src="server/src/main/resources/logos/JUnit.png" title="JUnit" alt="JUnit" width="40" height="40"/>&nbsp;)

[//]: # (</a>)

<a href="https://site.mockito.org/">
  <img src="server/src/main/resources/logos/Mockito.png" title="Mockito" alt="Mockito" width="70" height="40"/>&nbsp;
</a>
<a href="https://www.docker.com/">
  <img src="server/src/main/resources/logos/Docker.png" title="Docker" alt="Docker" width="120" height="40"/>&nbsp;
</a>

<a href="https://mapstruct.org/">
  <img src="server/src/main/resources/logos/Mapstruct.png" title="Mapstruct" alt="Mapstruct"/>&nbsp;
</a>

### System requirements:
JDK 11 amazon corretto  
IntellijIdea

### Startup instructions:
1. Download zip-file  
2. Unpack zip-файл  
3. Open app in IntellijIdea  
4. mvn clean package
5. docker-compose up