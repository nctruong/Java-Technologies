package com.csc.training.hibernate;

import java.util.Date;

import org.hibernate.Session;

import com.csc.training.hibernate.model.Stock;
import com.csc.training.hibernate.model.StockDetail;
import com.csc.training.hibernate.util.HibernateUtil;

public class MainApp {

	public static void main(String[] args) {
		// Get Session
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		// start transaction
		session.beginTransaction();

		// Save the Model object
		Stock stock = new Stock();
        
        stock.setStockCode("4715");
        stock.setStockName("GENM");
     
        StockDetail stockDetail = new StockDetail();
        stockDetail.setCompName("GENTING Malaysia");
        stockDetail.setCompDesc("Best resort in the world");
        stockDetail.setListedDate(new Date());
        
        stock.setStockDetail(stockDetail);
        stockDetail.setStock(stock);
        
        session.save(stock);

		// Commit transaction
		session.getTransaction().commit();

		// terminate session factory, otherwise program won't end
		HibernateUtil.getSessionFactory().close();

	}

}
