package com.example.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.entity.Income;
import com.example.entity.Statistics;

import lombok.RequiredArgsConstructor;

// 共通のインターフェースを定義し、ジェネリクスで任意のデータ型に設定
interface IncomeOperation<T> {
    T execute() throws SQLException;
}

// ロジックを共通化するクラス
@Service
@RequiredArgsConstructor
public class IncomeLogic {

    private final IncomeService incomeService;

    // 各ロジックを共通の構造にするためのクラス
    @RequiredArgsConstructor
    public class GetIncomeListLogic implements IncomeOperation<List<Income>> {
        private final String formattedDate;

        @Override
        public List<Income> execute() throws SQLException {
            return incomeService.getAll(formattedDate);
        }
    }

    @RequiredArgsConstructor
    public class GetStatisticsLogic implements IncomeOperation<Statistics> {
        private final Income income;

        @Override
        public Statistics execute() throws SQLException {
            return incomeService.getStatisticsAll(income);
        }
    }

    @RequiredArgsConstructor
    public class AddIncomeLogic implements IncomeOperation<List<Income>> {
        private final String formattedDate;
        private final Income income;

        @Override
        public List<Income> execute() throws SQLException {
            return incomeService.addOne(formattedDate, income);
        }
    }

    @RequiredArgsConstructor
    public class SetIncomeLogic implements IncomeOperation<List<Income>> {
        private final int count;
        private final String formattedDate;
        private final String modalDate;
        private final String modalName;
        private final int modalCount;
        private final Income income;

        @Override
        public List<Income> execute() throws SQLException {
            return incomeService.setOne(count, formattedDate, modalDate, modalName, modalCount, income);
        }
    }

    @RequiredArgsConstructor
    public class DeleteIncomeLogic implements IncomeOperation<List<Income>> {
        private final String formattedDate;
        private final Income income;

        @Override
        public List<Income> execute() throws SQLException {
            return incomeService.deleteOne(formattedDate, income);
        }
    }
}
