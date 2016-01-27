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
package test.googlecode.genericdao.dao.hibernate.dao.original;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.NonUniqueResultException;

import test.googlecode.genericdao.model.Person;

import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

public class PersonService {
	PersonDAO dao;
	
	@Resource(name="origPersonDAO")
	public void setPersonDAO(PersonDAO dao) {
		this.dao = dao;
	}
	
	public void create(Person object) {
		dao.create(object);
	}

	public boolean createOrUpdate(Person object) {
		return dao.createOrUpdate(object);
	}

	public boolean deleteById(Long... id) {
		return dao.deleteById(id[0]);
	}

	//Test calling method with used varargs
	public boolean deleteEntity(Person object) {
		return dao.deleteEntity(object);
	}

	//Test calling method with unused varargs
	public Person fetch(Long id, String... foo) {
		return dao.fetch(id);
	}

	public List<Person> fetchAll() {
		return dao.fetchAll();
	}

	public void update(Person object) {
		dao.update(object);
	}

	public List<Person> search(Search search) {
		return dao.search(search);
	}

	public int count(Search search) {
		return dao.count(search);
	}

	public SearchResult<Person> searchAndCount(Search search) {
		return dao.searchAndCount(search);
	}

	public Object searchUnique(Search search) throws NonUniqueResultException {
		return dao.searchUnique(search);
	}

	public boolean isConnected(Object object) {
		return dao.isConnected(object);
	}

	public void flush() {
		dao.flush();
	}

	public void refresh(Object object) {
		dao.refresh(object);
	}
	
	public Filter getFilterFromExample(Person example) {
		return dao.getFilterFromExample(example);
	}
	
	public Filter getFilterFromExample(Person example, ExampleOptions options) {
		return dao.getFilterFromExample(example, options);
	}
}
