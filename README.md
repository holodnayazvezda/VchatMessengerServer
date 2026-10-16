# VchatMessengerServer

Сервер мессенджера Vchat на Java 17 и Spring Boot 3.2.1. Содержит пользователей и авторизацию, личные сообщения, группы, каналы, аватары и Socket.IO-шлюз событий. Данные сохраняются через Spring Data JPA в H2.

## Запуск

Требуется JDK 17. Из корня репозитория:

```sh
sh gradlew bootRun
```

В Windows: `gradlew.bat bootRun`.

- HTTP API по умолчанию слушает порт `3000`.
- Socket.IO использует отдельный порт `4000`.
- Файловая база H2 задаётся как `jdbc:h2:file:./data`.

HTTP-порт, база и ограничения загрузок настраиваются в `src/main/resources/application.properties`. Порт Socket.IO задаётся в `src/main/java/com/example/vchatmessengerserver/gateway/SocketIOConfig.java`.

## Структура

Исходники расположены в `src/main/java/com/example/vchatmessengerserver/`: `auth/` отвечает за доступ, `channel/` и остальные предметные модули — за REST-операции, `gateway/` — за события Socket.IO. Адреса HTTP и шлюза должны быть согласованы с конфигурацией Android-клиента Vchat.
