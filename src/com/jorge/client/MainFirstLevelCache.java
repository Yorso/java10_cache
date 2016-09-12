package com.jorge.client;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.jorge.entity.Message;

/**
 * Caching objects: First-Level Cache
 * 
 * A cache is a copy of data. Copy means get data pulled from database, but it is living outside the database
 * 
 * EntityManager represents PersistenceContext and, therefore, a cache. Non-shared database connection
 * 
 * EntityManager is in First-Level Cache and in JVM (Java Virtual Machine)
 * 
 * The scope of First-Level Cache is EntityManager (We create only one EntityManager and use it to set data in several Message objects)
 * 
 */
public class MainFirstLevelCache {

	public static void main(String[] args) {
		BasicConfigurator.configure(); // Necessary for configure log4j. It must be the first line in main method
	       					           // log4j.properties must be in /src directory

		Logger  logger = Logger.getLogger(MainFirstLevelCache.class.getName());
		logger.debug("log4j configured correctly and logger set");

		logger.debug("creating entity manager factory");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("helloworld"); // Second-Level cache 
		logger.debug("creating entity manager");
		EntityManager em = emf.createEntityManager(); //First-Level cache
		
		logger.debug("getting transaction");
		EntityTransaction txn = em.getTransaction(); 
		
		try{
			logger.debug("beginning transaction");
			txn.begin();
			
			logger.debug("getting message 1 from DataBase");
			Message message1 = em.find(Message.class, 1L); // 1. Execute SQL statement against DataBase at Runtime (SELECT * FROM...)
														   // 2. We get a persistent Message object in return in EntityManager
			
			logger.info("Message 1: " + message1.toString());
			
			logger.debug("getting message 2 from EntityManager (First-Level Cache)");
			Message message2 = em.find(Message.class, 1L); // We get a Message object from EntityManager (above), not by executing SQL statement against DataBase
														   // Message object is in EntityManage.
														   // EntityMAnager gets Message object to us from the cache (First-Level cache),  not form DataBase
			
			logger.info("Message 2: " + message2.toString());
			
			logger.debug("making commit");
			txn.commit();
		}
		catch (Exception e) {
			if (txn != null) {
				logger.error("something was wrong, making rollback of transactions");
				txn.rollback(); // If something was wrong, we make rollback
			}
			logger.error("Exception: " + e.getMessage().toString());
		} finally {
			if (em != null) {
				logger.debug("close session");
				em.close();
			}
		}
	}

}
  