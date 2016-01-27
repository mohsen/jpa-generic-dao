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

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.search.BaseSearchTest;

import javax.persistence.NonUniqueResultException;

import org.junit.Test;

import java.util.List;

public class PagingAndSortingTest extends BaseSearchTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testBasicPaging() {
		initDB();

		Search s = new Search(Person.class);
		s.addSortAsc("lastName");
		s.addSortAsc("firstName");

		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA, sallyA, joeB, mamaB, margaretB, papaB },
				target.search(s));

		s.setMaxResults(3);
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA }, target.search(s));

		s.setFirstResult(4);
		assertListEqual(new Person[] { papaA, sallyA, joeB }, target.search(s));

		s.setMaxResults(-1);
		assertListEqual(new Person[] { papaA, sallyA, joeB, mamaB, margaretB, papaB }, target.search(s));

		s.setMaxResults(4);
		s.setPage(1);
		s.setFirstResult(2); // first result should override page
		assertListEqual(new Person[] { joeA, mamaA, papaA, sallyA }, target.search(s));

		s.setFirstResult(-1);
		assertListEqual(new Person[] { papaA, sallyA, joeB, mamaB }, target.search(s));

		s.setPage(0);
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA }, target.search(s));

		s.setPage(2);
		assertListEqual(new Person[] { margaretB, papaB }, target.search(s));

		s.clearPaging();
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA, sallyA, joeB, mamaB, margaretB, papaB },
				target.search(s));

		s.setPage(1); // page should have no effect when max results is not
		// set
		assertListEqual(new Person[] { grandmaA, grandpaA, joeA, mamaA, papaA, sallyA, joeB, mamaB, margaretB, papaB },
				target.search(s));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSorting() {
		initDB();

		// test single sort
		Search s = new Search(Person.class);
		// remove duplicate ages for ease of testing
		s.addFilterNotIn("id", grandmaA.getId(), joeB.getId(), papaB.getId());

		s.addSortAsc("age");
		assertListOrderEqual(new Person[] { sallyA, joeA, margaretB, mamaB, papaA, mamaA, grandpaA }, target.search(s));

		s.clearSorts();
		s.addSortDesc("age");
		assertListOrderEqual(new Person[] { grandpaA, mamaA, papaA, mamaB, margaretB, joeA, sallyA }, target.search(s));

		s.removeSort("age");
		s.addSortAsc("dob");
		assertListOrderEqual(new Person[] { grandpaA, mamaA, papaA, mamaB, margaretB, joeA, sallyA }, target.search(s));

		// Test nested sort
		s.clear();
		s.addFilterIn("id", sallyA.getId(), mamaB.getId(), grandmaA.getId());

		s.addSortAsc("home.address.street");
		assertListOrderEqual(new Person[] { grandmaA, mamaB, sallyA }, target.search(s));

		// Test multiple sort
		s.clearFilters();
		s.addSort("firstName", false);
		assertListOrderEqual(new Person[] { grandmaA, grandpaA, joeB, mamaB, margaretB, papaB, joeA, mamaA, papaA,
				sallyA }, target.search(s));

		// Test ignore case
		s.clear();
		// Set Margaret's first name to "margaret" (lowercase). When ignoring
		// case, Margaret < Sally, but when taking case into account,
		// Sally < margaret
		find(Person.class, margaretB.getId()).setFirstName("margaret");
		s.addFilterIn("id", margaretB.getId(), sallyA.getId());

		List<Person> results;
		// without ignore case
		// [some databases (i.e. MySQL) automatically ignore case for ordering,
		// so we won't include this test for those]
		if (!dbIgnoresCase) {
			s.addSortAsc("firstName");
			results = target.search(s);
			assertEquals(sallyA.getId(), results.get(0).getId());
			assertEquals(margaretB.getId(), results.get(1).getId());
		}

		// with ignore case
		s.removeSort("firstName");
		s.addSortAsc("firstName", true);
		results = target.search(s);
		assertEquals(margaretB.getId(), results.get(0).getId());
		assertEquals(sallyA.getId(), results.get(1).getId());

	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomSorts() {
		initDB();

		Search s = new Search(Person.class);
		// remove duplicate ages for ease of testing
		s.addFilterNotIn("id", grandmaA.getId(), joeB.getId(), papaB.getId());
		// Folks: sallyA, joeA, margaretB, mamaB, papaA, mamaA, grandpaA
		// Ages:  9       10    14         38     39     40     65
		
		// Test simple sorting 
		s.addSort(Sort.customExpressionAsc("{age}"));
		assertListOrderEqual(new Person[] { sallyA, joeA, margaretB, mamaB, papaA, mamaA, grandpaA }, target.search(s));

		s.clearSorts();
		s.addSort(Sort.customExpressionDesc("{age}"));
		assertListOrderEqual(new Person[] { grandpaA, mamaA, papaA, mamaB, margaretB, joeA, sallyA }, target.search(s));
		
		// Test sorting with more complex expression
		s.clearSorts();
		s.addSort(Sort.customExpressionAsc("abs(40 - {age})"));
		assertListOrderEqual(new Person[] { mamaA, papaA, mamaB, grandpaA, margaretB, joeA, sallyA }, target.search(s));
		
		s.clearSorts();
		s.addSort(Sort.customExpressionDesc("abs(40 - {age})"));
		assertListOrderEqual(new Person[] { sallyA, joeA, margaretB, grandpaA, mamaB, papaA, mamaA }, target.search(s));
		
		// Test multiple custEx sorts
		s.clearSorts();
		s.addSort(Sort.customExpressionAsc("{lastName}"));
		s.addSort(Sort.customExpressionAsc("abs(40 - {age})"));
		assertListOrderEqual(new Person[] { mamaA, papaA, grandpaA, joeA, sallyA, mamaB, margaretB }, target.search(s));
		
		// Test mix of custEx and regular sorts
		s.clearSorts();
		s.addSort(Sort.customExpressionAsc("lastName"));
		s.addSort(Sort.customExpressionAsc("abs(40 - {age})"));
		assertListOrderEqual(new Person[] { mamaA, papaA, grandpaA, joeA, sallyA, mamaB, margaretB }, target.search(s));
		
		// Test expression w/o custEx flag
		s.clearSorts();
		s.addSort(Sort.asc("{lastName}"));
		try {
			target.search(s);
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}
		
		s.clearSorts();
		s.addSort(Sort.desc("abs(40 - {age})"));
		try {
			target.search(s);
			fail("Invalid characters in property name, an exception should be thrown.");
		} catch (RuntimeException ex) {
		}	
		
		// Test custEx and ignore case (ignore case is "ignored" with custEx)

		// [some databases (i.e. MySQL) automatically ignore case for ordering,
		// so we won't include this test for those]
		if (!dbIgnoresCase) {
			// Set Margaret's first name to "margaret" (lowercase). When ignoring
			// case, Margaret < Sally, but when taking case into account,
			// Sally < margaret
			find(Person.class, margaretB.getId()).setFirstName("margaret");
			s.addFilterIn("id", margaretB.getId(), sallyA.getId());
			
			Sort sort = new Sort(true, "{firstName}", false);
			sort.setIgnoreCase(true);
			s.clearSorts();
			s.addSort(sort);
			
			// Even though we set ignore case, case should not be ignored
			// because ignore case is not used with a custom expression.
			List<Person> results = target.search(s);
			assertEquals(sallyA.getId(), results.get(0).getId());
			assertEquals(margaretB.getId(), results.get(1).getId());
		}
	}

	@Test
	public void testPagingWithSearchUnique() {
        initDB();

        //searchUnique with paging
		// First test the filters, must return Bob and Fred
		Search s = new Search(Person.class);
		s.addFilterEqual("lastName", "Beta");
		s.addSortAsc("firstName");
		assertListEqual(new Person[] { joeB, mamaB, margaretB, papaB }, target.search(s));
		// Then test to order by firstName in Asc order and limit results to 1, must return Bob
		try {
            s.setMaxResults(1);
            assertEquals(joeB, target.searchUnique(s));
		} catch (NonUniqueResultException e) {
            fail("searchUnique should apply paging to the results.");
        }
		// Then execute the same test, but in Desc order, must return Fred
		try {
            s.clearSorts();
            s.addSortDesc("firstName");
            s.setMaxResults(1);
            assertEquals(papaB, target.searchUnique(s));
		} catch (NonUniqueResultException e) {
            fail("searchUnique should apply paging to the results.");
        }

    }

}
