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
package test.googlecode.genericdao.dao.jpa.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import test.googlecode.genericdao.model.Person;

import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.SearchResult;

public class PersonService {

	PersonDAO dao;

	@Autowired
	public void setPersonDAO(PersonDAO dao) {
		this.dao = dao;
	}

	public int count(ISearch search) {
		return dao.count(search);
	}

	public Person[] find(Long... ids) {
		return dao.find(ids);
	}

	public Person find(Long id) {
		return dao.find(id);
	}

	public List<Person> findAll() {
		return dao.findAll();
	}

	public void flush() {
		dao.flush();
	}

	public Filter getFilterFromExample(Person example, ExampleOptions options) {
		return dao.getFilterFromExample(example, options);
	}

	public Filter getFilterFromExample(Person example) {
		return dao.getFilterFromExample(example);
	}

	public Person getReference(Long id) {
		return dao.getReference(id);
	}

	public Person[] getReferences(Long... ids) {
		return dao.getReferences(ids);
	}

	public boolean isAttached(Person entity) {
		return dao.isAttached(entity);
	}

	public Person[] merge(Person... entities) {
		return dao.merge(entities);
	}

	public Person merge(Person entity) {
		return dao.merge(entity);
	}

	public void persist(Person... entities) {

		dao.persist(entities);
	}

	public void refresh(Person... entities) {

		dao.refresh(entities);
	}

	public void remove(Person... entities) {

		dao.remove(entities);
	}

	public boolean remove(Person entity) {

		return dao.remove(entity);
	}

	public boolean removeById(Long id) {

		return dao.removeById(id);
	}

	public void removeByIds(Long... ids) {
		dao.removeByIds(ids);
	}

	public Person[] save(Person... entities) {
		return dao.save(entities);
	}

	public Person save(Person entity) {
		return dao.save(entity);
	}

	public <RT> List<RT> search(ISearch search) {
		return dao.search(search);
	}

	public <RT> SearchResult<RT> searchAndCount(ISearch search) {
		return dao.searchAndCount(search);
	}

	@SuppressWarnings("unchecked")
	public <RT> RT searchUnique(ISearch search) {
		return (RT) dao.searchUnique(search);
	}

}
