package test.googlecode.genericdao.databaseinitializer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

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

public class DatabaseRowInserter {
	
	private SqlExecutor sqlExecutor;
	
	private String tableName;
	private Map<String, Object> fields = new TreeMap<String, Object>();
	private Map<String, Object> updateKeyValues = new TreeMap<String, Object>();
	private boolean isInsertVsUpdate;
	
	public DatabaseRowInserter(Connection connection) {
		sqlExecutor = new SqlExecutor(connection);
	}
	
	public void insert(Person p) throws SQLException {
		startInsertForTable("person");
		setField("age", p.getAge());
		setField("dob", p.getDob());
		setField("first_name", p.getFirstName());
		setField("last_name", p.getLastName());
		setField("weight", p.getWeight());
		setField("isMale", p.getIsMale());
		setField("father_id", p.getFather() != null ? p.getFather().getId() : null);
		setField("mother_id", p.getMother() != null ? p.getMother().getId() : null);
		setField("home_id", p.getHome().getId());
		execute(true);
		p.setId(getGeneratedId());
	}

	public void insert(Home h) throws SQLException {
		startInsertForTable("home");
		setField("type", h.getType());
		setField("address_id", h.getAddress().getId());
		execute(true);
		h.setId(getGeneratedId());
	}

	public void insert(Address a) throws SQLException {
		startInsertForTable("address");
		setField("city", a.getCity());
		setField("state", a.getState());
		setField("street", a.getStreet());
		setField("zip", a.getZip());
		execute(true);
		a.setId(getGeneratedId());
	}

	public void insert(Pet p) throws SQLException {
		Boolean hasPaws = null;
		if (p instanceof LimbedPet) {
			hasPaws = ((LimbedPet) p).isHasPaws();
		}
		
		startInsertForTable("pet");
		setField("idNumber", p.getIdent().getIdNumber());
		setField("first", p.getIdent().getName().getFirst());
		setField("last", p.getIdent().getName().getLast());
		setField("species", p.getSpecies());
		setField("limbed", p instanceof LimbedPet);
		setField("hasPaws", hasPaws);
		execute(true);
		p.setId(getGeneratedId());
	}
	
	public void insertLimbedPetLimb(LimbedPet pet, String limb, int orderIndex) throws SQLException {
		startInsertForTable("LimbedPet_limbs");
		setField("LimbedPet_id", pet.getId());
		setField("limbs", limb);
		setField("idx", orderIndex);
		execute(false);
	}

	public void insert(Recipe r) throws SQLException {
		startInsertForTable("recipe");
		setField("title", r.getTitle());
		execute(true);
		r.setId(getGeneratedId());
	}

	public void insert(Ingredient i) throws SQLException {
		startInsertForTable("ingredient");
		setField("name", i.getName());
		execute(true);
		i.setIngredientId(getGeneratedId());
	}

	public void insert(Store s) throws SQLException {		
		startInsertForTable("store");
		setField("name", s.getName());
		execute(true);
		s.setId(getGeneratedId());
	}
	
	public void insert(Store s, Ingredient i) throws SQLException {		
		startInsertForTable("store_ingredient");
		setField("Store_id", s.getId());
		setField("ingredientsCarried_ingredientId", i.getIngredientId());
		execute(false);
	}

	public void insert(RecipeIngredient ri) throws SQLException {
		startInsertForTable("recipe_x_ingredient");
		setField("amount", ri.getAmount());
		setField("measure", ri.getMeasure());
		setField("ingredient_ingredientId", ri.getCompoundId().getIngredient().getIngredientId());
		setField("recipe_id", ri.getCompoundId().getRecipe().getId());
		execute(false);
	}

	public void insert(Project p) throws SQLException {
		startInsertForTable("project");
		setField("id", p.getId());
		setField("inceptionYear", p.getInceptionYear());
		setField("name", p.getName());
		execute(true);
		p.setId(getGeneratedId());
	}
	
	public void insertProjectMember(Project project, Person member) throws SQLException {
		startInsertForTable("project_person");
		setField("Project_id", project.getId());
		setField("members_id", member.getId());
		execute(false);		
	}
	
	public void updateFavoritePlaymate(Pet pet) throws SQLException {
		startUpdateForTable("pet");
		setField("favoritePlaymate_id", pet.getFavoritePlaymate().getId());
		whereKeyEquals("id", pet.getId());
		execute(false);
	}

	private void startInsertForTable(String tableName) {
		clearState();		
		this.tableName = tableName;
		isInsertVsUpdate = true;
	}
	
	private void startUpdateForTable(String tableName) {
		clearState();
		this.tableName = tableName;
		isInsertVsUpdate = false;
	}
	
	private void clearState() {
		fields.clear();
		updateKeyValues.clear();
	}
	
	private void setField(String fieldName, Object fieldValue) {
		fields.put(fieldName, fieldValue);
	}
	
	private void whereKeyEquals(String keyColumnName, Object value) {
		updateKeyValues.put(keyColumnName, value);
	}
	
	private void execute(boolean fetchGeneratedId) throws SQLException {
		SqlStatementBuilder statementBuilder;
		if (isInsertVsUpdate)
			statementBuilder = SqlStatementBuilder.insert(tableName, fields);
		else 
			statementBuilder = SqlStatementBuilder.update(tableName, fields, updateKeyValues);
		
		if (fetchGeneratedId)
			sqlExecutor.executeWithGeneratedId(statementBuilder);
		else
			sqlExecutor.executeWithoutGeneratedId(statementBuilder);
	}
	
	private long getGeneratedId() {
		return sqlExecutor.getGeneratedKey();
	}

}
