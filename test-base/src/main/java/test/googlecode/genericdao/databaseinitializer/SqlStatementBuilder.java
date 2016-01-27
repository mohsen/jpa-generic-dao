package test.googlecode.genericdao.databaseinitializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.StatementCreatorUtils;

import com.mysql.jdbc.Statement;

public abstract class SqlStatementBuilder {

	protected boolean autoGenerateKeys = true;
	protected String tableName;
	protected List<Entry<String, Object>> columnValues;
	protected List<Entry<String, Object>> keyValues;

	protected Connection connection;
	protected StringBuilder sql = new StringBuilder();
	protected PreparedStatement statement;
	
	int parameterIndex = 1;
	
	public static SqlStatementBuilder insert(String tableName,
			Map<String, Object> columnValues) {
		SqlStatementBuilder builder = new SqlInsertStatementBuilder();
		builder.tableName = tableName;
		builder.columnValues = mapToList(columnValues);
		builder.autoGenerateKeys = true;
		return builder;
	}
	
	public static SqlStatementBuilder update(String tableName,
			Map<String, Object> columnValues,
			Map<String, Object> keyValues) {
		SqlStatementBuilder builder = new SqlUpdateStatementBuilder();
		builder.tableName = tableName;
		builder.columnValues = mapToList(columnValues);
		builder.keyValues = mapToList(keyValues);
		builder.autoGenerateKeys = false;
		return builder;
	}	
	
	protected static List<Entry<String, Object>> mapToList(Map<String, Object> map) {
		return new ArrayList<Entry<String, Object>>(map.entrySet());
	}

	public PreparedStatement buildStatement(Connection connection)
			throws SQLException {

		this.connection = connection;
		
		buildSql();
		createStatement();
		setStatementParameterValues();
		
		return statement;
	}

	protected abstract void buildSql();
	
	protected void appendTableName() {
		sql.append(tableName);
	}
	
	protected String parameterPlaceholderOrNULL(Object value) {
		return value != null ? "?" : "NULL";
	}
	
	protected void appendCommaDelimitedList(List<String> list) {
		appendDelimitedList(list, ", ");
	}
		
	protected void appendDelimitedList(List<String> list, String delimiter) {
		boolean first = true;
		for (String element : list) {
			if (!first)
				sql.append(delimiter);
			first = false;
			sql.append(element);
		}
	}
	
	protected void createStatement() throws SQLException {
		int flag = autoGenerateKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
		
		statement = connection.prepareStatement(sql.toString(), flag);
	}
	
	protected void setStatementParameterValues() throws SQLException {
		setColumnValueParameters();
	}
	
	protected void setColumnValueParameters() throws SQLException {
		setParameters(columnValues);		
	}
	
	protected void setParameters(List<Entry<String, Object>> values) throws SQLException {
		for (Entry<String, Object> nameValuePair : values) {
			Object value = nameValuePair.getValue();
			if (value != null) {
				int sqlType = getSQLTypeFromJavaType(value.getClass());
				StatementCreatorUtils.setParameterValue(statement, parameterIndex++, sqlType, value);
			}
		}		
	}

	protected int getSQLTypeFromJavaType(Class<?> javaType) {
		if (javaType.equals(Long.class)) {
			return Types.INTEGER;
		} else if (javaType.equals(Integer.class)) {
			return Types.INTEGER;
		} else if (javaType.equals(String.class)) {
			return Types.VARCHAR;
		} else if (javaType.equals(Date.class)) {
			return Types.DATE;
		} else if (javaType.equals(Float.class)) {
			return Types.FLOAT;
		} else if (javaType.equals(Double.class)) {
			return Types.DOUBLE;
		} else if (javaType.equals(Boolean.class)) {
			return Types.BOOLEAN;
		} else {
			throw new RuntimeException("Unexpected Java Type for Argument");
		}
	}

}
