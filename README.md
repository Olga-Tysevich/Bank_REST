# BankCards System

Система управления банковскими картами с функциональностью аутентификации, авторизации и безопасных переводов между картами.

## Используемые технологии

* **Java 21**
* **Spring Boot 3.4.5**
* **PostgreSQL**
* **Redis**
* **JWT для аутентификации**
* **Liquibase для миграций базы данных**
* **Spring Security**
* **MapStruct для маппинга DTO**
* **Docke**
* **Swagger для документации API**

## Как запустить проект

### 1. Клонировать репозиторий

```bash
git clone -b master git@github.com:Olga-Tysevich/Bank_REST.git
```

### 2. Настроить переменные окружения

Создайте файл `.env` на основе файла `.env-example`, который находится в корне проекта.
Создайте файл `.env.test` на основе файла `.env-example`, который находится в корне проекта.

* Сгенерируйте JWT ключи. Вы можете сделать это на [jwt-keys.21no.de](https://jwt-keys.21no.de/) или любым другим способом.
* Установите срок действия ключей и запишите их в `.env` файл.
* Укажите настройки для базы данных, Redis.

Пример `.env`:

```env
JWT_SECRET_KEY=your_jwt_secret_key
JWT_EXPIRATION_TIME=3600
DATABASE_URL=jdbc:postgresql://localhost:5432/bankcards_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=yourpassword
REDIS_HOST=localhost
REDIS_PORT=6379
```

### 3. Запуск зависимостей через Docker Compose

Убедитесь, что у вас установлен Docker и Docker Compose.

1. Для запуска приложения, PostgreSQL, Redis выполните команду:

*аплик без докер-контейнера:*
- Установить св-во IS_DOCKERIZED в env в false, запустить контейнеры БД и редис, запустить приложение любым удобным способом
```bash

docker-compose up --build bank_rest_db bank-rest-redis

```

*либо аплик с докер-контейнером:*
- Установить св-во IS_DOCKERIZED в env в true
```bash

docker-compose up --build

```
*Если нужны предварительные данные в БД то в application.yml должен быть установлен профиль dev*

### 4. База данных и миграции

Проект использует **Liquibase** для миграций базы данных. Миграции будут автоматически применяться при старте приложения.

### 5. Доступ к API

После того как сервис запущен, API будет доступно по адресу:

```plaintext
http://localhost:8080
```

Вы можете использовать Swagger для просмотра и тестирования API. Документация будет доступна по следующему URL:

```plaintext
http://localhost:8080/swagger-ui/index.html
```

### 6. Тестирование

Для тестирования функциональности проекта мы используем **JUnit** и **Testcontainers**.

Чтобы запустить все тесты, выполните команду:

```bash
./mvnw test
```
