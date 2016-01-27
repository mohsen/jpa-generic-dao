package junit.googlecode.genericdao.search;

import static org.junit.Assert.*;

import org.junit.Test;

import test.googlecode.genericdao.BaseTest;
import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;

import com.googlecode.genericdao.search.Metadata;
import com.googlecode.genericdao.search.MetadataUtil;
import com.googlecode.genericdao.search.jpa.JPAAnnotationMetadataUtil;

public class JPAAnnotationMetadataTest extends BaseTest {
	protected MetadataUtil metadataUtil = new JPAAnnotationMetadataUtil();

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
	public void testValues() {
		Metadata md = metadataUtil.get(Person.class);
		Metadata md2 = md.getPropertyType("home");
		Metadata md3 = metadataUtil.get(RecipeIngredient.class);
		Metadata md4 = md3.getIdType();
		
		assertEquals(joeA.getFirstName(), md.getPropertyValue(joeA, "firstName"));
		assertEquals(joeA.getId(), md.getPropertyValue(joeA, "id"));
		assertEquals(joeA.getId(), md.getIdValue(joeA));
		
		Recipe recipe = recipes.get(0);
		RecipeIngredient ri = recipe.getIngredients().iterator().next();
		
		assertEquals(recipe, md4.getPropertyValue(ri.getCompoundId(), "recipe"));
		assertEquals(recipe.getTitle(), md4.getPropertyType("recipe").getPropertyValue(recipe, "title"));
		assertEquals(recipe.getId(), md4.getPropertyType("recipe").getIdValue(recipe));
		assertEquals(ri.getCompoundId(), md3.getIdValue(ri));
		assertEquals(null, md4.getIdValue(ri.getCompoundId()));
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
}
