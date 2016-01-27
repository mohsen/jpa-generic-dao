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
package test.googlecode.genericdao.dao.hibernate.dao;

import java.util.List;

import test.googlecode.genericdao.model.Person;

import com.googlecode.genericdao.search.Search;

public class PersonDAOImpl extends BaseDAOImpl<Person, Long> implements PersonDAO {

	public List<Person> findByName(String firstName, String lastName) {
		//If firstName or lastName are null, the corresponding filter will be ignored.
		return search(new Search().addFilterEqual("firstName", firstName).addFilterEqual("lastName", lastName));
	}

}
