package test.googlecode.genericdao.databaseinitializer;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import test.googlecode.genericdao.PersistenceHelper;

public class PersistableTestDataModel extends TestDataModel {
	protected DatabasePopulator dbPopulator;
	
	@PostConstruct
	public void setupDBPopulator() {
		dbPopulator = new DatabasePopulator(getAllModelObjects());
	}
	
	protected PersistenceHelper persistenceHelper;
	
	@Autowired(required = false) //TODO probably should be required. but some tests extend this
	public void setPersistenceHelper(PersistenceHelper persistenceHelper) {
		this.persistenceHelper = persistenceHelper;
	}

	public void persistModelToDatabase() {
		persistenceHelper.executeWithJdbcConnection(new PersistenceHelper.ExecutableWithJdbcConnection() {
			public void execute(Connection connection) throws SQLException {
				dbPopulator.persistEntitiesAndSetIds(connection);
			}
		});
	}
}
