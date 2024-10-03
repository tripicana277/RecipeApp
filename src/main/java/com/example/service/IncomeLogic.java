package com.example.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.entity.Income2;
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
    public class GetIncomeListLogic implements IncomeOperation<List<Income2>> {
        private final String formattedDate;

        @Override
        public List<Income2> execute() throws SQLException {
//        	return null;
        	return incomeService.getAll(formattedDate);
        }
    }

    @RequiredArgsConstructor
    public class GetStatisticsLogic implements IncomeOperation<Statistics> {
        private final Income2 income2;

        @Override
        public Statistics execute() throws SQLException {
//        	return null;
            return incomeService.getStatisticsAll(income2);
        }
    }

    @RequiredArgsConstructor
    public class AddIncomeLogic implements IncomeOperation<List<Income2>> {
        private final String formattedDate;
        private final Income2 income2;

        @Override
        public List<Income2> execute() throws SQLException {
//        	return null;
            return incomeService.addOne(formattedDate, income2);
        }
    }

    @RequiredArgsConstructor
    public class SetIncomeLogic implements IncomeOperation<List<Income2>> {
        private final int count;
        private final String formattedDate;
        private final String modalDate;
        private final String modalName;
        private final int modalCount;
        private final Income2 income2;

        @Override
        public List<Income2> execute() throws SQLException {
//        	return null;
        	return incomeService.setOne(count, formattedDate, modalDate, modalName, modalCount, income2);
        }
    }

    @RequiredArgsConstructor
    public class DeleteIncomeLogic implements IncomeOperation<List<Income2>> {
        private final String formattedDate;
        private final Income2 income2;

        @Override
        public List<Income2> execute() throws SQLException {
//        	return null;
            return incomeService.deleteOne(formattedDate, income2);
        }
    }
}
