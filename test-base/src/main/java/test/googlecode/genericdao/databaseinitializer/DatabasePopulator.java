package test.googlecode.genericdao.databaseinitializer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test.googlecode.genericdao.model.Address;
import test.googlecode.genericdao.model.Home;
import test.googlecode.genericdao.model.Ingredient;
import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Pet;
import test.googlecode.genericdao.model.Project;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.RecipeIngredient;
import test.googlecode.genericdao.model.Store;

public class DatabasePopulator {

	private List<Object> entities;
	
	public DatabasePopulator(List<Object> entities) {
		this.entities = new ArrayList<Object>(entities);
	}
	
	private DatabaseRowInserter inserter;
	
	public void persistEntitiesAndSetIds(Connection conn) throws SQLException {
		inserter = new DatabaseRowInserter(conn);
		
		for (Object entity : entities) {
			insertEntity(entity);
		}
		
		for (Object entity : entities) {
			if (entity instanceof Pet && ((Pet) entity).getFavoritePlaymate() != null)
				inserter.updateFavoritePlaymate((Pet) entity);
		}
	}
	
	private void insertEntity(Object entity) throws SQLException {
		if (entity instanceof Person)
			insert((Person) entity);
		else if (entity instanceof Pet)
			insert((Pet) entity);
		else if (entity instanceof Store)
			insert((Store) entity);
		else if (entity instanceof Recipe)
			insert((Recipe) entity);
		else if (entity instanceof Project)
			insert((Project) entity);
		else
			throw new RuntimeException("DatabaseRowInserter is not set up to handle entities of type " + entity.getClass().getName());
	}
	
	protected void insert(Person p) throws SQLException {
		if (p.getId() != null) return;
		if (p.getHome() != null) {
			insert(p.getHome());
		}
		
		inserter.insert(p);
	}
	
	protected void insert(Home h) throws SQLException {
		if (h.getId() != null) return;
		if (h.getAddress() != null) {
			insert(h.getAddress());
		}
		
		inserter.insert(h);
	}
	
	protected void insert(Address a) throws SQLException {
		if (a.getId() != null) return;
		inserter.insert(a);
	}
	
	protected void insert(Pet p) throws SQLException {
		if (p.getId() != null) return;
		inserter.insert(p);
		
		if (p instanceof LimbedPet) {
			int i = 0;
			for (String s : ((LimbedPet) p).getLimbs()) {
				inserter.insertLimbedPetLimb((LimbedPet) p, s, i++);
			}
		}
	}
	
	protected void insert(Store s) throws SQLException {
		if (s.getId() != 0) return;
		inserter.insert(s);
		
		for (Ingredient i : s.getIngredientsCarried()) {
			insert(i);
			inserter.insert(s, i);
		}
	}
	
	protected void insert(Recipe r) throws SQLException {
		if (r.getId() != 0) return;
		inserter.insert(r);
		
		for (RecipeIngredient ri : r.getIngredients()) {
			insert(ri);
		}
	}
	
	protected void insert(RecipeIngredient ri) throws SQLException {
		if (ri.getCompoundId().getIngredient().getIngredientId() == 0) {
			insert(ri.getCompoundId().getIngredient());
		}
		
		inserter.insert(ri);
	}
	
	protected void insert(Ingredient i) throws SQLException {
		if (i.getIngredientId() != 0) return;
		inserter.insert(i);
	}
	
	protected void insert(Project p) throws SQLException {
		if (p.getId() != null) return;
		inserter.insert(p);
		
		for (Person m : p.getMembers()) {
			inserter.insertProjectMember(p, m);
		}
	}
}
