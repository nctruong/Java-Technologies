package com.csc.training.hibernate;

import java.util.Date;

import org.hibernate.Session;

import com.csc.training.hibernate.model.Stock;
import com.csc.training.hibernate.model.StockDailyRecord;
import com.csc.training.hibernate.util.HibernateUtil;

public class MainApp {

	public static void main(String[] args) {
		// Get Session
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		// start transaction
		session.beginTransaction();

		// Save the Model object
		Stock stock = new Stock();
        stock.setStockCode("7052");
        stock.setStockName("PADINI");
        session.save(stock);
        
        StockDailyRecord stockDailyRecords = new StockDailyRecord();
        stockDailyRecords.setPriceOpen(new Float("1.2"));
        stockDailyRecords.setPriceClose(new Float("1.1"));
        stockDailyRecords.setPriceChange(new Float("10.0"));
        stockDailyRecords.setVolume(3000000L);
        stockDailyRecords.setDate(new Date());
        
        stockDailyRecords.setStock(stock);        
        stock.getStockDailyRecords().add(stockDailyRecords);

        session.save(stockDailyRecords);

		// Commit transaction
		session.getTransaction().commit();

		// terminate session factory, otherwise program won't end
		HibernateUtil.getSessionFactory().close();

	}

}
