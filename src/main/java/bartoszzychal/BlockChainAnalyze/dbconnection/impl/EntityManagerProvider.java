package bartoszzychal.BlockChainAnalyze.dbconnection.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {

	private static EntityManager entityManager;
	private static EntityManagerFactory emfactory;
	
	private EntityManagerProvider() {
	}
	
	public static synchronized EntityManager getEntityManager() {
		if (entityManager == null) {
			EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "BlockchainIndex" );
			entityManager = emfactory.createEntityManager( );
		}
		return entityManager;
	}
	
	public static synchronized void closeConnection() {
		if (emfactory.isOpen()) emfactory.close();
	}
}
