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
package test.googlecode.genericdao.dao.hibernate.dao;

import java.util.List;

import javax.annotation.Resource;

import test.googlecode.genericdao.model.Person;

import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

public class PersonService {

	private PersonDAO dao;
	
	@Resource(name="personDAO")
	public void setDAO(PersonDAO dao) {
		this.dao = dao;
	}
	
	
	public int count(Search search) {
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

	public Person getReference(Long id) {
		return dao.getReference(id);
	}

	public Person[] getReferences(Long... ids) {
		return dao.getReferences(ids);
	}

	public boolean isAttached(Person entity) {
		return dao.isAttached(entity);
	}

	public void refresh(Person entity) {
		dao.refresh(entity);
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

	public void save(Person... entities) {
		dao.save(entities);
	}

	public boolean save(Person entity) {
		return dao.save(entity);
	}

	public <RT> List<RT> search(Search search) {
		return dao.search(search);
	}

	public <RT> SearchResult<RT> searchAndCount(Search search) {
		return dao.searchAndCount(search);
	}

	public <RT> RT searchUnique(Search search) {
		return (RT) dao.searchUnique(search);
	}
	
	public Filter getFilterFromExample(Person example) {
		return dao.getFilterFromExample(example);
	}
	
	public Filter getFilterFromExample(Person example, ExampleOptions options) {
		return dao.getFilterFromExample(example, options);
	}
}
