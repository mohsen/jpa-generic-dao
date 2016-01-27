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
package test.googlecode.genericdao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.googlecode.genericdao.databaseinitializer.PersistableTestDataModel;
import test.googlecode.genericdao.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:jUnit-applicationContext.xml" })
public abstract class BaseTest extends PersistableTestDataModel {
	
	public <T> T find(Class<T> type, Serializable id) {
		return persistenceHelper.find(type, id);
	}

	public <T> T getProxy(Class<T> type, Serializable id) {
		return persistenceHelper.getProxy(type, id);
	}

	public void persist(Object entity) {
		persistenceHelper.persist(entity);
	}

	public void flush() {
		persistenceHelper.flush();
	}

	public void clear() {
		persistenceHelper.clear();
	}

	protected boolean dbIgnoresCase;

	@Autowired(required = true)
	public void setDbIgnoresCase(Boolean dbIgnoresCase) {
		this.dbIgnoresCase = dbIgnoresCase;
	}

	@Before
	public void onSetUp() throws Exception {
		resetModelObjects();
	}

	protected void initDB() {
		persistModelToDatabase();
	}

	protected void assertListEqual(Person[] expected, List<Person> actual) {
		Assert.assertEquals("The list did not have the expected length", expected.length, actual.size());

		HashMap<Long, Object> unmatched = new HashMap<Long, Object>();
		for (Person person : expected) {
			unmatched.put(person.getId(), "");
		}
		for (Person person : actual) {
			unmatched.remove(person.getId());
		}

		if (unmatched.size() != 0)
			Assert.fail("The list did not match the expected results.");
	}

	protected void assertListEqual(List<?> actual, Object... expected) {
		Assert.assertEquals("The list did not have the expected length", expected.length, actual.size());

		List<Object> remaining = new LinkedList<Object>();
		remaining.addAll(actual);

		for (Object o : expected) {
			if (!remaining.remove(o))
				Assert.fail("The list did not match the expected results.");
		}
	}

	protected void assertArrayEqual(Object[] actual, Object... expected) {
		Assert.assertEquals("The array did not have the expected length", expected.length, actual.length);

		List<Object> remaining = new LinkedList<Object>();
		for (Object o : actual) {
			remaining.add(o);
		}

		for (Object o : expected) {
			if (!remaining.remove(o))
				Assert.fail("The array did not match the expected results.");
		}
	}

	protected void assertListOrderEqual(Person[] expected, List<Person> actual) {
		Assert.assertEquals("The list did not have the expected length", expected.length, actual.size());

		for (int i = 0; i < expected.length; i++) {
			if (!expected[i].getId().equals(actual.get(i).getId()))
				Assert.fail("The list did not match the expected results.");
		}
	}

}
