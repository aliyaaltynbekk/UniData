# UniData - Система управления данными учебного заведения

![Java CI with Maven](https://github.com/aliyaaltynbekk/unidata/workflows/Java%20CI%20with%20Maven/badge.svg)

Система управления данными учебного заведения на базе Spring Boot с поддержкой обмена сообщениями, управления пользователями и ролями.

## Технологический стек

- Java 23
- Spring Boot 3.2.2
- Spring Security
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- Maven

## Особенности

- Авторизация и аутентификация пользователей
- Управление ролями и правами доступа
- Система обмена сообщениями между пользователями
- Шифрование сообщений
- Панель администратора для управления системой
- Поддержка многоязычности (Казахский, Русский)

## Установка и запуск

### Предварительные требования
- JDK 23
- Maven 3.8+
- PostgreSQL 14+

### Шаги установки

1. Клонируйте репозиторий
   ```bash
   git clone https://github.com/USERNAME/unidata.git
   cd unidata
   ```

2. Настройте базу данных PostgreSQL
   ```sql
   CREATE DATABASE unidata;
   CREATE USER unidata_user WITH PASSWORD 'unidata1';
   GRANT ALL PRIVILEGES ON DATABASE unidata TO unidata_user;
   ```

3. Настройте подключение к базе данных
   
   Создайте файл `src/main/resources/application-dev.properties` с параметрами подключения:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/unidata
   spring.datasource.username=unidata_user
   spring.datasource.password=unidata1
   ```

4. Соберите проект
   ```bash
   mvn clean package
   ```

5. Запустите приложение
   ```bash
   java -jar target/unidata-1.0.0.jar --spring.profiles.active=dev
   ```

6. Откройте приложение в браузере по адресу `http://localhost:8080`

## Разработка

### Настройка среды разработки

1. Импортируйте проект в IntelliJ IDEA или Eclipse
2. Настройте JDK 23
3. Запустите приложение в режиме разработки:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### Структура проекта
unidata/
├── src/
│   ├── main/
│   │   ├── java/org/unidata1/
│   │   │   ├── controller/          # Контроллеры REST API и MVC
│   │   │   ├── model/               # Модели данных
│   │   │   ├── repository/          # Репозитории для доступа к данным
│   │   │   ├── service/             # Бизнес-логика
│   │   │   ├── config/              # Конфигурации Spring
│   │   │   └── UniDataApplication.java  # Точка входа в приложение
│   │   └── resources/
│   │       ├── static/              # Статические ресурсы (CSS, JS)
│   │       ├── templates/           # Шаблоны Thymeleaf
│   │       └── application.properties  # Конфигурация приложения
│   └── test/                        # Тесты
├── pom.xml                          # Конфигурация Maven
└── README.md                        # Документация

## Вклад в проект

1. Создайте форк репозитория
2. Создайте ветку для вашей функциональности (`git checkout -b feature/amazing-feature`)
3. Внесите изменения и создайте коммиты (`git commit -m 'Add some amazing feature'`)
4. Отправьте изменения в ваш форк (`git push origin feature/amazing-feature`)
5. Создайте Pull Request в основной репозиторий

## Автор

Алия Алтынбек - [GitHub](https://github.com/aliyaaltynbekk)
