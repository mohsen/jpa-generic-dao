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
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.NonUniqueResultException;
import org.hibernate.SessionFactory;

import com.googlecode.genericdao.dao.hibernate.HibernateBaseDAO;
import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.SearchResult;

public class HibernateBaseDAOTester extends HibernateBaseDAO {
	@Override
	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	public <T> List<T> all(Class<T> klass) {
		return super._all(klass);
	}

	public int count(Class<?> klass) {
		return super._count(klass);
	}

	public int count(ISearch search) {
		return super._count(search);
	}

	public boolean deleteById(Class<?> klass, Serializable id) {
		return super._deleteById(klass, id);
	}

	public boolean deleteEntity(Object entity) {
		return super._deleteEntity(entity);
	}

	public void flush() {
		super._flush();
	}

	public <T> T get(Class<T> klass, Serializable id) {
		return super._get(klass, id);
	}

	public boolean sessionContains(Object o) {
		return super._sessionContains(o);
	}

	public <T> T load(Class<T> klass, Serializable id) {
		return super._load(klass, id);
	}

	public void load(Object transientEntity, Serializable id) {
		super._load(transientEntity, id);
	}

	public <T> T merge(T entity) {
		return super._merge(entity);
	}

	public void persist(Object... entities) {
		super._persist(entities);
	}

	public void refresh(Object o) {
		super._refresh(o);
	}

	public Serializable save(Object object) {
		return super._save(object);
	}

	@SuppressWarnings("unchecked")
	public List search(ISearch search) {
		return super._search(search);
	}

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(ISearch search) {
		return super._searchAndCount(search);
	}

	public Object searchUnique(ISearch search) throws NonUniqueResultException {
		return super._searchUnique(search);
	}

	public void update(Object... transientEntities) {
		super._update(transientEntities);
	}

	public int count(Class<?> searchClass, ISearch search) {
		return super._count(searchClass, search);
	}

	@SuppressWarnings("unchecked")
	public List search(Class<?> searchClass, ISearch search) {
		return super._search(searchClass, search);
	}

	@SuppressWarnings("unchecked")
	public SearchResult searchAndCount(Class<?> searchClass, ISearch search) {
		return super._searchAndCount(searchClass, search);
	}

	public Object searchUnique(Class<?> searchClass, ISearch search) {
		return super._searchUnique(searchClass, search);
	}
	

	public void deleteById(Class<?> type, Serializable... ids) {
		super._deleteById(type, ids);
	}

	public void deleteEntities(Object... entities) {
		super._deleteEntities(entities);
	}

	public <T> T[] get(Class<T> type, Serializable... ids) {
		return super._get(type, ids);
	}

	public <T> T[] load(Class<T> type, Serializable... ids) {
		return super._load(type, ids);
	}

	public void save(Object... entities) {
		super._save(entities);
	}
	public boolean[] saveOrUpdateIsNew(Object... entities) {
		return super._saveOrUpdateIsNew(entities);
	}

	public void saveOrUpdate(Object entity) {
		super._saveOrUpdate(entity);
	}

	public boolean saveOrUpdateIsNew(Object entity) {
		return super._saveOrUpdateIsNew(entity);
	}

	public boolean[] exists(Class<?> type, Serializable... ids) {
		return super._exists(type, ids);
	}

	public boolean exists(Class<?> type, Serializable id) {
		return super._exists(type, id);
	}

	public boolean exists(Object entity) {
		return super._exists(entity);
	}

	public Filter getFilterFromExample(Object example, ExampleOptions options) {
		return _getFilterFromExample(example, options);
	}
	
	public Filter getFilterFromExample(Object example) {
		return _getFilterFromExample(example);
	}
}
