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
package junit.googlecode.genericdao.dao.jpa;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import test.googlecode.genericdao.dao.jpa.JPABaseDAOTester;
import test.googlecode.genericdao.BaseTest;
import test.googlecode.genericdao.model.Home;
import test.googlecode.genericdao.model.Ingredient;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Pet;
import test.googlecode.genericdao.model.Project;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;
import test.googlecode.genericdao.model.RecipeIngredientId;
import test.googlecode.genericdao.model.Store;

import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Transactional
public class BaseDAOTest extends BaseTest {

	private JPABaseDAOTester target;

	@Autowired
	public void setJpaBaseDAOTester(JPABaseDAOTester dao) {
		this.target = dao;
	}

	@Test
	public void testPersist() {
		target.persist(grandpaA.getHome().getAddress());
		target.persist(grandpaA.getHome());
		target.persist(grandpaA);

		List<Person> list = target.all(Person.class);
		assertEquals(1, list.size());
		assertEquals(grandpaA, list.get(0));

		assertEquals(grandpaA, target.find(Person.class, grandpaA.getId()));

		target.persist(papaA.getHome().getAddress());
		target.persist(papaA.getHome());
		target.persist(grandmaA);
		target.persist(papaA);
		target.persist(mamaA);
		target.persist(joeA);

		grandpaA.setFirstName("Dean");

		assertEquals("Dean", target.find(Person.class, joeA.getId()).getFather().getFather().getFirstName());

		grandpaA.setFirstName("Grandpa");
	}

	@Test
	public void testMerge() {
		initDB();
		Person fred = copy(papaA);
		fred.setFirstName("Fred");
		Person attachedFred = target.merge(fred);

		assertEquals("The change should be made.", "Fred", target.find(Person.class, joeA.getId()).getFather()
				.getFirstName());

		assertFalse(target.contains(fred));
		assertTrue(target.contains(attachedFred));

		Search s = new Search(Person.class);
		s.addField("father.lastName");
		s.setResultMode(Search.RESULT_SINGLE);
		s.addFilterEqual("id", joeA.getId());

		fred.setLastName("Santos");
		assertEquals("The change should not be made.", "Alpha", target.searchUnique(s));

		attachedFred.setLastName("Santos");
		assertEquals("The change should be made.", "Santos", target.searchUnique(s));
	}

	@Test
	public void testRemove() {
		initDB();
		//remove all project member relationships to avoid integrity constraint violations
		for (Project project : target.all(Project.class)) {
			if (project.getMembers() != null)
				project.getMembers().clear();
		}

		List<Person> list = target.all(Person.class);
		int sizeBefore = list.size();

		assertTrue("Should return true when successfully deleting", target.removeById(Person.class, joeA.getId()));
		assertTrue("Should return true when successfully deleting", target.removeEntity(sallyA));

		list = target.all(Person.class);
		assertEquals(sizeBefore - 2, list.size());
		for (Person person : list) {
			if (person.getId().equals(joeA.getId()) || person.getId().equals(sallyA.getId()))
				fail("Neither Joe nor Sally should now be in the DB");
		}

		joeA.setId(null);
		sallyA.setId(null);
		target.persist(joeA);
		target.persist(sallyA);

		list = target.all(Person.class);
		assertEquals(sizeBefore, list.size());
		boolean joeFound = false, sallyFound = false;
		for (Person person : list) {
			if (person.getFirstName().equals("Joe") && person.getLastName().equals("Alpha"))
				joeFound = true;
			if (person.getFirstName().equals("Sally") && person.getLastName().equals("Alpha"))
				sallyFound = true;
		}
		assertTrue("Joe and Sally should now be back in the DB", joeFound && sallyFound);

		// Test deleting by non-existent ID.
		Search s = new Search(Person.class);
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("id", Field.OP_MAX);
		Long unusedId = ((Long) target.searchUnique(s)).longValue() + 1;

		// deleteById should not throw an error
		assertFalse(target.removeById(Person.class, unusedId));

		Person fake = new Person();
		assertFalse("return false when no ID", target.removeEntity(fake));
		fake.setId(unusedId);
		assertFalse("return false when ID not found", target.removeEntity(fake));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testForceClass() {
		Person bob = copy(grandpaA);
		Person fred = copy(grandmaA);
		target.persist(bob);
		target.persist(fred);

		Search s = new Search();
		Search sP = new Search(Person.class);
		Search sH = new Search(Home.class);
		// search
		assertListEqual(new Person[] { bob, fred }, target.search(Person.class, s));
		assertListEqual(new Person[] { bob, fred }, target.search(Person.class, sP));
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());

		// count
		assertEquals(2, target.count(Person.class, s));
		assertEquals(2, target.count(Person.class, sP));
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());

		// searchAndCount
		assertListEqual(new Person[] { bob, fred }, target.searchAndCount(Person.class, s).getResult());
		assertListEqual(new Person[] { bob, fred }, target.searchAndCount(Person.class, sP).getResult());
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());

		// searchUnique
		s.addFilterEqual("id", bob.getId());
		assertEquals(bob, target.searchUnique(Person.class, s));
		sP.addFilterEqual("id", bob.getId());
		assertEquals(bob, target.searchUnique(Person.class, sP));
		assertEquals(null, s.getSearchClass());
		assertEquals(Person.class, sP.getSearchClass());

		try {
			target.search(Person.class, sH);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch (IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
		try {
			target.count(Person.class, sH);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch (IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
		try {
			target.searchAndCount(Person.class, sH);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch (IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
		try {
			target.searchUnique(Person.class, sH);
			fail("An error should be thrown when a different class is specified in the Search.");
		} catch (IllegalArgumentException ex) {
			assertEquals(Home.class, sH.getSearchClass());
		}
	}

	@Test
	public void testPersistMulti() {
		target.persist(grandpaA.getHome().getAddress(), grandpaA.getHome(), grandpaA);

		List<Person> list = target.all(Person.class);
		assertEquals(1, list.size());
		assertEquals(grandpaA, list.get(0));

		assertEquals(grandpaA, target.find(Person.class, grandpaA.getId()));

		target.persist(papaA.getHome().getAddress(), papaA.getHome(), grandmaA, papaA, mamaA, joeA);

		grandpaA.setFirstName("Dean");
		try {
			assertEquals("Dean", target.find(Person.class, joeA.getId()).getFather().getFather().getFirstName());
		} finally {
			grandpaA.setFirstName("Grandpa");
		}
	}
	
	@Test
	public void testMergeMulti() {
		initDB();
		
		String[] orig = new String[] { grandpaA.getFirstName(), grandmaA.getFirstName() };

		try {
			Object[] persisted = target.merge(Object.class, grandpaA.getHome().getAddress(), grandpaA.getHome(), grandpaA, grandmaA);
		
			assertFalse(target.contains(grandpaA.getHome().getAddress()));
			assertFalse(target.contains(grandpaA.getHome()));
			assertFalse(target.contains(grandpaA));
			
			assertEquals(grandpaA, persisted[2]);
			assertEquals(grandmaA, persisted[3]);
			
			grandpaA.setFirstName("Gob");
			grandmaA.setFirstName("Jean");
			assertEquals(0, target.count(new Search(Person.class).addFilterIn("firstName", "Gob", "Jean")));
			
			Person[] folks = target.merge(Person.class, grandpaA, grandmaA);
			
			assertTrue(persisted[2] == folks[0]);
			assertTrue(persisted[3] == folks[1]);
	
			assertEquals(2, target.count(new Search(Person.class).addFilterIn("firstName", "Gob", "Jean")));
		} finally {
			grandpaA.setFirstName(orig[0]);
			grandmaA.setFirstName(orig[1]);
		}
	}

	@Test
	public void testPersistOrMerge() {

		initDB();

		String[] orig = new String[] { grandpaA.getFirstName(), grandmaA.getFirstName() };

		try {
			grandpaA.setFirstName("GGG1");
			grandmaA.setFirstName("GGG2");

			// update
			Object[] persisted = target.persistOrMerge(Object.class, grandpaA.getHome().getAddress(), grandpaA.getHome(), grandpaA);
			assertTrue(persisted[0] != grandpaA.getHome().getAddress());
			assertTrue(persisted[1] != grandpaA.getHome());
			assertTrue(persisted[2] != grandpaA);

			assertTrue(target.persistOrMerge(grandmaA) != grandmaA);

			Person bob = new Person("Bob", "Loblaw");

			// save with null id
			assertTrue(target.persistOrMerge(bob) == bob);

			Person[] people = new Person[] { new Person("First", "Person"), new Person("Second", "Person") };

			// save with null id (multi)
			persisted = target.persistOrMerge(Object.class, (Object[]) people);
			assertTrue(persisted[0] == people[0]);
			assertTrue(persisted[1] == people[1]);
			assertTrue(persisted.getClass().equals(Object[].class));

			Search s = new Search(Person.class);
			s.addFilterIn("firstName", "GGG1", "GGG2", "Bob", "First", "Second");
			assertListEqual(new Person[] { grandpaA, grandmaA, bob, people[0], people[1] }, target.search(s));

			grandpaA.setFirstName("GGG3"); //will not be update in DB. grandpaA is not attached
			grandmaA.setFirstName("GGG4"); //will not be update in DB. grandmaA is not attached
			bob.setFirstName("Bobby");
			people[0].setFirstName("Firstly");
			people[1].setFirstName("Secondly");

			s.clear();
			s.addFilterIn("firstName", "GGG3", "GGG4", "Bobby", "Firstly", "Secondly");
			assertListEqual(new Person[] { bob, people[0], people[1] }, target.search(s));

			// save some update some, also multiple types
			people[1] = new Person("Miley", "Gordon");

			Pet[] pets = new Pet[4];
			pets[0] = catPrissy;
			pets[1] = target.find(Pet.class, catNorman.getId());
			pets[2] = new Pet("dog", "Mr.", "Waddlesworth", 42);
			pets[3] = new Pet("dog", "Mrs.", "Waddlesworth", 43);

			persisted = target.persistOrMerge(Object.class, people[0], pets[0], pets[1], people[1], pets[2], pets[3]);
			assertTrue(persisted[0] == people[0]);
			assertFalse(persisted[1] == pets[0]);
			assertTrue(persisted[2] == pets[1]);
			assertTrue(persisted[3] == people[1]);
			assertTrue(persisted[4] == pets[2]);
			assertTrue(persisted[5] == pets[3]);
			
			assertEquals(people[0], persisted[0]);
			assertEquals(pets[0], persisted[1]);
			assertEquals(pets[1], persisted[2]);
			assertEquals(people[1], persisted[3]);
			assertEquals(pets[2], persisted[4]);
			assertEquals(pets[3], persisted[5]);

			assertTrue(target.contains(persisted[0]));
			assertTrue(target.contains(persisted[1]));
			assertTrue(target.contains(persisted[2]));
			assertTrue(target.contains(persisted[3]));
			assertTrue(target.contains(persisted[4]));
			assertTrue(target.contains(persisted[5]));

		} finally {
			grandpaA.setFirstName(orig[0]);
			grandmaA.setFirstName(orig[1]);
		}
	}
	
	@Test
	public void testGetReference() {
		initDB();
		
		long maxPersonId = (Long) target.searchUnique(new Search(Person.class).addField("id", Field.OP_MAX));
		
		Person joeRef = target.getReference(Person.class, joeA.getId());
		assertEquals(joeA.getId(), joeRef.getId());
		assertEquals(joeA.getFirstName(), joeRef.getFirstName());
		
		Person[] refs = target.getReferences(Person.class, joeA.getId(), joeB.getId(), maxPersonId + 1);
		assertTrue(joeRef == refs[0]);
		assertEquals(joeB.getId(), refs[1].getId());
		assertEquals(joeB.getFirstName(), refs[1].getFirstName());
		
		try {
			refs[2].getFirstName();
			fail("An error should have been thrown for a reference to a non-existant entity.");
		} catch (EntityNotFoundException ex) { }
	}
	
	@Test
	public void testRefresh() {
		//This test isn't very complete. I don't have a way to alter the underlying data so I can refresh.
		Person p = copy(grandpaA);
		target.persist(p);
		target.refresh(p, p);
		p.getId();
	}

	@Test
	public void testFindMulti() {
		initDB();

		Search s = new Search(Person.class);
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("id", Field.OP_MAX);
		long maxId = (Long) target.searchUnique(s);

		Person[] people = target.find(Person.class, papaA.getId(), maxId + 1, papaB.getId());
		assertEquals(3, people.length);
		assertEquals(papaA.getId(), people[0].getId());
		assertEquals(papaA.getAge(), people[0].getAge());
		assertNull(people[1]);
		assertEquals(papaB.getId(), people[2].getId());
		assertEquals(papaB.getAge(), people[2].getAge());
	}

	@Test
	public void testRemoveMulti() {
		initDB();
		//remove all project member relationships to avoid integrity constraint violations
		for (Project project : target.all(Project.class)) {
			if (project.getMembers() != null)
				project.getMembers().clear();
		}
		target.flush();
		clear();
		
		
		Search s = new Search(Person.class);
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("id", Field.OP_MAX);
		long maxId = (Long) target.searchUnique(s);

		// delete unattached
		assertFalse(target.contains(joeA));
		assertFalse(target.contains(joeB));
		assertFalse(target.contains(sallyA));
		assertFalse(target.contains(margaretB));

		target.removeByIds(Person.class, joeA.getId(), null, joeB.getId(), maxId + 1);
		target.flush();
		
		assertNull(target.find(Person.class, joeA.getId()));
		assertFalse(target.contains(joeA));
		assertNull(target.find(Person.class, joeB.getId()));
		assertFalse(target.contains(joeB));

		target.removeEntities(sallyA, null, margaretB, catNorman);
		target.flush();
		
		assertNull(target.find(Person.class, sallyA.getId()));
		assertFalse(target.contains(sallyA));
		assertNull(target.find(Person.class, margaretB.getId()));
		assertFalse(target.contains(margaretB));
		assertNull(target.find(Pet.class, catNorman.getId()));
		assertFalse(target.contains(catNorman));

		// delete attached
		Person[] people = target.find(Person.class, papaA.getId(), papaB.getId(), mamaA.getId(), mamaB.getId());
		
		assertTrue(target.contains(people[0]));
		assertTrue(target.contains(people[1]));
		assertTrue(target.contains(people[2]));
		assertTrue(target.contains(people[3]));

		target.removeByIds(Person.class, people[0].getId(), null, people[1].getId(), (Long) maxId + 1);

		assertNull(target.find(Person.class, people[0].getId()));
		assertFalse(target.contains(people[0]));
		assertNull(target.find(Person.class,  people[1].getId()));
		assertFalse(target.contains(people[1]));

		target.removeEntities(people[2], people[3]);

		assertNull(target.find(Person.class, people[2].getId()));
		assertFalse(target.contains(people[2]));
		assertNull(target.find(Person.class, people[3].getId()));
		assertFalse(target.contains(people[3]));

	}

	@Test
	public void testExists() {
		initDB();

		Search s = new Search(Store.class);
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("id", Field.OP_MAX);
		long maxStoreId = (Long) target.searchUnique(s);

		s.setSearchClass(Recipe.class);
		long maxRecipeId = (Long) target.searchUnique(s);

		s.setSearchClass(Ingredient.class);
		long maxIngredientId = (Long) target.searchUnique(s);

		assertTrue(target.exists(Store.class, maxStoreId));
		assertFalse(target.exists(Store.class, maxStoreId + 1));

		Store store = target.find(Store.class, maxStoreId);
		assertTrue(target.exists(Store.class, maxStoreId));
		assertTrue(target.exists(store));

		boolean[] exists = target.exists(Store.class, maxStoreId, maxStoreId + 1);
		assertTrue(exists[0]);
		assertFalse(exists[1]);

		exists = target.exists(Recipe.class, maxRecipeId, maxRecipeId + 1);
		assertTrue(exists[0]);
		assertFalse(exists[1]);

		store = new Store();
		assertFalse(target.exists(store)); // id = 0

		store.setId(maxStoreId + 1);
		assertFalse(target.exists(store));

		store.setId(maxStoreId);
		assertTrue(target.exists(store));

		s.clear();
		s.setSearchClass(Ingredient.class);
		s.addSortAsc("name");
		List<Ingredient> ingredients = target.search(s);
		// Butter, Chicken, Flour, Salt, Sugar, Yeast

		// recipes:
		// Bread, Fried Chicken, Toffee

		assertTrue(target.exists(RecipeIngredient.class, recipes.get(0).getIngredients().iterator().next().getCompoundId()));
		assertTrue(target.exists(RecipeIngredient.class, new RecipeIngredientId(recipes.get(1), ingredients.get(1))));
		assertFalse(target.exists(RecipeIngredient.class, new RecipeIngredientId(recipes.get(1), ingredients.get(5))));

		assertTrue(target.exists(recipes.get(0).getIngredients().iterator().next()));

		RecipeIngredient ri = new RecipeIngredient();
		assertFalse(target.exists(ri));

		ri.setCompoundId(new RecipeIngredientId(recipes.get(1), ingredients.get(5)));
		assertFalse(target.exists(ri));

		ri.setCompoundId(new RecipeIngredientId(recipes.get(1), ingredients.get(1)));
		assertTrue(target.exists(ri));
	}

	@Test
	public void testCompoundId() {
		initDB();
		
		Search s = new Search(Recipe.class);
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("id", Field.OP_MAX);
		long maxRecipeId = (Long) target.searchUnique(s);

		s.setSearchClass(Ingredient.class);
		long maxIngredientId = (Long) target.searchUnique(s);

		s.clear();
		s.addSortAsc("name");
		List<Ingredient> ingredients = target.search(s);
		// Butter, Chicken, Flour, Salt, Sugar, Yeast

		// recipes:
		// Bread, Fried Chicken, Toffee		
		
		
		//save & update
		RecipeIngredient ri = new RecipeIngredient(recipes.get(0), ingredients.get(0), .125f, "cup"); //new
		RecipeIngredient ri2 = new RecipeIngredient(recipes.get(0), ingredients.get(1), .25f, "lb."); //new
		RecipeIngredient ri3 = new RecipeIngredient(recipes.get(2), ingredients.get(4), 2.25f, "cups"); //not new
		RecipeIngredient ri4 = new RecipeIngredient(recipes.get(1), ingredients.get(1), 3f, "lbs."); //not new
		
		assertTrue(target.persistOrMerge(ri) == ri);
		
		Object[] persisted = target.persistOrMerge(Object.class, ri2, ri3);
		assertTrue(persisted[0] == ri2);
		assertFalse(persisted[1] == ri3);
		
		assertFalse(target.persistOrMerge(ri4) == ri4);
		
		//get
		assertEquals(ri, target.find(RecipeIngredient.class, ri.getCompoundId()));
		assertEquals(ri2, target.find(RecipeIngredient.class, new RecipeIngredientId(recipes.get(0), ingredients.get(1))));
		
		Recipe r = new Recipe();
		r.setId(recipes.get(0).getId());
		Ingredient i = new Ingredient();
		i.setIngredientId(ingredients.get(0).getIngredientId());
		
		assertEquals(ri, target.find(RecipeIngredient.class, new RecipeIngredientId(r, i)));
		
		//search
		s.clear();
		s.setSearchClass(RecipeIngredient.class);
		s.setResultMode(Search.RESULT_SINGLE);
		s.addField("id");
		s.addSortAsc("id");
		s.addFilterEqual("id", ri.getCompoundId());
		
		assertEquals(ri.getCompoundId(), target.searchUnique(s));
		
		//exists (see exists test)
	}
	
	@Test
	public void testSearch() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addField("firstName");
		s.addFilterEqual("lastName", "Alpha");
		SearchResult result;
		
		assertListEqual(target.search(s), "Joe", "Sally", "Mama", "Papa", "Grandpa", "Grandma");
		assertEquals(6, target.count(s));
		result = target.searchAndCount(s);
		assertListEqual(result.getResult(), "Joe", "Sally", "Mama", "Papa", "Grandpa", "Grandma");
		assertEquals(6, result.getTotalCount());
		
		assertListEqual(target.search(Person.class, s), "Joe", "Sally", "Mama", "Papa", "Grandpa", "Grandma");
		assertEquals(6, target.count(Person.class, s));
		result = target.searchAndCount(Person.class, s);
		assertListEqual(result.getResult(), "Joe", "Sally", "Mama", "Papa", "Grandpa", "Grandma");
		assertEquals(6, result.getTotalCount());
		
		s.setSearchClass(null);
		assertListEqual(target.search(Person.class, s), "Joe", "Sally", "Mama", "Papa", "Grandpa", "Grandma");
		assertEquals(6, target.count(Person.class, s));
		result = target.searchAndCount(Person.class, s);
		assertListEqual(result.getResult(), "Joe", "Sally", "Mama", "Papa", "Grandpa", "Grandma");
		assertEquals(6, result.getTotalCount());
		
		s.setSearchClass(Recipe.class);
		try {
			target.search(Person.class, s);
			fail("An error should have been thrown");
		} catch (IllegalArgumentException ex) { }
		try {
			target.count(Person.class, s);
			fail("An error should have been thrown");
		} catch (IllegalArgumentException ex) { }
		try {
			target.searchAndCount(Person.class, s);
			fail("An error should have been thrown");
		} catch (IllegalArgumentException ex) { }
		
	}
	
	@Test
	public void testExample() {
		initDB();
		
		Person example = new Person();
		example.setFirstName("Joe");
		example.setLastName("Alpha");
		
		Search s = new Search(Person.class);
		s.addFilter(target.getFilterFromExample(example));
		assertEquals(joeA, target.searchUnique(s));
		
		example.setAge(0);
		s.clear();
		s.addFilter(target.getFilterFromExample(example));
		assertEquals(null, target.searchUnique(s));
		
		s.clear();
		s.addFilter(target.getFilterFromExample(example, new ExampleOptions().setExcludeZeros(true)));
		assertEquals(joeA, target.searchUnique(s));
	}
}
