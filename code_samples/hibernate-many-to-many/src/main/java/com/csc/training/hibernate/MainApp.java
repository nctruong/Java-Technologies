package com.csc.training.hibernate;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;

import com.csc.training.hibernate.model.Category;
import com.csc.training.hibernate.model.Stock;
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
 
        Category category1 = new Category("CONSUMER", "CONSUMER COMPANY");
        Category category2 = new Category("INVESTMENT", "INVESTMENT COMPANY");
    
        Set<Category> categories = new HashSet<Category>();
        categories.add(category1);
        categories.add(category2);
        
        stock.setCategories(categories);
        
        session.save(stock);
		
		// Commit transaction
		session.getTransaction().commit();

		// terminate session factory, otherwise program won't end
		HibernateUtil.getSessionFactory().close();

	}

}
