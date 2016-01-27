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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.search.BaseSearchTest;
import test.googlecode.genericdao.search.InternalHelper;

import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

public class FieldAndFetchTest extends BaseSearchTest {
	
	protected InternalHelper internalHelper;
	
	@Autowired
	public void setInternalHelper(InternalHelper internalHelper) {
		this.internalHelper = internalHelper;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFetches() {
		initDB();
		List<Person> results;

		Search s = new Search(Person.class);
		s.addSortAsc("age");
		results = target.search(s);
		assertFalse(internalHelper.isEntityFetched(results.get(3).getHome()));

		s.addFetch("mother");
		results = target.search(s);
		assertFalse(internalHelper.isEntityFetched(results.get(3).getHome()));

		s.addFetch("father");

		s.addFetch("home.address");
		results = target.search(s);
		assertTrue(internalHelper.isEntityFetched(results.get(3).getHome()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFields() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterIn("id", joeA.getId(), margaretB.getId());
		s.addSortAsc("firstName");

		List<Object[]> resultArray;
		List<List<?>> resultList;
		List<Map<String, Object>> resultMap;

		s.addField("firstName", "first");
		s.addField("lastName");
		s.addField("age");
		s.addField(Field.ROOT_ENTITY); //same as s.addField("");

		s.setResultMode(Search.RESULT_ARRAY);
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Alpha", resultArray.get(0)[1]);
		assertEquals(10, resultArray.get(0)[2]);
		assertEquals(joeA, resultArray.get(0)[3]);
		assertEquals("Margaret", resultArray.get(1)[0]);
		assertEquals("Beta", resultArray.get(1)[1]);
		assertEquals(14, resultArray.get(1)[2]);
		assertEquals(margaretB, resultArray.get(1)[3]);

		s.setResultMode(Search.RESULT_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Alpha", resultList.get(0).get(1));
		assertEquals(10, resultList.get(0).get(2));
		assertEquals(joeA, resultList.get(0).get(3));
		assertEquals("Margaret", resultList.get(1).get(0));
		assertEquals("Beta", resultList.get(1).get(1));
		assertEquals(14, resultList.get(1).get(2));
		assertEquals(margaretB, resultList.get(1).get(3));

		s.setResultMode(Search.RESULT_MAP);
		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("first"));
		assertEquals("Alpha", resultMap.get(0).get("lastName"));
		assertEquals(10, resultMap.get(0).get("age"));
		assertEquals(joeA, resultMap.get(0).get(""));
		assertEquals("Margaret", resultMap.get(1).get("first"));
		assertEquals("Beta", resultMap.get(1).get("lastName"));
		assertEquals(14, resultMap.get(1).get("age"));
		assertEquals(margaretB, resultMap.get(1).get(""));

		s.clearFields();
		s.addField("firstName");

		s.setResultMode(Search.RESULT_ARRAY);
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("Joe", resultArray.get(0)[0]);
		assertEquals("Margaret", resultArray.get(1)[0]);

		s.setResultMode(Search.RESULT_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("Joe", resultList.get(0).get(0));
		assertEquals("Margaret", resultList.get(1).get(0));

		s.setResultMode(Search.RESULT_MAP);
		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Margaret", resultMap.get(1).get("firstName"));

		s.setResultMode(Search.RESULT_SINGLE);
		List<String> resultSingle = target.search(s);
		assertEquals(2, resultSingle.size());
		assertEquals("Joe", resultSingle.get(0));
		assertEquals("Margaret", resultSingle.get(1));

		s.clearFields();
		s.addField("home.type", "homeType");
		s.addField("father.mother.home.address.street");
		s.addField("firstName", "home.type");

		s.setResultMode(Search.RESULT_ARRAY);
		resultArray = target.search(s);
		assertEquals(2, resultArray.size());
		assertEquals("house", resultArray.get(0)[0]);
		assertEquals("3290 W Fulton", resultArray.get(0)[1]);
		assertEquals("Joe", resultArray.get(0)[2]);
		assertEquals("apartment", resultArray.get(1)[0]);
		assertEquals(null, resultArray.get(1)[1]);
		assertEquals("Margaret", resultArray.get(1)[2]);

		s.setResultMode(Search.RESULT_LIST);
		resultList = target.search(s);
		assertEquals(2, resultList.size());
		assertEquals("house", resultList.get(0).get(0));
		assertEquals("3290 W Fulton", resultList.get(0).get(1));
		assertEquals("Joe", resultList.get(0).get(2));
		assertEquals("apartment", resultList.get(1).get(0));
		assertEquals(null, resultList.get(1).get(1));
		assertEquals("Margaret", resultList.get(1).get(2));

		s.setResultMode(Search.RESULT_MAP);
		resultMap = target.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("house", resultMap.get(0).get("homeType"));
		assertEquals("3290 W Fulton", resultMap.get(0).get("father.mother.home.address.street"));
		assertEquals("Joe", resultMap.get(0).get("home.type"));
		assertEquals("apartment", resultMap.get(1).get("homeType"));
		assertEquals(null, resultMap.get(1).get("father.mother.home.address.street"));
		assertEquals("Margaret", resultMap.get(1).get("home.type"));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testColumnOperators() {
		initDB();

		Search s = new Search(Person.class);

		s.setResultMode(Search.RESULT_SINGLE);
		s.addFilterEqual("lastName", "Beta");

		// ages 10, 14, 38, 39

		s.addField("age", Field.OP_COUNT);
		assertEquals(4, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("age", Field.OP_COUNT_DISTINCT);
		assertEquals(4, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("age", Field.OP_MAX);
		assertEquals(39, target.searchUnique(s));

		s.clearFields();
		s.addField("age", Field.OP_MIN);
		assertEquals(10, target.searchUnique(s));

		s.clearFields();
		s.addField("age", Field.OP_SUM);
		assertEquals(101, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("age", Field.OP_AVG);
		assertEquals(25, Math.round((Double) target.searchUnique(s)));

		// 38, 38, 65

		s.clearFields();
		s.addField("mother.age", Field.OP_COUNT);
		assertEquals(3, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("mother.age", Field.OP_COUNT_DISTINCT);
		assertEquals(2, ((Number) target.searchUnique(s)).intValue());

		s.clearFields();
		s.addField("mother.age", Field.OP_SUM);
		assertEquals(141, ((Number) target.searchUnique(s)).intValue());

		s.setResultMode(Search.RESULT_ARRAY);
		s.clearFields();
		s.addField("age", Field.OP_SUM);
		s.addField("mother.age", Field.OP_SUM);
		Object[] arrayResult = (Object[]) target.searchUnique(s);
		assertEquals(101, ((Number) arrayResult[0]).intValue());
		assertEquals(141, ((Number) arrayResult[1]).intValue());

		s.setResultMode(Search.RESULT_LIST);
		List listResult = (List) target.searchUnique(s);
		assertEquals(101, ((Number) listResult.get(0)).intValue());
		assertEquals(141, ((Number) listResult.get(1)).intValue());

		s.setResultMode(Search.RESULT_MAP);
		Map mapResult = (Map) target.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("age")).intValue());
		assertEquals(141, ((Number) mapResult.get("mother.age")).intValue());

		s.setResultMode(Search.RESULT_MAP);
		s.clearFields();
		s.addField("age", Field.OP_SUM, "myAge");
		s.addField("mother.age", Field.OP_SUM, "myMomsAge");
		mapResult = (Map) target.searchUnique(s);
		assertEquals(101, ((Number) mapResult.get("myAge")).intValue());
		assertEquals(141, ((Number) mapResult.get("myMomsAge")).intValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDistinct() {
		initDB();

		Search s = new Search(Person.class);
		s.setDistinct(true);

		s.addFilterLessOrEqual("age", 15);
		assertEquals(4, target.count(s));
		assertListEqual(target.search(s), joeA, joeB, sallyA, margaretB);

		s.clear();
		s.setDistinct(true);
		s.addField("lastName");
		assertEquals(2, target.count(s));
		assertListEqual(target.search(s), "Alpha", "Beta");

		s.clear();
		s.setDistinct(true);
		s.addField("mother");
		assertEquals(3, target.count(s));
		assertListEqual(target.search(s), mamaA, mamaB, grandmaA);

		s.clearFields();
		s.addField("mother.firstName");
		s.addField("mother.lastName");
		try {
			assertEquals(4, target.count(s));
			fail();
		} catch (IllegalArgumentException ex) {
			// We don't support distinct counts with multiple fields at this
			// time
		}
		List<Object[]> results = target.search(s);
		List<String> results2 = new ArrayList<String>(results.size());
		for (Object[] a : results) {
			results2.add((String) a[0] + " " + (String) a[1]);
		}
		assertListEqual(results2, "null null", "Mama Alpha", "Mama Beta", "Grandma Alpha");

		// This is a miscellaneous test. When column operators are defined, the
		// count should always be 1.
		s.clear();
		s.addField("age", Field.OP_COUNT);
		assertEquals(1, target.count(s));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testResultModeAuto() {
		initDB();

		Search s = new Search(Person.class).addFilterEqual("firstName", "Margaret");

		// SINGLE
		assertEquals(margaretB, target.searchUnique(s));

		s.addField("firstName");
		assertEquals("Margaret", target.searchUnique(s));

		// ARRAY
		s.addField("lastName");
		Object[] strResults = (Object[]) target.searchUnique(s);
		assertEquals("Margaret", strResults[0]);
		assertEquals("Beta", strResults[1]);

		// MAP
		s.clearFields();
		s.addField("firstName", "fn");
		Map<String, Object> mapResults = (Map<String, Object>) target.searchUnique(s);
		assertEquals("Margaret", mapResults.get("fn"));

		s.addField("lastName");
		mapResults = (Map<String, Object>) target.searchUnique(s);
		assertEquals("Margaret", mapResults.get("fn"));
		assertEquals("Beta", mapResults.get("lastName"));
	}

	@Test
	public void testCombineFieldAndFetch() {
		initDB();

		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Sally");
		s.addField("mother");

		Person p = (Person) target.searchUnique(s);
		assertFalse(internalHelper.isEntityFetched(p.getHome()));

		s.addFetch("mother.home");
		p = (Person) target.searchUnique(s);
		assertTrue(internalHelper.isEntityFetched(p.getHome()));

		// with another field
		s.addField("mother.firstName");
		p = (Person) ((Object[]) target.searchUnique(s))[0];
		assertTrue(internalHelper.isEntityFetched(p.getHome()));

		// fetch with no applicable fields (it should ignore these. just make
		// sure no error is thrown)
		s.removeField("mother");
		target.searchUnique(s);

		s.clearFields();
		s.addField("age", Field.OP_MAX);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCustomField() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterEqual("id", joeA.getId());
		
		s.addField("{mother.firstName}||' '||{mother.lastName}", Field.OP_CUSTOM, "Yo Mama");
		s.addField("'Alpha'", Field.OP_CUSTOM, "String Constant");
		s.addField("24601", Field.OP_CUSTOM, "Numeric Constant");
		
		// Test with result array
		s.setResultMode(Search.RESULT_ARRAY);
		Object[] resultArray = (Object[]) target.searchUnique(s); 
		assertEquals("Mama Alpha", resultArray[0]);
		assertEquals("Alpha", resultArray[1]);
		assertEquals(24601, resultArray[2]);
		
		// Test with map to make sure it works with column aliases
		s.setResultMode(Search.RESULT_MAP);
		Map<String, Object> resultMap = (Map<String, Object>) target.searchUnique(s);
		assertEquals("Mama Alpha", resultMap.get("Yo Mama"));
		assertEquals("Alpha", resultMap.get("String Constant"));
		assertEquals(24601, resultMap.get("Numeric Constant"));
		
		// Test that auto mode works too.
		s.setResultMode(Search.RESULT_AUTO);
		resultMap = (Map<String, Object>) target.searchUnique(s);
		assertEquals("Mama Alpha", resultMap.get("Yo Mama"));
		assertEquals("Alpha", resultMap.get("String Constant"));
		assertEquals(24601, resultMap.get("Numeric Constant"));
		
		// Test for mixing properties
		s.addField("age");
		
		resultMap = (Map<String, Object>) target.searchUnique(s);
		assertEquals("Mama Alpha", resultMap.get("Yo Mama"));
		assertEquals("Alpha", resultMap.get("String Constant"));
		assertEquals(24601, resultMap.get("Numeric Constant"));
		assertEquals(10, resultMap.get("age"));
		
		// Test aggregation operators
		s.clear();
		s.addField("max({age})", Field.OP_CUSTOM);
		assertEquals(65, target.searchUnique(s));
		
		// Test mixing aggregation operators
		s.addField("firstName", Field.OP_MIN);
		resultArray = (Object[]) target.searchUnique(s);
		assertEquals(65, resultArray[0]);
		assertEquals("Grandma", resultArray[1]);
		
		// Test that if OP_CUSTOM is not specified, the search fails.
		s.clear();
		s.addField("{mother.firstName}||' '||{mother.lastName}");
		try {
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (RuntimeException ex) {
		}
		
		s.clearFields();
		s.addField("'Alpha'");
		try {
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (RuntimeException ex) {
		}
		
		s.clearFields();
		s.addField("24601");
		try {
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (RuntimeException ex) {
		}
	}
	
	@Test
	public void testCustomJoin() {
	    initDB();
        Search s = new Search(LimbedPet.class).addJoin("inner join {limbs} l").addFilterCustom("l = ?1", "right frontish leg");
                // addFilter(Filter.some("father", Filter.equal("lastName", "Beta")));

        assertEquals(target.count(s), 1);
        
        spiderJimmy = (LimbedPet) target.searchUnique(new Search(LimbedPet.class).addFilterEqual("species", "spider"));
        
        assertEquals(spiderJimmy, target.searchUnique(s));
	}

	
}
