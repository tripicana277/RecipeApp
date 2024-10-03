package com.example.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entity.Components;
import com.example.entity.Income;
import com.example.entity.Statistics;
import com.example.service.IncomeLogic;
import com.example.service.IncomeService;

import lombok.Data;

@Data
@Controller
public class MainController {

	private final IncomeService incomeService;
	private final IncomeLogic incomeLogic;
	private final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
	private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

	@Autowired
	private Components userSession;

	public MainController(IncomeService incomeService) {
		this.incomeService = incomeService;
		this.incomeLogic = new IncomeLogic(incomeService); // ロジックを1回だけインスタンス化
	}

	@GetMapping("/getIncome/{id}")
	public String getIncome(@PathVariable("id") int id, Model model) throws SQLException {
		LocalDate localDate = getSessionAdjustedDate(id, 2, false);
//		String formattedDate = localDate.format(yearMonthFormatter);

//		List<Income> incomes = incomeLogic.new GetIncomeListLogic(formattedDate).execute();
//		model.addAttribute("incomes", incomes);
		model.addAttribute("localDate", localDate);

		return "Income";
	}

	@GetMapping("/getStatistics/{id}")
	public String getStatistics(@PathVariable("id") int id, Model model) throws SQLException {
		LocalDate localDate = getSessionAdjustedDate(id, 2, true);
		String formattedDate = localDate.format(yearFormatter);

		Statistics statistics = incomeLogic.new GetStatisticsLogic(new Income(formattedDate, null, 0)).execute();

		model.addAttribute("allAsset", statistics.getAllAsset() + userSession.getStandardAsset());
		model.addAttribute("averageIncome", statistics.getAverageIncome());
		model.addAttribute("treeMapMonthCount", statistics.getTreeMapMonthCount());
		model.addAttribute("localDate", localDate);

		return "Statistics";
	}

	@GetMapping("/add/{id}")
	public String addIncome(@PathVariable("id") int id, 
	                        @RequestParam("date") String date, 
	                        @RequestParam("name") String name, 
	                        @RequestParam("count") int count, 
	                        Model model) throws SQLException {

		LocalDate localDate = getSessionAdjustedDate(id, 2, false);
		String formattedDate = localDate.format(yearMonthFormatter);

		List<Income> incomes = incomeLogic.new AddIncomeLogic(formattedDate, new Income(date, name, count)).execute();
		model.addAttribute("incomes", incomes);
		model.addAttribute("localDate", localDate);

		return "Income";
	}

	@GetMapping("/set/{id}/{count}/{incomeDate}/{incomeName}/{incomeCount}")
	public String setIncome(@PathVariable("id") int id, 
	                        @PathVariable("count") int count, 
	                        @RequestParam(value = "modalDate", required = false, defaultValue = "") String modalDate,
	                        @RequestParam(value = "modalName", required = false, defaultValue = "") String modalName,
	                        @RequestParam(value = "modalCount", required = false, defaultValue = "0") int modalCount, 
	                        @PathVariable("incomeDate") String incomeDate, 
	                        @PathVariable("incomeName") String incomeName, 
	                        @PathVariable("incomeCount") int incomeCount, 
	                        Model model) throws SQLException {

		LocalDate localDate = getSessionAdjustedDate(id, 2, false);
		String formattedDate = localDate.format(yearMonthFormatter);

		List<Income> incomes = incomeLogic.new SetIncomeLogic(count, formattedDate, modalDate, modalName, modalCount, 
		                                                     new Income(incomeDate, incomeName, incomeCount)).execute();
		model.addAttribute("incomes", incomes);
		model.addAttribute("localDate", localDate);

		return "Income";
	}

	@GetMapping("/delete/{id}")
	public String deleteIncome(@PathVariable("id") int id, 
	                           @RequestParam("incomeName") String name, 
	                           Model model) throws SQLException {

		LocalDate localDate = getSessionAdjustedDate(id, 2, false);
		String formattedDate = localDate.format(yearMonthFormatter);

		List<Income> incomes = incomeLogic.new DeleteIncomeLogic(formattedDate, new Income(formattedDate, name, 0)).execute();
		model.addAttribute("incomes", incomes);
		model.addAttribute("localDate", localDate);

		return "Income";
	}

	// セッションの日時を調整
	private LocalDate getSessionAdjustedDate(int id, int baseId, boolean adjustByYear) {
		LocalDate localDate = LocalDate.now();
		if (id != baseId) {
			localDate = adjustByYear ? userSession.getDate().plusYears(id - baseId) : userSession.getDate().plusMonths(id - baseId);
		}
		userSession.setDate(localDate);
		return localDate;
	}
}
