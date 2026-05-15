# 🎮 GameStore

Веб-приложение интернет-магазина видеоигр. Учебный проект на **Spring Boot 3 + Thymeleaf + PostgreSQL**.

## ✨ Возможности

- 📋 Каталог игр с поиском по названию/жанру/разработчику и фильтром по жанрам
- 🤖 «AI-поиск» — рекомендации по описанию запроса (ключевые слова + синонимы, без LLM)
- 🛒 Корзина и оформление покупки
- 📚 Личная библиотека купленных игр
- 👤 Регистрация и авторизация (Spring Security + BCrypt)
- ⚙ Админ-панель: добавление/удаление игр
- 🎨 Адаптивный дизайн в тёмной игровой стилистике

## 🛠 Стек

| Слой           | Технология                          |
|----------------|--------------------------------------|
| Язык           | Java 21                              |
| Backend        | Spring Boot 3.3.5                    |
| Web            | Spring MVC, Thymeleaf                |
| Безопасность   | Spring Security 6 (BCrypt)           |
| ORM / БД       | Spring Data JPA + Hibernate + PostgreSQL 16 |
| Сборка         | Maven                                |
| Утилиты        | Lombok, Spring DevTools              |

## 🚀 Запуск

### 1. Поднять PostgreSQL

Из корня проекта:

```bash
docker compose up -d
```

Это запустит контейнер `gamestore-postgres` на порту **5432** с базой `gamestore`,
пользователем `admin` и паролем `secret`.

> Если Docker нет — создай в локальном Postgres базу `gamestore`
> с такими же credentials или поменяй `src/main/resources/application.properties`.

### 2. Запустить приложение

```bash
./mvnw spring-boot:run
```

Открыть в браузере: **http://localhost:8080**

### 3. Тестовые аккаунты

| Роль   | Логин   | Пароль     |
|--------|---------|------------|
| Админ  | admin   | admin123   |
| Игрок  | player1 | player123  |

Тестовые данные подгружаются автоматически при первом запуске
(см. `config/DataInitializer.java`).

## 📁 Структура

```
src/main/
├── java/com/university/gamestore/
│   ├── GameStoreApplication.java   ← точка входа
│   ├── config/
│   │   ├── DataInitializer.java    ← начальные данные
│   │   └── SecurityConfig.java     ← правила доступа, BCrypt
│   ├── controller/
│   │   ├── AdminController.java    ← /admin/**
│   │   ├── AuthController.java     ← /login, /register
│   │   ├── CartController.java     ← /cart, /profile, /library
│   │   └── GameController.java     ← /, /games, /ai-search
│   ├── model/
│   │   ├── Game.java               ← @Entity
│   │   ├── Order.java
│   │   └── User.java
│   ├── repository/                 ← JpaRepository
│   └── service/
│       ├── AiService.java          ← поиск по ключевым словам
│       ├── GameService.java
│       ├── UserDetailsServiceImpl.java
│       └── UserService.java
└── resources/
    ├── application.properties
    └── templates/
        ├── fragments/layout.html   ← общий навбар + стили
        ├── admin/                  ← панель + форма
        ├── auth/                   ← логин + регистрация
        ├── cart/                   ← корзина + успех покупки
        ├── games/                  ← каталог + детали + AI-поиск
        ├── profile/                ← профиль + библиотека
        └── index.html              ← главная
```

## 🤖 Про «AI-поиск»

Без вызова внешних LLM (быстро, дёшево, работает офлайн).
Алгоритм (см. `service/AiService.java`):

1. Запрос пользователя токенизируется, выбрасываются стоп-слова
2. Каждый токен расширяется по словарю синонимов
   (например, «расслабляющая» → ищем «симулятор», «инди», «ферм»)
3. Для каждой игры считается вес совпадений в полях
   `title` × 5, `genre` × 4, `developer` × 3, `description` × 2
4. Если в запросе есть «бесплатно» — даём бонус бесплатным играм и штрафуем платные
5. Топ-N результатов с весом > 0 возвращаются пользователю

## 🔐 Безопасность

- Пароли хэшируются BCrypt'ом
- CSRF отключён (учебный проект; для прода включить)
- Маршруты разделены на публичные / для пользователя / для админа
- Авторизация формой, сессии хранятся в куке

## 🧪 Что можно ещё улучшить

- Включить CSRF, добавить токены в формы
- Покрытие тестами (есть только заготовка `GamestoreApplicationTests`)
- Профили `dev` / `prod` с разными настройками БД
- Загрузка обложек как файлов, а не URL
- Постраничная навигация в каталоге
- Список заказов в профиле
- Топ игр по реальному количеству покупок, а не по `rating`
