package com.example.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.entity.Income2;
import com.example.entity.Statistics;

@Service
public class IncomeService {

	private final JdbcTemplate jdbcTemplate;

	public IncomeService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// SQLクエリ定数
	private static final String SELECT_INCOME_BY_MONTH = "SELECT * FROM INCOME2 WHERE DATE_FORMAT(INCOME_DATE, '%Y-%m') = ? ORDER BY INCOME_DATE ASC";
	private static final String SELECT_INCOME_BY_YEAR = "SELECT * FROM INCOME2 WHERE DATE_FORMAT(INCOME_DATE, '%Y') = ? ORDER BY INCOME_DATE ASC";
	private static final String SELECT_ALL_INCOME = "SELECT * FROM INCOME2 ORDER BY INCOME_DATE ASC";
	private static final String INSERT_INCOME = "INSERT INTO INCOME2 (INCOME_DATE, INCOME_NAME, INCOME_COUNT) VALUES (?, ?, ?)";
	private static final String UPDATE_INCOME_DATE = "UPDATE INCOME2 SET INCOME_DATE = ? WHERE INCOME_NAME = ?";
	private static final String UPDATE_INCOME_NAME = "UPDATE INCOME2 SET INCOME_NAME = ? WHERE INCOME_NAME = ?";
	private static final String UPDATE_INCOME_COUNT = "UPDATE INCOME2 SET INCOME_COUNT = ? WHERE INCOME_NAME = ?";
	private static final String DELETE_INCOME_BY_NAME = "DELETE FROM INCOME2 WHERE INCOME_NAME = ?";

	// 収入データを月ごとに取得
	public List<Income2> getAll(String formattedDate) {
		try {
			return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
		} catch (DataAccessException e) {
			System.err.println("Error occurred while retrieving income data: " + e.getMessage());
			throw e;
		}
	}

	// 全ての統計データを取得
	public Statistics getStatisticsAll(Income2 income2) {
		try {
			List<Income2> incomesByYear = queryIncome(SELECT_INCOME_BY_YEAR, income2.getIncomeDate());
			List<Income2> allIncomes = queryIncome(SELECT_ALL_INCOME);

			Map<String, Integer> MonthlyIncomeTotals = getMonthlyIncomeTotals(incomesByYear);

			return new Statistics(
					getIncomeTotal(allIncomes),
					(int) Math.round(getIncomeAverage(MonthlyIncomeTotals)), MonthlyIncomeTotals);
		} catch (DataAccessException e) {
			System.err.println("Error occurred while calculating statistics: " + e.getMessage());
			throw e;
		}
	}

	// 月ごとの収入を集計
	public Map<String, Integer> getMonthlyIncomeTotals(List<Income2> incomeList) {
		return incomeList.stream()
				.collect(Collectors.groupingBy(
						income -> income.getIncomeDate().substring(0, 7),
						Collectors.summingInt(Income2::getIncomeCount)));
	}

	// 合算(総資産)を計算
	public Integer getIncomeTotal(List<Income2> incomeList) {
		return incomeList.stream()
				.collect(Collectors.summingInt(Income2::getIncomeCount));
	}

	// 月毎の収入の平均値を計算
	public Double getIncomeAverage(Map<String, Integer> MonthlyIncomeTotals) {
		return MonthlyIncomeTotals.values().stream()
				.mapToInt(Integer::intValue)
				.average()
				.orElse(0.0);
	}

	// 新しい収入データを追加
	public List<Income2> addOne(String formattedDate, Income2 income2) {
		try {
			if (isDuplicateIncome(income2.getIncomeName())) {
				return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
			}
			jdbcTemplate.update(INSERT_INCOME, income2.getIncomeDate(), income2.getIncomeName(),
					income2.getIncomeCount());
			return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
		} catch (DataAccessException e) {
			System.err.println("Error occurred while adding income data: " + e.getMessage());
			throw e;
		}
	}

	// 収入データを更新
	public List<Income2> setOne(int count, String formattedDate, String modalDate, String modalName, int modalCount,
			Income2 income2) {
		try {
			if (isDuplicateIncome(modalName)) {
				return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
			}

			switch (count) {
			case 1:
				jdbcTemplate.update(UPDATE_INCOME_DATE, modalDate, income2.getIncomeName());
				break;
			case 2:
				jdbcTemplate.update(UPDATE_INCOME_NAME, modalName, income2.getIncomeName());
				break;
			default:
				jdbcTemplate.update(UPDATE_INCOME_COUNT, modalCount, income2.getIncomeName());
				break;
			}
			return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
		} catch (DataAccessException e) {
			System.err.println("Error occurred while updating income data: " + e.getMessage());
			throw e;
		}
	}

	// 収入データを削除
	public List<Income2> deleteOne(String formattedDate, Income2 income2) {
		try {
			jdbcTemplate.update(DELETE_INCOME_BY_NAME, income2.getIncomeName());
			return queryIncome(SELECT_INCOME_BY_MONTH, formattedDate);
		} catch (DataAccessException e) {
			System.err.println("Error occurred while deleting income data: " + e.getMessage());
			throw e;
		}
	}

	// 汎用的なクエリ実行メソッド
	@SuppressWarnings("deprecation")
	private List<Income2> queryIncome(String sql, Object... params) {
		try {
			return jdbcTemplate.query(sql, params, new IncomeRowMapper());
		} catch (DataAccessException e) {
			System.err.println("Error occurred during query execution: " + e.getMessage());
			throw e;
		}
	}

	// 重複チェック処理を共通化
	private boolean isDuplicateIncome(String incomeName) {
		try {
			List<Income2> income2s = queryIncome(SELECT_ALL_INCOME);
			return income2s.stream().anyMatch(income -> income.getIncomeName().equals(incomeName));
		} catch (DataAccessException e) {
			System.err.println("Error occurred while checking for duplicate income: " + e.getMessage());
			throw e;
		}
	}

	// RowMapperの実装
	private static class IncomeRowMapper implements RowMapper<Income2> {
		public Income2 mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
			return new Income2(
					rs.getString("INCOME_DATE"),
					rs.getString("INCOME_NAME"),
					rs.getInt("INCOME_COUNT"));
		}
	}
}
