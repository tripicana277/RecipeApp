package com.example.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.entity.Income;
import com.example.entity.Statistics;

@Service
public class IncomeService {

	private final JdbcTemplate jdbcTemplate;

	public IncomeService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// SQLクエリ定数
	private static final String SELECT_INCOME_BY_MONTH = "SELECT * FROM INCOME WHERE DATE_FORMAT(INCOME_DATE, '%Y-%m') = ? ORDER BY INCOME_DATE ASC";
	private static final String SELECT_INCOME_BY_YEAR = "SELECT * FROM INCOME WHERE DATE_FORMAT(INCOME_DATE, '%Y') = ? ORDER BY INCOME_DATE ASC";
	private static final String SELECT_ALL_INCOME = "SELECT * FROM INCOME ORDER BY INCOME_DATE ASC";
	private static final String INSERT_INCOME = "INSERT INTO INCOME (INCOME_DATE, INCOME_NAME, INCOME_COUNT) VALUES (?, ?, ?)";
	private static final String UPDATE_INCOME_DATE = "UPDATE INCOME SET INCOME_DATE = ? WHERE INCOME_NAME = ?";
	private static final String UPDATE_INCOME_NAME = "UPDATE INCOME SET INCOME_NAME = ? WHERE INCOME_NAME = ?";
	private static final String UPDATE_INCOME_COUNT = "UPDATE INCOME SET INCOME_COUNT = ? WHERE INCOME_NAME = ?";
	private static final String DELETE_INCOME_BY_NAME = "DELETE FROM INCOME WHERE INCOME_NAME = ?";

	// 収入データを月ごとに取得
	public List<Income> getAll(String formattedDate) {
		return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
	}

	// 全ての統計データを取得
	public Statistics getStatisticsAll(Income income) {
		List<Income> incomesByYear = queryIncome(SELECT_INCOME_BY_YEAR, income.getIncomeDate());
		List<Income> allIncomes = queryIncome(SELECT_ALL_INCOME);

		// 月毎の収入
		Map<String, Integer> MonthlyIncomeTotals = getMonthlyIncomeTotals(incomesByYear);

		return new Statistics(
				getIncomeTotal(allIncomes),
				(int) Math.round(getIncomeAverage(MonthlyIncomeTotals)), MonthlyIncomeTotals);
	}

	// 月ごとの収入を集計
	public Map<String, Integer> getMonthlyIncomeTotals(List<Income> incomeList) {
		return incomeList.stream()
				.collect(Collectors.groupingBy(
						income -> income.getIncomeDate().substring(0, 7),
						Collectors.summingInt(Income::getIncomeCount)));
	}

	// 合算(総資産)を計算
	public Integer getIncomeTotal(List<Income> incomeList) {
		return incomeList.stream()
				.collect(Collectors.summingInt(Income::getIncomeCount));
	}

	// 月毎の収入の平均値を計算
	public Double getIncomeAverage(Map<String, Integer> MonthlyIncomeTotals) {
		return MonthlyIncomeTotals.values().stream()
				.mapToInt(Integer::intValue)
				.average()
				.orElse(0.0);
	}

	// 新しい収入データを追加
	public List<Income> addOne(String formattedDate, Income income) {
		if (isDuplicateIncome(income.getIncomeName())) {
			return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
		}
		jdbcTemplate.update(INSERT_INCOME, income.getIncomeDate(), income.getIncomeName(), income.getIncomeCount());
		return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
	}

	// 収入データを更新
	public List<Income> setOne(int count, String formattedDate, String modalDate, String modalName, int modalCount,
			Income income) {
		if (isDuplicateIncome(modalName)) {
			return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
		}

		switch (count) {
		case 1:
			jdbcTemplate.update(UPDATE_INCOME_DATE, modalDate, income.getIncomeName());
			break;
		case 2:
			jdbcTemplate.update(UPDATE_INCOME_NAME, modalName, income.getIncomeName());
			break;
		default:
			jdbcTemplate.update(UPDATE_INCOME_COUNT, modalCount, income.getIncomeName());
			break;
		}
		return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
	}

	// 収入データを削除
	public List<Income> deleteOne(String formattedDate, Income income) {
		jdbcTemplate.update(DELETE_INCOME_BY_NAME, income.getIncomeName());
		return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
	}

	// 汎用的なクエリ実行メソッド(非推奨のメソッドを使用。
	// 1.可変長引数 (Object... args)の可読性が低い
	// 2.?に対しObject型の型推測が正しく行われず不一致になる恐れがある)
	@SuppressWarnings("deprecation")
	private List<Income> queryIncome(String sql, Object... params) {
		return jdbcTemplate.query(sql, params, new IncomeRowMapper());
	}

	// 重複チェック処理を共通化
	private boolean isDuplicateIncome(String incomeName) {
		List<Income> incomes = queryIncome(SELECT_ALL_INCOME);
		return incomes.stream().anyMatch(income -> income.getIncomeName().equals(incomeName));
	}

	// RowMapperの実装(rowNumは現在の行番号)
	private static class IncomeRowMapper implements RowMapper<Income> {
		public Income mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
			return new Income(
					rs.getString("INCOME_DATE"),
					rs.getString("INCOME_NAME"),
					rs.getInt("INCOME_COUNT"));
		}
	}
}
