package test.googlecode.genericdao.databaseinitializer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class SqlUpdateStatementBuilder extends SqlStatementBuilder {
	
	@Override
	protected void buildSql() {
		beginSql();
		appendTableName();
		appendSetClause();
		appendWhereClause();
	}
	
	protected void beginSql() {
		sql.append("UPDATE ");
	}
	
	protected void appendSetClause() {
		List<String> assignments = new ArrayList<String>(); 
		for (Entry<String, Object> entry : columnValues) {
			assignments.add(entry.getKey() + " = " + parameterPlaceholderOrNULL(entry.getValue()));
		}
		
		sql.append(" SET ");
		appendCommaDelimitedList(assignments);
	}

	protected void appendWhereClause() {
		List<String> whereConditions = new ArrayList<String>(); 
		for (Entry<String, Object> entry : keyValues) {
			whereConditions.add(entry.getKey() + " = ?");
		}
		
		if (!whereConditions.isEmpty()) {
			sql.append(" WHERE ");
			appendDelimitedList(whereConditions, " AND ");
		}
	}

	@Override
	protected void setStatementParameterValues() throws SQLException {
		super.setStatementParameterValues();
		setKeyValueParameters();
	}
	
	protected void setKeyValueParameters() throws SQLException {
		setParameters(keyValues);
	}
	
}
