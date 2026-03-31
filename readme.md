# 💹 Currency Exchange Project: Final Sprint

## 🚩 Поточний статус
* **DAO**: `CurrencyDAO` та `ExchangeRateDAO` повністю готові. Реалізовано CRUD-операції та `updateByCodes` (PATCH).
* **Servlets**: Ендпоінти `/currencies`, `/currency/*`, `/exchangeRates`, `/exchangeRate/*` працюють.
* **Infrastructure**: SQLite підключено через `DataSource`, налаштовано GSON та парсинг `x-www-form-urlencoded`.

---

## 🎯 Наступний крок: Ендпоінт `/exchange`
Потрібно реалізувати калькулятор для конвертації валют на основі наявних курсів у базі.

### 📝 Технічне завдання
**Запит:** `GET /exchange?from=USD&to=UAH&amount=10`

#### Логіка пошуку курсу (3 сценарії):
1.  **Прямий курс ($A \to B$)**: 
    * Шукаємо пару `USDUAH` в таблиці `ExchangeRates`.
    * Якщо знайдено: $Result = amount \times rate$.
2.  **Зворотний курс ($B \to A$)**: 
    * Якщо `AB` немає, шукаємо `UAHUSD`.
    * Розраховуємо зворотний курс: $rate_{AB} = 1 / rate_{BA}$.
3.  **Крос-курс через USD ($USD \to A$ та $USD \to B$)**: 
    * Якщо прямих зв'язків немає, шукаємо курси обох валют відносно долара.
    * Формула: $$Rate(A \to B) = \frac{Rate(USD \to B)}{Rate(USD \to A)}$$

---

## 🛠 To-Do List (Завдання на завтра)

### 1. Model (DTO)
Створити клас `ExchangeResponse` для формування фінального JSON:
* `Currency baseCurrency`
* `Currency targetCurrency`
* `BigDecimal rate`
* `BigDecimal amount`
* `BigDecimal convertedAmount`

### 2. DAO Layer
Додати в `ExchangeRateDAO` метод `getExchangeRate(String from, String to)`:
* [ ] Реалізувати пошук за сценарієм №1.
* [ ] Реалізувати розрахунок за сценарієм №2 (використовувати `RoundingMode.HALF_UP`).
* [ ] Реалізувати розрахунок за сценарієм №3 (крос-курс через USD).

### 3. Servlet Layer
Створити `ExchangeServlet` (мапінг `/exchange`):
* [ ] Отримати параметри `from`, `to`, `amount` через `req.getParameter()`.
* [ ] Валідувати дані (наявність параметрів, формат числа).
* [ ] Викликати DAO логіку та відправити JSON-відповідь.

---

## 💡 Важливі технічні деталі
* **Математика**: Тільки `BigDecimal`. Для ділення обов'язково вказувати `scale` (мінімум 6 знаків) та `RoundingMode.HALF_UP`.
* **Помилки**: Якщо курс неможливо вирахувати жодним способом — повернути **404** з повідомленням `{"message": "Exchange rate not found"}`.
* **Кодування**: Не забути `resp.setCharacterEncoding("UTF-8")` для коректного відображення символів валют.