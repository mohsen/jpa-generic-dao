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

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

public class SecurityTest extends BaseSearchTest {
	
	@Test
	public void testInjectionAttack() {
		Search s = new Search(Person.class);
		
		try {
			s.addField("address foo"); //spaces are not allowed
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.setResultMode(Search.RESULT_ARRAY);
			s.addField("firstName + lastName");
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addSortAsc("Mr. Friday");
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addFilterGreaterThan("age-1", 44);
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		try {
			s.clear();
			s.addFilterOr(Filter.equal("firstName", "Joe"), Filter.notEqual("age()", 44));
			target.search(s);
			fail("An exception should have been thrown.");
		} catch (IllegalArgumentException ex) {}
		
		//this shouldn't fail because property values are escaped
		s.clear();
		s.addFilterIn("firstName", "(select nonexistantProperty from Person)");
		target.search(s);
	}
}
