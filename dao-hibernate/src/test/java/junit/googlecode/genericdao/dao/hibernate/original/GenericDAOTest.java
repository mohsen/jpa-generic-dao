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

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import test.googlecode.genericdao.dao.hibernate.dao.original.PersonDAO;
import test.googlecode.genericdao.dao.hibernate.dao.original.PersonDAOImpl;
import test.googlecode.genericdao.BaseTest;
import test.googlecode.genericdao.model.Home;
import test.googlecode.genericdao.model.Person;

import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Search;

@Transactional
public class GenericDAOTest extends BaseTest {

	private PersonDAO personDAO;

	@Autowired
	public void setOrigPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}
	
	@Autowired
	SessionFactory sessionFactory;

	/**
	 * Just quickly check that all the methods basically work. The underlying
	 * implementation is more thoroughly tested in the
	 * <code>junit.googlecode.genericdao.dao.hibernate</code> package
	 */
	@Test
	public void testDAO() {
		Person fred = new Person();
		fred.setFirstName("Fred");
		fred.setLastName("Smith");
		fred.setAge(35);
		setup(fred);

		personDAO.create(fred);

		Person bob = new Person();
		bob.setFirstName("Bob");
		bob.setLastName("Jones");
		bob.setAge(58);
		setup(bob);

		personDAO.create(bob);

		fred.setFather(bob);

		assertEquals(bob, personDAO.fetch(bob.getId()));
		assertEquals(fred, personDAO.fetch(fred.getId()));

		//search
		assertListEqual(new Person[] { bob, fred }, personDAO.fetchAll());
		assertListEqual(new Person[] { bob, fred }, personDAO
				.search(new Search()));
		assertListEqual(new Person[] { bob, fred }, personDAO
				.search(new Search(Person.class)));

		//count
		assertEquals(2, personDAO.count(new Search()));
		assertEquals(2, personDAO.count(new Search(Person.class)));
		
		//searchAndCount
		assertListEqual(new Person[] { bob, fred }, personDAO
				.searchAndCount(new Search()).getResult());
		assertListEqual(new Person[] { bob, fred }, personDAO
				.searchAndCount(new Search(Person.class)).getResult());

		//searchUnique
		Search s = new Search();
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, personDAO.searchUnique(s));
		s = new Search(Person.class);
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, personDAO.searchUnique(s));
		
		//searchGeneric
		s = new Search();
		s.addFilterEqual("id", bob.getId());
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("firstName");
		assertEquals(bob.getFirstName(), personDAO.searchGeneric(s).get(0));
		s.setSearchClass(Person.class);
		assertEquals(bob.getFirstName(), personDAO.searchGeneric(s).get(0));
		
		try {
			personDAO.search(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.count(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.searchAndCount(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.searchUnique(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		try {
			personDAO.searchGeneric(new Search(Home.class));
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch(IllegalArgumentException ex) {}
		
		//example
		Person example = new Person();
		example.setFirstName("Bob");
		example.setLastName("Jones");
		
		s = new Search(Person.class);
		s.addFilter(personDAO.getFilterFromExample(example));
		assertEquals(bob, personDAO.searchUnique(s));
		
		example.setAge(0);
		s.clear();
		s.addFilter(personDAO.getFilterFromExample(example));
		assertEquals(null, personDAO.searchUnique(s));
		
		s.clear();
		s.addFilter(personDAO.getFilterFromExample(example, new ExampleOptions().setExcludeZeros(true)));
		assertEquals(bob, personDAO.searchUnique(s));
		
		//check searching with null
		assertListEqual(personDAO.search(null), fred, bob);
		assertListEqual(personDAO.searchGeneric(null), fred, bob);
		assertListEqual(personDAO.search(null), fred, bob);
		assertListEqual(personDAO.searchGeneric(null), fred, bob);
		assertEquals(2, personDAO.count(null));
		assertListEqual(personDAO.searchAndCount(null).getResult(), fred, bob);
		assertEquals(2, personDAO.searchAndCount(null).getTotalCount());
		try {
			personDAO.searchUnique(null);
			fail("Should have thrown NullPointerException.");
		} catch (NullPointerException ex) {}
		

		personDAO.deleteEntity(bob);
		assertEquals(null, personDAO.fetch(bob.getId()));

		personDAO.deleteById(fred.getId());
		assertEquals(null, personDAO.fetch(fred.getId()));

		assertEquals(0, personDAO.count(new Search(Person.class)));
		
		bob.setId(null);
		fred.setId(null);

		personDAO.createOrUpdate(bob);
		personDAO.create(fred);

		flush();
		clear();
		
		Person bob2 = copy(bob);
		bob2.setFirstName("Bobby");
		personDAO.update(bob2);
		assertEquals("Bobby", (personDAO.fetch(bob.getId())).getFirstName());

		personDAO.refresh(bob2);
		assertTrue(personDAO.isConnected(bob2));
		assertFalse(personDAO.isConnected(bob));
	}

	@Test
	public void testSubclassingDAO() {
		initDB();
		
		PersonDAO2 p2 = new PersonDAO2();
		p2.setSessionFactory(sessionFactory);
		
		assertTrue(p2.fetchAll().size() > 0);
		
		PersonDAO2 p3 = new PersonDAO2() {
			@Override
			public Person findJoeA() {
				assertEquals(Person.class, persistentClass);
				
				return (Person) searchUnique(new Search().addFilterEqual("firstName", "Joe").addFilterLike("lastName", "A%"));
			}
		};
		p3.setSessionFactory(sessionFactory);
		
		assertEquals(joeA, p3.findJoeA());
	}
	
	//Used for testSubclassingDAO()
	private static class PersonDAO2 extends PersonDAOImpl {
		public Person findJoeA() {
			return null; //placeholder method
		}
	}
}
