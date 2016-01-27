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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.search.BaseSearchTest;

import com.googlecode.genericdao.search.ExampleOptions;
import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

public class FilterTest extends BaseSearchTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testGeneral() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Joe");
		assertListEqual(new Person[] { joeA, joeB }, target.search(s));

		s.addFilterEqual("lastName", "Alpha");
		assertListEqual(new Person[] { joeA }, target.search(s));

		s.removeFiltersOnProperty("firstName");
		s.addFilterLessThan("age", 13);
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));

		s.addFilterGreaterOrEqual("age", 10);
		assertListEqual(new Person[] { joeA }, target.search(s));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOperators() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterEqual("lastName", "Beta");
		assertListEqual(new Person[] { joeB, margaretB, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterEqual("age", 10);
		assertListEqual(new Person[] { joeA, joeB }, target.search(s));

		s.clear();
		s.addFilterNotEqual("lastName", "Alpha");
		assertListEqual(new Person[] { joeB, margaretB, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterNotEqual("age", 10);
		assertListEqual(new Person[] { sallyA, margaretB, mamaA, papaA, mamaB, papaB, grandmaA, grandpaA }, target
				.search(s));

		s.clear();
		s.addFilterLike("firstName", "%pa");
		assertListEqual(new Person[] { papaA, papaB, grandpaA }, target.search(s));

		// -- LIKE is already case insensitive in some databases (MySQL)
		// while it is case sensitive in others (H2, HSQLDB, ProsgresSQL) --
		// so we won't include this part of the test for those that do ignore case
		if (!dbIgnoresCase) {
			s.clear();
			s.addFilterLike("firstName", "pA%");
			assertEquals("none should match because of case", 0, target.search(s).size());
		}

		s.clear();
		s.addFilterILike("firstName", "pA%");
		assertListEqual(new Person[] { papaA, papaB }, target.search(s));

		s.clear();
		s.addFilterLessThan("lastName", "Beta");
		assertListEqual(new Person[] { joeA, sallyA, papaA, mamaA, grandpaA, grandmaA }, target.search(s));

		s.clear();
		s.addFilterLessThan("lastName", "Alpha");
		assertListEqual(new Person[] {}, target.search(s));

		s.clear();
		s.addFilterLessThan("dob", mamaB.getDob());
		assertListEqual(new Person[] { papaA, papaB, mamaA, grandpaA, grandmaA }, target.search(s));

		s.clear();
		s.addFilterGreaterThan("lastName", "Beta");
		assertListEqual(new Person[] {}, target.search(s));

		s.clear();
		s.addFilterGreaterThan("lastName", "Alpha");
		assertListEqual(new Person[] { joeB, margaretB, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterGreaterThan("dob", mamaB.getDob());
		assertListEqual(new Person[] { joeA, joeB, sallyA, margaretB }, target.search(s));

		s.clear();
		s.addFilterLessOrEqual("age", 39);
		assertListEqual(new Person[] { joeA, joeB, sallyA, margaretB, mamaB, papaA, papaB }, target.search(s));

		s.clear();
		s.addFilterGreaterOrEqual("age", 39);
		assertListEqual(new Person[] { papaA, papaB, mamaA, grandmaA, grandpaA }, target.search(s));

		s.clear();
		s.addFilterIn("age", 9, 10, 14, 65);
		assertListEqual(new Person[] { sallyA, joeA, joeB, margaretB, grandmaA, grandpaA }, target.search(s));

		s.clear();
		s.addFilterIn("firstName", "Joe", "Papa");
		assertListEqual(new Person[] { joeA, joeB, papaA, papaB }, target.search(s));

		s.clear();
		s.addFilterNotIn("age", 9, 10, 14, 65);
		assertListEqual(new Person[] { papaA, mamaA, papaB, mamaB }, target.search(s));

		s.clear();
		s.addFilterNotIn("firstName", "Joe", "Papa", "Mama");
		assertListEqual(new Person[] { sallyA, margaretB, grandmaA, grandpaA }, target.search(s));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNesting() {
		initDB();

		Search s = new Search(Person.class);

		s.addFilterEqual("father.id", papaA.getId());
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));

		s.clear();
		s.addFilterEqual("father.firstName", "Papa");
		assertListEqual(new Person[] { joeA, sallyA, joeB, margaretB }, target.search(s));

		s.clear();
		s.addFilterEqual("father.firstName", "Grandpa");
		assertListEqual(new Person[] { papaA, mamaB }, target.search(s));

		s.clear();
		s.addFilterEqual("father.father.firstName", "Grandpa");
		assertListEqual(new Person[] { joeA, sallyA }, target.search(s));

		s.addFilterEqual("mother.father.firstName", "Grandpa");
		s.setDisjunction(true);
		assertListEqual(new Person[] { joeA, sallyA, joeB, margaretB }, target.search(s));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLogicOperators() {
		initDB();
		Search s = new Search(Person.class);

		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.greaterOrEqual("age", 10), Filter.lessThan("age", 20));
		assertListEqual(new Person[] { joeA }, target.search(s));

		s.clear();
		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.and(Filter.greaterOrEqual("age", 10), Filter.lessThan(
				"age", 20)));
		assertListEqual(new Person[] { joeA }, target.search(s));

		s.clear();
		s.addFilterAnd(Filter.equal("lastName", "Alpha"), Filter.or(Filter.lessOrEqual("age", 10), Filter.greaterThan(
				"age", 60)));
		assertListEqual(new Person[] { joeA, sallyA, grandmaA, grandpaA }, target.search(s));

		s.clear();
		s.addFilterNot(Filter.and(Filter.equal("lastName", "Alpha"), Filter.or(Filter.lessOrEqual("age", 10), Filter
				.greaterThan("age", 60))));
		assertListEqual(new Person[] { joeB, margaretB, papaA, papaB, mamaA, mamaB }, target.search(s));

		s.clear();
		s.addFilterOr(Filter.not(Filter.or(Filter.equal("firstName", "Joe"), Filter.equal("lastName", "Alpha"))),
				Filter.and(Filter.equal("firstName", "Papa"), Filter.equal("lastName", "Alpha")));
		assertListEqual(new Person[] { margaretB, papaB, mamaB, papaA }, target.search(s));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNull() {
		persist(grandpaA.getHome().getAddress());
		persist(grandpaA.getHome());
		persist(grandpaA);
		persist(spiderJimmy);

		Search s = new Search(Person.class);

		s.addFilterEqual("firstName", null);
		s.addFilterGreaterOrEqual("firstName", null);
		s.addFilterGreaterThan("firstName", null);
		s.addFilterLessOrEqual("firstName", null);
		s.addFilterLessThan("firstName", null);
		s.addFilterLike("firstName", null);
		s.addFilterILike("firstName", null);
		s.addFilterIn("firstName", (Object[]) null);
		s.addFilterIn("firstName", (Collection<?>) null);
		s.addFilterNotIn("firstName", (Object[]) null);
		s.addFilterNotIn("firstName", (Collection<?>) null);

		assertEquals(1, target.count(s));

		Filter filter;
		s.addFilter(filter = new Filter("firstName", null, Filter.OP_NOT_NULL));
		assertEquals(1, target.count(s));

		filter.setOperator(Filter.OP_NULL);
		assertEquals(0, target.count(s));

		// empty in and not in lists
		s.clear();
		s.addFilterIn("firstName"); // empty array
		assertEquals(0, target.count(s));

		s.clear();
		s.addFilterIn("firstName", new ArrayList<Object>(0)); // empty
		// collection
		assertEquals(0, target.count(s));

		s.clear();
		s.addFilterNotIn("firstName"); // empty array
		assertEquals(1, target.count(s));

		s.clear();
		s.addFilterNotIn("firstName", new ArrayList<Object>(0)); // empty
		// collection
		assertEquals(1, target.count(s));

		// test null/not null operators
		Person g2 = copy(grandmaA);
		g2.setFirstName(null);
		persist(g2);

		s.clear();
		s.addFilterNotNull("firstName");
		assertListEqual(new Person[] { grandpaA }, target.search(s));

		s.clear();
		s.addFilterNull("firstName");
		assertListEqual(new Person[] { g2 }, target.search(s));
	}

	@Test
	public void testExample() {
		initDB();

		ExampleOptions options = new ExampleOptions();
		Person person = new Person();
		Person person2 = new Person();

		// just id
		person.setId(joeA.getId());
		assertListEqual(findByExample(person, options), joeA);
		person.setId(null);

		// just first name
		person.setFirstName("Joe");
		assertListEqual(findByExample(person, options), joeA, joeB);

		// first name and age
		person.setAge(10);
		assertListEqual(findByExample(person, options), joeA, joeB);

		person.setAge(11);
		assertListEqual(findByExample(person, options));

		// first name and zero age (excludeZero option)
		person.setAge(0);
		assertListEqual(findByExample(person, options));

		options.setExcludeZeros(true);
		assertListEqual(findByExample(person, options), joeA, joeB);
		// parent with id
		person.setFather(papaA);
		assertListEqual(findByExample(person, options), joeA);

		// parent without id
		person2.setLastName("Alpha");
		person.setFather(person2);
		assertListEqual(findByExample(person, options), joeA);

		// null parents (excludeNull option)
		person = copy(joeA);
		person.setId(null);
		options.setExcludeNulls(false);
		options.excludeProp("id");
		options.excludeProp("home");
		assertListEqual(findByExample(person, options), joeA);

		person.setMother(null);
		assertListEqual(findByExample(person, options));

		// exclude property
		options = new ExampleOptions();
		options.excludeProp("father");
		options.excludeProp("lastName");
		options.excludeProp("home");
		options.excludeProp("weight");
		options.excludeProp("dob");
		assertListEqual(findByExample(person, options), joeA, joeB);

		options = new ExampleOptions();
		person = new Person();
		person2 = new Person();
		person2.setFirstName("Papa");
		person2.setLastName("Alpha");
		person.setFather(person2);
		assertListEqual(findByExample(person, options), joeA, sallyA);

		options.excludeProp("father.lastName");
		assertListEqual(findByExample(person, options), joeA, sallyA, joeB, margaretB);

		// like mode & ignore case
		options = new ExampleOptions();
		person = new Person();
		person.setLastName("Alpha");

		options.setLikeMode(ExampleOptions.ANYWHERE);
		person.setFirstName("ll");
		assertListEqual(findByExample(person, options), sallyA);
		person.setFirstName("LL");
		if (!dbIgnoresCase)
			assertListEqual(findByExample(person, options));
		options.setIgnoreCase(true);
		assertListEqual(findByExample(person, options), sallyA);

		options.setIgnoreCase(false);
		options.setLikeMode(ExampleOptions.END);
		person.setFirstName("ll");
		assertListEqual(findByExample(person, options));
		person.setFirstName("lly");
		assertListEqual(findByExample(person, options), sallyA);
		person.setFirstName("LLy");
		if (!dbIgnoresCase)
			assertListEqual(findByExample(person, options));
		options.setIgnoreCase(true);
		assertListEqual(findByExample(person, options), sallyA);
		person.setFirstName("LL");
		assertListEqual(findByExample(person, options));

		options.setIgnoreCase(false);
		options.setLikeMode(ExampleOptions.START);
		person.setFirstName("ll");
		assertListEqual(findByExample(person, options));
		person.setFirstName("Sal");
		assertListEqual(findByExample(person, options), sallyA);
		person.setFirstName("sAl");
		if (!dbIgnoresCase)
			assertListEqual(findByExample(person, options));
		options.setIgnoreCase(true);
		assertListEqual(findByExample(person, options), sallyA);
		person.setFirstName("LL");
		assertListEqual(findByExample(person, options));

	}

	@SuppressWarnings("unchecked")
	private List findByExample(Object example, ExampleOptions options) {
		Search s = new Search(example.getClass());
		s.addFilter(target.getFilterFromExample(example, options));
		return target.search(s);
	}

	@Test
	public void testCustomExpressions() {
		initDB();
		
		// Test single
		Search s = new Search(Person.class);
		s.addField("id");
		
		s.addFilterCustom("{firstName} = 'Sally'");
		assertEquals(sallyA.getId(), target.searchUnique(s));
		
		s.clearFilters();
		s.addFilterCustom("{father} is not null and {lastName} <> {father.lastName}");
		assertEquals(mamaB.getId(), target.searchUnique(s));
		
		// Test single with parameters
		s.clearFilters();
		s.addFilterCustom("{firstName} = ?1", "Sally");
		assertEquals(sallyA.getId(), target.searchUnique(s));
		
		s.clearFilters();
		s.addFilterCustom("{firstName} = ?1", Collections.singleton("Sally"));
		assertEquals(sallyA.getId(), target.searchUnique(s));

		s.clearFilters();
		s.addFilterCustom("{age} >= ?1 AND {age} < ?2", 40, 50);
		assertEquals(mamaA.getId(), target.searchUnique(s));
		
		s.clearFilters();
		s.addFilterCustom("{age} >= ?1 AND {age} < ?2", Arrays.asList(40, 50));
		assertEquals(mamaA.getId(), target.searchUnique(s));
		
		// Test multiple
		s.clearFilters();
		s.addFilterCustom("{firstName} = 'Joe'");
		s.addFilterCustom("{lastName} = 'Beta'");
		assertEquals(joeB.getId(), target.searchUnique(s));
		
		s.clearFilters();
		s.addFilterOr(
			Filter.custom("{age} > 100"),
			Filter.custom("1 = 1")
		);
		s.addFilterCustom("{firstName}||{lastName} = 'PapaBeta'");
		assertEquals(papaB.getId(), target.searchUnique(s));
		
		// Test multiple with parameters
		s.clearFilters();
		s.addFilterCustom("{firstName} = ?1", "Joe");
		s.addFilterCustom("{lastName} = ?1", "Beta");
		assertEquals(joeB.getId(), target.searchUnique(s));
		
		s.clearFilters();
		s.addFilterOr(
			Filter.custom("{age} > ?1", Collections.singleton(100)),
			Filter.custom("lower(?1) = ?2", Arrays.asList("Ice", "ice"))
		);
		s.addFilterCustom("{firstName}||{lastName} = ?1", "PapaBeta");
		assertEquals(papaB.getId(), target.searchUnique(s));
		
		// Test combination with other filters
		s.clearFilters();
		s.addFilterCustom("{firstName} = 'Joe'");
		s.addFilterEqual("lastName", "Beta");
		assertEquals(joeB.getId(), target.searchUnique(s));
		
		s.clearFilters();
		s.addFilterOr(
			Filter.greaterThan("age", 100),
			Filter.custom("1 = 1")
		);
		s.addFilterCustom("{firstName}||{lastName} = 'PapaBeta'");
		assertEquals(papaB.getId(), target.searchUnique(s));
		
		// Test combination with parameters
		s.clearFilters();
		s.addFilterCustom("{firstName} = ?1", "Joe");
		s.addFilterEqual("lastName", "Beta");
		assertEquals(joeB.getId(), target.searchUnique(s));
		
		s.clearFilters();
		s.addFilterOr(
			Filter.greaterThan("age", 100),
			Filter.custom("?1 = ?2 / ?3", Arrays.asList(4.0, 8.0, 2.0))
		);
		s.addFilterCustom("{firstName}||{lastName} = ?1", "PapaBeta");
		assertEquals(papaB.getId(), target.searchUnique(s));
		
		// Test sub-query
		s.clearFilters();
		s.addFilterCustom("{home} = ?1", grandmaA.getHome());
		s.addFilterCustom("{} in (select dude from Person dude where dude.isMale = 1)");
		assertEquals(grandpaA.getId(), target.searchUnique(s));
		
		// Test combination with fields
		s.clear();
		s.addField("{lastName}||', '||{firstName}", Field.OP_CUSTOM);
		s.addFilterCustom("{firstName}||{lastName} = ?1", "PapaBeta");
		assertEquals("Beta, Papa", target.searchUnique(s));
		
		// Test with invalid positional parameters
		/*
		 * I decided this should not be an exception case. It does not actually prevent the program from functioning.
		 * It is merely an indication that there may be a mistake in the usage. However, I can think of cases where
		 * it may be desirable to be able to include value parameters that are not actually used in the expression.
		 * For example, s.addFilterCustom("{firstName} = ?" + (isFemale ? "1" : "2"), "Sally", "Andy");
		 * This example is contrived, but it demonstrates a general class of potential cases where it might be desirable
		 * to have this option.
		s.clear();
		s.addFilterCustom("{firstName} = 'Sally'", "Andy");
		try {
			target.search(s);
			fail("There is an unexpected parameter, an exception should be thrown.");
		} catch (IllegalArgumentException ex) {
		}
		*/
		
		/*
		 * I don't think this should be an exception case. There problem is that specifying a custom filter with no
		 * values produces a state indistinguishable from setting a single NULL value. So in the off chance that
		 * someone actually intends to set a single value to null, I don't want to foil their plans with an unnecessary
		 * exception. Legitimate SQL example: "... where myFunc(x, ?1) = 3" -> "... where myFunc(x, NULL) = 3"
		s.clear();
		s.addFilterCustom("{firstName} = ?1");
		try {
			target.search(s);
			fail("There is a missing parameter, an exception should be thrown.");
		} catch (IllegalArgumentException ex) {
		}
		*/
		
		/*
		 * I decided this should not be an exception case. It does not actually prevent the program from functioning.
		 * It is merely an indication that there may be a mistake in the usage. However, I can think of cases where
		 * it may be desirable to be able to include value parameters that are not actually used in the expression.
		 * For example, s.addFilterCustom("{firstName} = ?" + (isFemale ? "1" : "2"), "Sally", "Andy");
		 * This example is contrived, but it demonstrates a general class of potential cases where it might be desirable
		 * to have this option.
		s.clear();
		s.addFilterCustom("{firstName} = ?1", "Sally", "Andy");
		try {
			target.search(s);
			fail("There is an unexpected parameter, an exception should be thrown.");
		} catch (IllegalArgumentException ex) {
		}
		*/
		
		// Test nulls, sets, arrays
		s.clear();
		s.addFilter(new Filter(null, null, Filter.OP_CUSTOM));
		try {
			target.search(s);
			fail("The property is null; an exception should be thrown.");
		} catch (IllegalArgumentException ex) {
		}
		
		s.clear();
		s.addFilter(new Filter("", null, Filter.OP_CUSTOM));
		try {
			target.search(s);
			fail("The property is empty; an exception should be thrown.");
		} catch (IllegalArgumentException ex) {
		}
		
		s.clear();
		/*
		 * This was the original intent for this test, but it won't actually work because x = NULL is not valid SQL. 
		s.addFilter(new Filter("{father} = ?1", null, Filter.OP_CUSTOM));
		*/
		s.addFilter(new Filter("{father} is null", null, Filter.OP_CUSTOM));
		assertListEqual(target.search(s), mamaA, papaB, grandmaA, grandpaA);
		
		s.clear();
		s.addFilter(new Filter("{firstName} = ?1", "Sally", Filter.OP_CUSTOM));
		assertListEqual(target.search(s), sallyA);
		
		s.clear();
		s.addFilter(new Filter("{firstName} = ?1", new String[] { "Sally" }, Filter.OP_CUSTOM));
		assertListEqual(target.search(s), sallyA);
		
		s.clear();
		s.addFilter(new Filter("{firstName} = ?1", Collections.singleton("Sally"), Filter.OP_CUSTOM));
		assertListEqual(target.search(s), sallyA);
		
		
		// Test that no other operator allows custom expressions
		try {
			target.search(new Search(Person.class).addFilterEmpty("{firstName}"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterEqual("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterGreaterOrEqual("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterGreaterThan("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterILike("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
			
		}
		
		try {
			target.search(new Search(Person.class).addFilterLessOrEqual("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
			
		}
		
		try {
			target.search(new Search(Person.class).addFilterLessThan("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterLike("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterIn("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterNotEmpty("{firstName}"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterNotEqual("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterNotIn("{firstName}", "Joe"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterNotNull("{firstName}"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterNull("{firstName}"));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterAll("{firstName}", Filter.equal("firstName", "Joe")));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterNone("{firstName}", Filter.equal("firstName", "Joe")));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		try {
			target.search(new Search(Person.class).addFilterSome("{firstName}", Filter.equal("firstName", "Joe")));
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
	}
	
}
