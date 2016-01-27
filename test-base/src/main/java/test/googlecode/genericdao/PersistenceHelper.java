/* Copyright 2013 David Wolverton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.googlecode.genericdao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

public interface PersistenceHelper {
	public <T> T find(Class<T> type, Serializable id);

	public void persist(Object entity);
	
	/**
	 * Find object using lazy loading: i.e. getReference() instead of find()
	 * and load() instead of get()
	 */
	public <T> T getProxy(Class<T> type, Serializable id);
	
	public void flush();
	
	public void clear();
	
	public void executeWithJdbcConnection(ExecutableWithJdbcConnection executable);
	
	public interface ExecutableWithJdbcConnection {
		public void execute(Connection connection) throws SQLException;
	}
}
