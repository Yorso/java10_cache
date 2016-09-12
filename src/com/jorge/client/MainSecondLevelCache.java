package com.jorge.client;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.jorge.entity.Message;

/**
 * Caching objects: Second-Level Cache
 * 
 * A cache is a copy of data. Copy means get data pulled from database, but it is living outside the database
 * 
 * This time we have two Message objects, each one belongs to its EntityManager (em1 and em2) and each Entity Manager is in JVM
 * 
 * The scope of Second-Level Cache is EntityManagerFactory (We create several EntityManager and each one of them sets data in its own Message object)
 * 
 */
public class MainSecondLevelCache {

	public static void main(String[] args) {
		EntityTransaction txn = null;
		
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
	       					           // log4j.properties must be in /src directory

		Logger  logger = Logger.getLogger(MainSecondLevelCache.class.getName());
		logger.debug("log4j configured correctly and logger set");

		logger.debug("creating entity manager factory");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("helloworld"); // Second-Level cache 
		
		try{
			logger.debug("creating entity manager 1");
			EntityManager em1 = emf.createEntityManager(); //First-Level cache
			
			logger.debug("getting transaction 1");
			txn = em1.getTransaction(); 
			
			logger.debug("beginning transaction 1");
			txn.begin();
			
			logger.debug("getting message 1 from DataBase");
			Message message1 = em1.find(Message.class, 1L); // 1. Execute SQL statement against DataBase at Runtime (SELECT * FROM...)
														    // 2. We get a persistent Message 1 object in return in EntityManager em1
			
			logger.info("Message 1: " + message1.toString());
			
			logger.debug("making commit 1");
			txn.commit();
			
			logger.debug("close session 1");
			em1.close();
			
			
			
			logger.debug("creating entity manager 2");
			EntityManager em2 = emf.createEntityManager(); //First-Level cache
			
			logger.debug("getting transaction 2");
			txn = em2.getTransaction(); 
			
			logger.debug("beginning transaction 2");
			txn.begin();
			
			logger.debug("getting message 2 from DataBase");
			Message message2 = em2.find(Message.class, 1L); // 1. Execute SQL statement against DataBase at Runtime (SELECT * FROM...)
														    // 2. We get a persistent Message 2 object in return in EntityManager em2
			
			logger.info("Message 2: " + message2.toString());
			
			logger.debug("making commit 2");
			txn.commit();
			
			logger.debug("close session 2");
			em2.close();
		}
		catch (Exception e) {
			if (txn != null) {
				logger.error("something was wrong, making rollback of transactions");
				txn.rollback(); // If something was wrong, we make rollback
			}
			logger.error("Exception: " + e.getMessage().toString());
		} 
	}

}
  