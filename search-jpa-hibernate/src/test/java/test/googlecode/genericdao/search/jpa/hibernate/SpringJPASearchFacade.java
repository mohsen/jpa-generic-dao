package test.googlecode.genericdao.search.jpa.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.googlecode.genericdao.search.jpa.JPASearchFacade;

public class SpringJPASearchFacade extends JPASearchFacade {
	@Override
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}
}
