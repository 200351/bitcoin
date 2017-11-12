package bartoszzychal.BlockChainAnalyze.dbconnection;

import javax.persistence.EntityManager;

import bartoszzychal.BlockChainAnalyze.dbconnection.impl.EntityManagerProvider;

public interface IRepository {
	
	default void openTransaction() {
		EntityManagerProvider.getEntityManager().getTransaction( ).begin();
	}

	default void closeTransaction() {
		EntityManagerProvider.getEntityManager().getTransaction().commit();
	}
	
	default void rollbackTransaction() {
		EntityManagerProvider.getEntityManager().getTransaction( ).rollback();
	}
	
	default EntityManager getEntityManager() {
		return EntityManagerProvider.getEntityManager();
	}
	
	default void closeConnection() {
		EntityManagerProvider.closeConnection();
	}
}
