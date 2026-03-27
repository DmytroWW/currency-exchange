import java.sql.Connection;
import java.sql.SQLException;

import com.dmyk.dao.CurrencyDAO;
import com.dmyk.model.Currency;
import com.dmyk.utils.DataSource;

public class TestDAO {
	public static void main(String[] args) {
		try (Connection connection = DataSource.getConnection()) {
			CurrencyDAO dao = new CurrencyDAO(connection);

			Currency newCurrency = new Currency(0, "GBP", "British Pound", "£");
			Currency created = dao.create(newCurrency);

			System.out.println("Валюту додано з ID: " + created.getId());

			var allCurrencies = dao.findAll();
			System.out.println("Список всіх валют: " + allCurrencies.size());

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}