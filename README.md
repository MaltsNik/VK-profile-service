<img width="1771" height="700" alt="image" src="https://github.com/user-attachments/assets/0f5d4ab7-3fea-4125-b6c3-a9bb6c88608a" />

## RESTful cloud-native сервис для получения информации о пользователях ВКонтакте и проверки их членства в группах.

### Описание
Интеграционный микросервис на Java, который предоставляет REST API для работы с VK API.
Сервис позволяет получать ФИО пользователя по его ID и проверять его членство в указанной группе ВКонтакте.

### Основные возможности
- Получение профиля пользователя VK - имя, фамилия, отчество
- Проверка членства в группе - определение принадлежности пользователя к группе
- Кэширование запросов - повышение производительности через Caffeine Cache
- Basic Authentication - защита API c помощью базовой аутентификации
- Валидация входных данных - проверка корректности запросов
- Обработка ошибок - сообщения об ошибках VK API
- OpenAPI документация - документация Swagger UI
- Health checks - мониторинг состояния сервиса

### Технологический стек
- **Java 17**
- **Spring Boot** - основной фреймворк
- **Apache Camel** - интеграционное взаимодействие
- **Caffeine** - кэширование
- **Spring Security** - Basic Auth
- **JUnit 5 & Mockito** - тестирование
- **SpringDoc OpenAPI** - документация API
- **Docker & Kubernetes (Minikube)** - контейнеризация и деплой

## API Endpoint

### POST `/api/v1/user/profile`


**Headers:**
- `vk_service_token` - Сервисный токен VK
- `Authorization` - Basic Auth 

**Request:**
```json
{
  "user_id": 68779302,
  "group_id": "public93559769"
}
```

**Response:**
```json
{
  "last_name": "Сидоров",
  "first_name": "Сергей",
  "middle_name": "Петрович",
  "member": true
}

