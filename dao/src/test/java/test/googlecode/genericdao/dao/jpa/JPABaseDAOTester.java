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
package test.googlecode.genericdao.dao.jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.dao.jpa.JPABaseDAO;
import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;

@Repository
public class JPABaseDAOTester extends JPABaseDAO {

	@PersistenceContext
	@Override
	public void setEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}
	
	@Autowired
	@Override
	public void setSearchProcessor(JPASearchProcessor searchProcessor) {
		super.setSearchProcessor(searchProcessor);
	}
	
	public <T> List<T> all(Class<T> type) {
		return super._all(type);
	}

	public boolean contains(Object o) {
		return super._contains(o);
	}

	public int count(Class<?> searchClass, ISearch search) {
		return super._count(searchClass, search);
	}

	public int count(Class<?> type) {
		return super._count(type);
	}

	public int count(ISearch search) {
		return super._count(search);
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

	public <T> T[] find(Class<T> type, Serializable... ids) {
		return super._find(type, ids);
	}

	public <T> T find(Class<T> type, Serializable id) {
		return super._find(type, id);
	}

	public void flush() {
		super._flush();
	}

	public Filter getFilterFromExample(Object example, ExampleOptions options) {
		return super._getFilterFromExample(example, options);
	}

	public Filter getFilterFromExample(Object example) {
		return super._getFilterFromExample(example);
	}

	public <T> T getReference(Class<T> type, Serializable id) {
		return super._getReference(type, id);
	}

	public <T> T[] getReferences(Class<T> type, Serializable... ids) {
		return super._getReferences(type, ids);
	}

	public <T> T[] merge(Class<T> arrayType, T... entities) {
		return super._merge(arrayType, entities);
	}

	public <T> T merge(T entity) {
		return super._merge(entity);
	}

	public void persist(Object... entities) {
		super._persist(entities);
	}

	public <T> T[] persistOrMerge(Class<T> arrayType, T... entities) {
		return super._persistOrMerge(arrayType, entities);
	}

	public <T> T persistOrMerge(T entity) {
		return super._persistOrMerge(entity);
	}

	public void refresh(Object... entities) {
		super._refresh(entities);
	}

	public void removeByIds(Class<?> type, Serializable... ids) {
		super._removeByIds(type, ids);
	}

	public boolean removeById(Class<?> type, Serializable id) {
		return super._removeById(type, id);
	}

	public void removeEntities(Object... entities) {
		super._removeEntities(entities);
	}

	public boolean removeEntity(Object entity) {
		return super._removeEntity(entity);
	}

	public List search(Class<?> searchClass, ISearch search) {
		return super._search(searchClass, search);
	}

	public List search(ISearch search) {
		return super._search(search);
	}

	public SearchResult searchAndCount(Class<?> searchClass, ISearch search) {
		return super._searchAndCount(searchClass, search);
	}

	public SearchResult searchAndCount(ISearch search) {
		return super._searchAndCount(search);
	}

	public Object searchUnique(Class<?> searchClass, ISearch search) throws NonUniqueResultException, NoResultException {
		return super._searchUnique(searchClass, search);
	}

	public Object searchUnique(ISearch search) throws NonUniqueResultException, NoResultException {
		return super._searchUnique(search);
	}

}
