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
package junit.googlecode.genericdao.dao.hibernate.original;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import test.googlecode.genericdao.BaseTest;
import test.googlecode.genericdao.model.Person;

import com.googlecode.genericdao.dao.hibernate.original.DAODispatcher;
import com.googlecode.genericdao.dao.hibernate.original.FlexDAOAdapter;
import com.googlecode.genericdao.dao.hibernate.original.GeneralDAO;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.flex.FlexSearch;

@Transactional
public class FlexDAOAdapterTest extends BaseTest {
	private GeneralDAO generalDAO;
	private FlexDAOAdapter flexDAOAdapter;
	private DAODispatcher dispatcher;

	@Autowired @Qualifier("origGeneralDAO")
	public void setOrigGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}

	@Autowired
	public void setOrigFlexDAOAdapter(FlexDAOAdapter flexDAOAdapter) {
		this.flexDAOAdapter = flexDAOAdapter;
	}

	@Autowired
	public void setOrigDAODispatcher(DAODispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	/**
	 * Just quickly check that all the methods basically work. We're relying on
	 * underlying implementation that is thoroughly tested elsewhere.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFlexDAOAdapter() throws Exception {
		// use general DAO
		dispatcher.setSpecificDAOs(new HashMap<String, Object>());

		Person fred = new Person();
		fred.setFirstName("Fred");
		fred.setLastName("Smith");
		fred.setAge(35);
		setup(fred);

		flexDAOAdapter.create(fred);

		Person bob = new Person();
		bob.setFirstName("Bob");
		bob.setLastName("Jones");
		bob.setAge(58);
		setup(bob);

		flexDAOAdapter.create(bob);

		fred.setFather(bob);

		assertEquals(bob, flexDAOAdapter.fetch(bob.getId(), Person.class
				.getName()));
		assertEquals(fred, flexDAOAdapter.fetch(fred.getId(), Person.class
				.getName()));

		FlexSearch s = new FlexSearch();
		s.setSearchClassName(Person.class.getName());

		assertListEqual(new Person[] { bob, fred }, flexDAOAdapter
				.fetchAll(Person.class.getName()));
		assertListEqual(new Person[] { bob, fred }, flexDAOAdapter.search(s));

		assertEquals(2, flexDAOAdapter.searchLength(s));
		assertListEqual(new Person[] { bob, fred }, flexDAOAdapter
				.searchAndLength(s).getResult());

		s.setFilters(new Filter[] { Filter.equal("id", bob.getId()) });
		assertEquals(bob, flexDAOAdapter.searchUnique(s));

		flexDAOAdapter.deleteEntity(bob);
		assertEquals(null, flexDAOAdapter.fetch(bob.getId(), Person.class
				.getName()));

		flexDAOAdapter.deleteById(fred.getId(), Person.class.getName());
		assertEquals(null, flexDAOAdapter.fetch(fred.getId(), Person.class
				.getName()));

		assertEquals(0, flexDAOAdapter.searchLength(s));

		bob.setId(null);
		fred.setId(null);

		flexDAOAdapter.createOrUpdate(bob);
		flexDAOAdapter.create(fred);

		flush();
		clear();
		
		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		flexDAOAdapter.update(bob2);
		assertEquals("Bobby", ((Person) generalDAO.fetch(bob.getClass(), bob
				.getId())).getFirstName());
	}
}