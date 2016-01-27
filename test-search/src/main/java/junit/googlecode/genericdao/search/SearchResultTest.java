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

import org.junit.Test;

import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.search.BaseSearchTest;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

public class SearchResultTest extends BaseSearchTest {
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterLessThan("lastName", "Balloons");
		
		assertEquals(6, target.count(s));
		
		SearchResult<Person> result = target.searchAndCount(s);
		assertEquals(6, result.getTotalCount());
		assertListEqual(new Person[] { joeA, sallyA, papaA, mamaA, grandpaA, grandmaA }, result.getResult());
	}
	
}
