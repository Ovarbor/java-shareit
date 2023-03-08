## Shareit - backend service for sharing items.

Shareit allows you to book item for certain dates, close access to items at the time of booking,
add new item to share on request, if the desired item is not available.

### Functional:

1. User can add new items and will be considered its owner, also can add name, description to this item.
2. You can search for item you need. The item is booked for certain dates. The owner of the item must confirm the booking.
3. If the desired item is not found in the search, the user can create a request. The other users can add desire item.
4. After using the item, users can leave feedback.

**Структура проекта:** gateway (контроллер, который осуществляет валидацию входных данных) + server (серверная часть приложения).

**Стек технологий:** Java 11, Spring Boot, Spring Data, Spring Rest Template, PostgreSQL, Docker, Lombok, Mapstruct, Spring AOP, JUnit, Mockito.

**Инструкция по развертыванию проекта:**
1. Загрузить zip-файл c github
2. Распаковать zip-файл
3. Открыть проект в IntellijIdea
4. mvn clean package
5. docker-compose up

### Планирую доработать приложение, реализовав следующую функциональность:
Составление рейтинга пользователей.
