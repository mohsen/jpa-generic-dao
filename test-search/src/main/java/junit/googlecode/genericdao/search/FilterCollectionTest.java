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
package junit.googlecode.genericdao.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import test.googlecode.genericdao.model.Home;
import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;
import test.googlecode.genericdao.model.Store;
import test.googlecode.genericdao.search.BaseSearchTest;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

public class FilterCollectionTest extends BaseSearchTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testCollectionFilters() {
		initDB();
		
		Search s = new Search(Home.class);
		s.addFilterSome("residents", Filter.equal("lastName", "Beta"));
		
		List<Home> homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeB.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterSome("residents", Filter.and(Filter.equal("firstName", "Joe"), Filter.equal("lastName", "Alpha")));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeA.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterSome("residents", Filter.equal("father.father.lastName", "Alpha"));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeA.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterAll("residents", Filter.greaterThan("age", 15));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(grandpaA.getHome().getId(), homeResults.get(0).getId());
		
		
		s.clearFilters();
		s.addFilterAll("residents", Filter.like("home.address.street", "%Fulton"));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(grandpaA.getHome().getId(), homeResults.get(0).getId());
		

		// the null problem
		s.clearFilters();
		s.addFilterAll("residents", Filter.greaterThan("father.age", 50));
		assertEquals(0, target.search(s).size());
		
		s.clearFilters();
		s.addFilterNone("residents", Filter.equal("lastName", "Alpha"));
		
		homeResults = target.search(s);
		assertEquals(1, homeResults.size());
		assertEquals(joeB.getHome().getId(), homeResults.get(0).getId());
		
		//TODO test nested collection filters
		
		
		//value collection
		s.clear();
		s.setSearchClass(LimbedPet.class);
		s.addFilterSome("limbs", Filter.equal(Filter.ROOT_ENTITY, "left front leg"));
		assertEquals(3, target.count(s));
		
		s.clear();
		s.addFilterSome("limbs", Filter.equal(null, "left frontish leg"));
		assertListEqual(target.search(s), spiderJimmy);
		
		s.clear();
		s.addFilterAll("limbs", Filter.notEqual(null, "left frontish leg"));
		assertListEqual(target.search(s), catNorman, catPrissy);
		
		s.clear();
		s.addFilterNone("limbs", Filter.equal(null, "left frontish leg"));
		assertListEqual(target.search(s), catNorman, catPrissy);
	}
	
	@Test
	public void testEmpty() {
		initDB();
		
		//with entity
		Search s = new Search(Person.class);
		s.addFilterEmpty("father");
		assertListEqual(target.search(s), grandpaA, grandmaA, mamaA, papaB);

		s.clear();
		s.addFilterEmpty("father.id");
		assertListEqual(target.search(s), grandpaA, grandmaA, mamaA, papaB);
		
		s.clear();
		s.addFilterNotEmpty("father");
		assertListEqual(target.search(s), mamaB, papaA, joeA, joeB, margaretB, sallyA);
		
		//with int and string value
		Person pete = find(Person.class, papaA.getId());
		pete.setAge(null);
		pete.setFirstName(null);
		pete.setLastName("");
		
		s.clear();
		s.addFilterEmpty("age");
		assertListEqual(target.search(s), pete);
		
		s.clear();
		s.addFilterEmpty("firstName");
		assertListEqual(target.search(s), pete);

		s.clear();
		s.addFilterEmpty("lastName");
		assertListEqual(target.search(s), pete);
		
		s.clear();
		s.addFilterNotEmpty("lastName");
		assertListEqual(target.search(s), grandmaA, grandpaA, papaB, mamaA, mamaB, joeA, joeB, sallyA, margaretB);
		
		pete.setAge(0);
		s.clear();
		s.addFilterEmpty("age");
		assertListEqual(target.search(s)); //no matches, 0 is not empty
		
		//with value collection
		s.clear();
		s.setSearchClass(LimbedPet.class);
		s.addFilterEmpty("limbs");
		assertListEqual(target.search(s)); //no results
		
		LimbedPet cat = find(LimbedPet.class, catPrissy.getId());
		cat.setLimbs(new ArrayList<String>(0));
		
		assertListEqual(target.search(s), cat);
		
		cat.setLimbs(null);
		
		assertListEqual(target.search(s), cat);
		
		//with one-to-many
		
		Recipe air = new Recipe();
		air.setTitle("Air");
		air.setIngredients(new HashSet<RecipeIngredient>());
		persist(air);
		
		s.clear();
		s.setSearchClass(Recipe.class);
		s.addFilterEmpty("ingredients");
		assertListEqual(target.search(s), air);
		
		s.clear();
		s.addFilterNotEmpty("ingredients");
		assertListEqual(target.search(s), recipes.toArray());
		
		//with many-to-many
		
		Store emptyStore = new Store();
		emptyStore.setName("Empty Store");
		persist(emptyStore);
		
		s.clear();
		s.setSearchClass(Store.class);
		s.addFilterEmpty("ingredientsCarried");
		assertListEqual(target.search(s), emptyStore);
		
		s.clear();
		s.addFilterNotEmpty("ingredientsCarried");
		assertListEqual(target.search(s), stores.toArray());		
	}
	
	@Test
	public void testSizeProperty() {
		initDB();
		
		Search s = new Search(LimbedPet.class);
		s.addFilterEqual("limbs.size", 8);
		assertListEqual(target.search(s), spiderJimmy);
		
		s.clear();
		s.addFilterLessThan("limbs.size", 6);
		assertListEqual(target.search(s), catNorman, catPrissy);
		
		s.clear();
		s.setSearchClass(Person.class);
		s.addFilterGreaterThan("home.residents.size", 2);
		assertListEqual(target.search(s), papaA, papaB, mamaA, mamaB, sallyA, margaretB, joeA, joeB);
	}

}
