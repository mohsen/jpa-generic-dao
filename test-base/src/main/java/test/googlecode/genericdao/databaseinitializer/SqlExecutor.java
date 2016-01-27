package test.googlecode.genericdao.databaseinitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlExecutor {
	
	private Connection connection;
	private PreparedStatement statement = null;
	private ResultSet generatedKeys = null;
	private long generatedKey;
	
	public SqlExecutor(Connection connection) {
		this.connection = connection;
	}
	
	public void executeWithoutGeneratedId(SqlStatementBuilder statementBuilder) throws SQLException {
	    try {
	    	doExecuteWithoutGeneratedId(statementBuilder);
	    } finally {
    		if (statement != null) statement.close();
	    }		
	}
	
	private void doExecuteWithoutGeneratedId(SqlStatementBuilder statementBuilder) throws SQLException {
		executeStatement(statementBuilder);
	}
	
	private void executeStatement(SqlStatementBuilder statementBuilder) throws SQLException {
		statement = statementBuilder.buildStatement(connection);
		statement.executeUpdate();
	}
	
	public void executeWithGeneratedId(SqlStatementBuilder statementBuilder) throws SQLException {
	    try {
	    	doExecuteWithGeneratedId(statementBuilder);
	    } finally {
    		if (statement != null) statement.close();
	    }		
	}
	
	private void doExecuteWithGeneratedId(SqlStatementBuilder statementBuilder) throws SQLException {
		executeStatement(statementBuilder);
		updateGeneratedKey();
	}
	
	private void updateGeneratedKey() throws SQLException {
		try {
			doUpdateGeneratedKey();
		} finally {
			if (generatedKeys != null) generatedKeys.close();
		}
	}
	
	private void doUpdateGeneratedKey() throws SQLException {
		generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            generatedKey = generatedKeys.getLong(1);
        } else {
            throw new SQLException("Inserting entity failed, no generated key obtained.");
        }
	}
	
	public long getGeneratedKey() {
		return generatedKey;
	}
}
