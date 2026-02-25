com.dmyk.model

Класи: Currency, ExchangeRate.

Що це: Прості Java-об'єкти (POJO), які описують структуру даних (поля ID, Code, Name тощо).

com.dmyk.dao (Data Access Object)

Класи: CurrencyDAO, ExchangeRateDAO.

Що це: Логіка роботи з базою даних. Тут лежать SQL-запити (SELECT, INSERT, UPDATE).

com.dmyk.controller (або com.dmyk.servlet)

Класи: CurrenciesServlet, ExchangeRatesServlet, ExchangeServlet.

Що це: Сервлети, які приймають HTTP-запити (GET, POST, PATCH), викликають DAO та відправляють JSON-відповідь.

com.dmyk.dto (Data Transfer Object)

Класи: ExchangeResponseDTO, ErrorResponseDTO.

Що це: Спеціальні класи для "красивих" JSON-відповідей, які можуть відрізнятися від таблиць у БД (наприклад, для методу /exchange).

com.dmyk.utils

Класи: DataSource.

Що це: Допоміжні класи (підключення до БД через HikariCP, конфігурації).