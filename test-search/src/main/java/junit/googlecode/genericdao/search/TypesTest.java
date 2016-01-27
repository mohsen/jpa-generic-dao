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

import java.util.List;

import org.junit.Test;

import test.googlecode.genericdao.model.Ingredient;
import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Pet;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;
import test.googlecode.genericdao.search.BaseSearchTest;

import com.googlecode.genericdao.search.Search;

public class TypesTest extends BaseSearchTest {
	
	@Test
	public void testComponent() {
		initDB();
		
		Search s = new Search(Pet.class);

		s.addFilterGreaterThan("ident.idNumber", 3333);
		s.addField("ident.name.first");
		s.setResultMode(Search.RESULT_SINGLE);
		assertEquals(spiderJimmy.getIdent().getName().getFirst(), target.searchUnique(s));
		
		s.clear();
		s.addFilterEqual("ident.name.first", "Miss");
		s.addField("ident.idNumber");
		s.setResultMode(Search.RESULT_SINGLE);
		assertEquals(catPrissy.getIdent().getIdNumber(), target.searchUnique(s));
		
		s.clear();
		s.addFilterEqual("favoritePlaymate.ident.name.first", "Jimmy");
		s.addFilterEqual("species", "cat");
		s.addField("ident.name.first");
		s.setResultMode(Search.RESULT_SINGLE);
		assertEquals(catNorman.getIdent().getName().getFirst(), target.searchUnique(s));
		
		//many-to-many ids
		//for example, querying on student.studentTeacher.id.teacher.firstName
	}
	
	@Test
	public void testIdProperty() {
		initDB();
		
		Recipe toffee = recipes.get(2);
		Ingredient sugar = (Ingredient) target.searchUnique(new Search(Ingredient.class).addFilterEqual("name", "Sugar"));
		
		Search s = new Search(RecipeIngredient.class);
		
		s.addField("id.recipe.id");
		s.addFilterEqual("id.ingredient.id", sugar.getIngredientId());
		assertListEqual(target.search(s), toffee.getId());
		
		s.clear();
		s.addField("id.recipe.title");
		s.addFilterEqual("id.ingredient.name", "Salt");
		assertListEqual(target.search(s), "Fried Chicken", "Bread");
		
		s.clear();
		s.addField("compoundId.recipe.id");
		s.addFilterEqual("compoundId.ingredient.ingredientId", sugar.getIngredientId());
		assertListEqual(target.search(s), toffee.getId());
		
		s.clear();
		s.addField("compoundId.recipe.title");
		s.addFilterEqual("compoundId.ingredient.name", "Salt");
		assertListEqual(target.search(s), "Fried Chicken", "Bread");
		
		s.clear();
		s.setSearchClass(Person.class);
		s.addField("mother.father.id");
		s.addFilterEqual("father.id", papaB.getId());
		assertListEqual(target.search(s), grandpaA.getId(), grandpaA.getId());
	}
	
	@Test
	public void testClassProperty() {
		initDB();
		
		Search s = new Search(Pet.class);
		s.addFilterEqual("class", LimbedPet.class);
		assertListEqual(target.search(s), catNorman, catPrissy, spiderJimmy);
		
		s.clear();
		s.addFilterNotEqual("class", LimbedPet.class.getName());
		assertListEqual(target.search(s), fishWiggles);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testPlymorphism() {
		initDB();
		
		Search s = new Search(Pet.class);
		List<Pet> result = target.search(s);
		assertEquals(4, result.size());
		
		s.setSearchClass(LimbedPet.class);
		result = target.search(s);
		assertEquals(3, result.size());
	}
	
	@Test
	public void testBlankProperty() {
		initDB();
		
		Search s = new Search(Person.class);
		s.addFilterEqual("", grandpaA);
		assertListEqual(target.search(s), grandpaA);
		
		s.clear();
		s.addFilterNotEqual(null, grandmaA);
		s.addFilterGreaterOrEqual("age", 55);
		assertListEqual(target.search(s), grandpaA);
		
		s.clear();
		s.addFilterIn(null, joeA, joeB, sallyA, margaretB);
		s.addFilterGreaterOrEqual("age", 10);
		assertListEqual(target.search(s), joeA, joeB, margaretB);
		
		s.clear();
		s.setSearchClass(Pet.class);
		s.addFilterEqual("class", "test.googlecode.genericdao.model.LimbedPet");
		assertListEqual(target.search(s), spiderJimmy, catNorman, catPrissy);
	}
}
