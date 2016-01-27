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
package test.googlecode.genericdao.dao.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;

import test.googlecode.genericdao.PersistenceHelper;

public class HibernatePersistenceHelper implements PersistenceHelper {

	private SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T find(Class<T> type, Serializable id) {
		return (T) sessionFactory.getCurrentSession().get(type, id);
	}
	
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	public void persist(Object entity) {
		sessionFactory.getCurrentSession().persist(entity);
	}
	
	public void clear() {
		sessionFactory.getCurrentSession().clear();
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> type, Serializable id) {
		return (T) sessionFactory.getCurrentSession().load(type, id);
	}
	
	public void executeWithJdbcConnection(
			final ExecutableWithJdbcConnection executable) {
		sessionFactory.getCurrentSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				executable.execute(connection);
			}
		});
	}

}
