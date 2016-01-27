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

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;
import test.googlecode.genericdao.search.BaseSearchTest;

import com.googlecode.genericdao.search.Metadata;
import com.googlecode.genericdao.search.MetadataUtil;

public class MetadataTest extends BaseSearchTest {
	
	protected MetadataUtil metadataUtil;
	
	@Autowired
	public void setMetadataUtil(MetadataUtil metadataUtil) {
		this.metadataUtil = metadataUtil;
	}

	@Test
	public void testProperties() {
		Metadata md = metadataUtil.get(Person.class);
		Metadata md2 = md.getPropertyType("home");
		Metadata md3 = metadataUtil.get(RecipeIngredient.class);
		Metadata md4 = md3.getIdType();
		
		assertArrayEqual(md.getProperties(), "id", "firstName", "lastName", "age", "dob", "father", "mother", "isMale", "weight", "home");
		assertArrayEqual(md2.getProperties(), "id", "type", "address", "residents");
		assertArrayEqual(md3.getProperties(), "compoundId", "amount", "measure");
		assertArrayEqual(md4.getProperties(), "recipe", "ingredient");
		
		assertFalse(md2.getPropertyType("address").isCollection());
		assertFalse(md2.getPropertyType("address").isString());
		assertFalse(md2.getPropertyType("address").isNumeric());
		assertTrue(md2.getPropertyType("address").isEntity());
		assertFalse(md2.getPropertyType("address").isEmeddable());
		
		assertFalse(md2.getPropertyType("type").isCollection());
		assertTrue(md2.getPropertyType("type").isString());
		assertFalse(md2.getPropertyType("type").isNumeric());
		assertFalse(md2.getPropertyType("type").isEntity());
		assertFalse(md2.getPropertyType("type").isEmeddable());
		
		assertFalse(md.getPropertyType("age").isCollection());
		assertFalse(md.getPropertyType("age").isString());
		assertTrue(md.getPropertyType("age").isNumeric());
		assertFalse(md.getPropertyType("age").isEntity());
		assertFalse(md.getPropertyType("age").isEmeddable());
		
		assertFalse(md3.getPropertyType("compoundId").isCollection());
		assertFalse(md3.getPropertyType("compoundId").isString());
		assertFalse(md3.getPropertyType("compoundId").isNumeric());
		assertFalse(md3.getPropertyType("compoundId").isEntity());
		assertTrue(md3.getPropertyType("compoundId").isEmeddable());
		
		assertFalse(md4.getPropertyType("recipe").isCollection());
		assertFalse(md4.getPropertyType("recipe").isString());
		assertFalse(md4.getPropertyType("recipe").isNumeric());
		assertTrue(md4.getPropertyType("recipe").isEntity());
		assertFalse(md4.getPropertyType("recipe").isEmeddable());
		
		assertFalse(md.getPropertyType("id").isCollection());
		assertFalse(md.getPropertyType("id").isString());
		assertTrue(md.getPropertyType("id").isNumeric());
		assertFalse(md.getPropertyType("id").isEntity());
		assertFalse(md.getPropertyType("id").isEmeddable());
	}
	
	@Test
	public void testIds() {
		Metadata md = metadataUtil.get(Person.class);
		Metadata md2 = md.getPropertyType("home");
		Metadata md3 = metadataUtil.get(RecipeIngredient.class);
		Metadata md4 = md3.getIdType();
		
		assertEquals("id", md.getIdProperty());
		assertEquals("id", md2.getIdProperty());
		assertEquals("compoundId", md3.getIdProperty());
		assertEquals(null, md4.getIdProperty());
		
		assertEquals(Long.class, md.getIdType().getJavaClass());
		assertEquals(null, md4.getIdType());
	}
	
	@Test
	public void testGetPropertyValueOnUnpersistedEntity() {
		assertNull(joeA.getId()); // NOTE: with the test objects not persisted to the DB, the ids are still null.
		testGetPropertyValue(joeA, recipes.get(0));
	}
	
	@Test
	public void testGetPropertyValueOnEntityUnconnectedToORM() {
		initDB();
		assertNotNull(joeA.getId());
		testGetPropertyValue(joeA, recipes.get(0));
	}
	
	@Test
	public void testGetPropertyValueOnEntityFetchedFromORM() {
		initDB();
		
		Person joeFromDatabase = find(Person.class, joeA.getId());
		Recipe recipeFromDatabase = find(Recipe.class, recipes.get(0).getId());
		
		testGetPropertyValue(joeFromDatabase, recipeFromDatabase);
	}
	
	@Test @Ignore("Metadata cannot get value of property from an unloaded lazy-loading object. This is an acceptable limitation.")
	public void testGetPropertyValueOnEntityFetchedFromORMAsProxy() {
		initDB();
		
		Person joeFromDatabase = getProxy(Person.class, joeA.getId());
		Recipe recipeFromDatabase = getProxy(Recipe.class, recipes.get(0).getId());
		
		testGetPropertyValue(joeFromDatabase, recipeFromDatabase);
	}
	
	protected void testGetPropertyValue(Person person, Recipe recipe) {
		Metadata md_Person = metadataUtil.get(Person.class);
		Metadata md_RecipeIngredient = metadataUtil.get(RecipeIngredient.class);
		Metadata md_RecipeIngredientId = md_RecipeIngredient.getIdType();
		
		assertEquals(person.getFirstName(), md_Person.getPropertyValue(person, "firstName"));
		assertEquals(person.getId(), md_Person.getPropertyValue(person, "id"));
		assertEquals(person.getId(), md_Person.getIdValue(person));
		
		RecipeIngredient ri = recipe.getIngredients().iterator().next();
		
		assertEquals(recipe, md_RecipeIngredientId.getPropertyValue(ri.getCompoundId(), "recipe"));
		assertEquals(recipe.getTitle(), md_RecipeIngredientId.getPropertyType("recipe").getPropertyValue(recipe, "title"));
		assertEquals(recipe.getId(), md_RecipeIngredientId.getPropertyType("recipe").getIdValue(recipe));
		assertEquals(ri.getCompoundId(), md_RecipeIngredient.getIdValue(ri));
		assertEquals(null, md_RecipeIngredientId.getIdValue(ri.getCompoundId()));
	}
	
	@Test
	public void testCollections() {
		Metadata md = metadataUtil.get(Person.class);
		Metadata md2 = md.getPropertyType("home");
		Metadata md3 = metadataUtil.get(LimbedPet.class);
		
		assertTrue(md2.getPropertyType("residents").isCollection());
		assertFalse(md2.getPropertyType("residents").isString());
		assertFalse(md2.getPropertyType("residents").isNumeric());
		assertTrue(md2.getPropertyType("residents").isEntity());
		assertFalse(md2.getPropertyType("residents").isEmeddable());
		
		assertTrue(md3.getPropertyType("limbs").isCollection());
		assertTrue(md3.getPropertyType("limbs").isString());
		assertFalse(md3.getPropertyType("limbs").isNumeric());
		assertFalse(md3.getPropertyType("limbs").isEntity());
		assertFalse(md3.getPropertyType("limbs").isEmeddable());
		
	}
	
	@Test
	public void testProxyIssues() {
		initDB();
		Person joe = getProxy(Person.class, joeA.getId());
		
		//joe's class is now actually a JavaAssist or CGLib generated extension of Person.
		//these methods should return deal with that problem...
		assertEquals(Person.class, metadataUtil.getUnproxiedClass(joe.getClass()));
		assertEquals(Person.class, metadataUtil.getUnproxiedClass(joe));
	}
}
