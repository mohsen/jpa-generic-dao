package test.googlecode.genericdao.databaseinitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class SqlInsertStatementBuilder extends SqlStatementBuilder {
	
	@Override
	protected void buildSql() {
		beginSql();
		appendTableName();
		appendColumnNames();
		appendColumnValues();
	}

	protected void beginSql() {
		sql.append("INSERT INTO ");
	}

	protected void appendColumnNames() {
		List<String> names = new ArrayList<String>();
		
		for (Entry<String, Object> nameValuePair : columnValues) {
			names.add(nameValuePair.getKey());
		}
		
		sql.append("(");
		appendCommaDelimitedList(names);
		sql.append(")");
	}

	protected void appendColumnValues() {
		List<String> values = new ArrayList<String>();
		
		for (Entry<String, Object> nameValuePair : columnValues) {
			values.add(parameterPlaceholderOrNULL(nameValuePair.getValue()));
		}
		
		sql.append(" values (");
		appendCommaDelimitedList(values);
		sql.append(")");
	}
}
