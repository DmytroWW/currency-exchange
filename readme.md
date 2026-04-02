# Currency Exchange API

A robust Java-based RESTful API for managing currency data and performing real-time currency conversions. This project implements a classic N-tier architecture using Jakarta Servlets, JDBC with HikariCP, and SQLite.

---

## 🛠 Tech Stack

* **Java 21+** (Configured for Java 21/25)
* **Jakarta Servlet 6.0** (Web Layer)
* **SQLite JDBC** (Database)
* **HikariCP** (High-performance Connection Pooling)
* **Gson** (JSON Serialization/Deserialization)
* **Maven** (Dependency Management & Build)

---

## 🌟 Key Features

* **Smart Conversion Logic**: The `ExchangeService` automatically calculates rates using three different strategies:
    1.  **Direct Rate**: Uses the rate stored in the database (e.g., USD to EUR).
    2.  **Reverse Rate**: Calculates the inverse if only the opposite pair exists (e.g., EUR to USD = $1 / rate$).
    3.  **Cross-rate via USD**: Calculates the rate between two currencies if they both have a rate defined against USD.
* **N-tier Architecture**: Clear separation of concerns between Controllers, Services, DAOs, and Models.
* **Resilient Error Handling**: Centralized exception management through a base servlet and custom `DatabaseException`.

---

## 📡 API Endpoints

### Currencies
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/currencies` | Retrieves a list of all currencies. |
| `GET` | `/currency/{code}` | Retrieves a specific currency by its 3-letter code (e.g., `/currency/USD`). |
| `POST` | `/currencies` | Adds a new currency. (Form params: `name`, `code`, `sign`). |

### Exchange Rates
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/exchangeRates` | Retrieves all available exchange rates. |
| `GET` | `/exchangeRate/{pair}` | Retrieves a rate for a specific pair (e.g., `/exchangeRate/USDEUR`). |
| `POST` | `/exchangeRates` | Adds a new rate. (Form params: `baseCurrencyCode`, `targetCurrencyCode`, `rate`). |
| `PATCH` | `/exchangeRate/{pair}` | Updates an existing rate. (Form body: `rate=...`). |

### Currency Exchange
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/exchange` | Performs currency conversion based on query parameters. |

**Example request:** `GET /exchange?from=USD&to=UAH&amount=100.50`

---

## 🏗 Project Structure

* **`controller`**: Handles HTTP requests, parses parameters, and returns JSON responses.
* **`service`**: Contains business logic, including `ExchangeService` for conversion algorithms and `DTOMapper`.
* **`dao`**: Data Access Object layer for direct SQLite interaction.
* **`model`**: Database entities (Currency, ExchangeRate).
* **`dto`**: Objects optimized for JSON transfer.
* **`utils`**: Database configuration (`DataSource`) and core interfaces.

---

## ⚙️ Setup and Installation

### 1. Database Configuration
The project uses SQLite. You need to ensure the database file exists and the path is correctly set in `com.dmyk.utils.DataSource.java`:

```java
config.setJdbcUrl("jdbc:sqlite:C:/path/to/your/db/currency.db");
```

### 2. Build the Project
Use Maven to compile and package the project into a `.war` file:

```bash
mvn clean package
```

### 3. Deployment
1.  Ensure you have a Jakarta Servlet compatible container (like **Tomcat 10+**).
2.  Deploy the `CurrencyExchange.war` file found in the `target` folder.
3.  The API will be available at `http://localhost:8080/CurrencyExchange/`.

---

## ⚠️ Important Note on PATCH
Since standard HTML forms and some servlet containers do not natively support `application/x-www-form-urlencoded` for **PATCH** requests, this project includes a custom `parseFormBody` method in the `BaseServlet` to manually parse the request body.

---
