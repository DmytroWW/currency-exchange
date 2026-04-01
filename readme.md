У класі DataSource переконайся, що метод getConnection() повертає ds.getConnection() від Hikari.

Перепиши ExchangeRateDAO та CurrencyDAO, щоб вони самі викликали DataSource.getConnection() всередині try.

Зроби конструктори DAO та Service приватними (або просто не передавай туди Connection).