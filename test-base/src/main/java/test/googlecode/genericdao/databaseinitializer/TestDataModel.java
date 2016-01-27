package test.googlecode.genericdao.databaseinitializer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import test.googlecode.genericdao.model.Ingredient;
import test.googlecode.genericdao.model.LimbedPet;
import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Pet;
import test.googlecode.genericdao.model.Project;
import test.googlecode.genericdao.model.Recipe;
import test.googlecode.genericdao.model.Store;

public class TestDataModel {

	protected Person joeA, // 10
	sallyA, // 9
	papaA, // 39
	mamaA, // 40
	joeB, // 10
	margaretB, // 13
	papaB, // 39
	mamaB, // 38
	grandpaA, // 65
	grandmaA; // 65

	protected Pet fishWiggles;
	protected LimbedPet catPrissy, catNorman, spiderJimmy;
	
	protected List<Store> stores;
	protected List<Recipe> recipes;
	
	protected List<Project> projects;
	
	@Autowired
	public void setJoeA(Person joeA) {
		this.joeA = joeA;
	}
	
	@Autowired
	public void setSallyA(Person sallyA) {
		this.sallyA = sallyA;
	}
	
	@Autowired
	public void setPapaA(Person papaA) {
		this.papaA = papaA;
	}
	
	@Autowired
	public void setMamaA(Person mamaA) {
		this.mamaA = mamaA;
	}
	
	@Autowired
	public void setJoeB(Person joeB) {
		this.joeB = joeB;
	}
	
	@Autowired
	public void setMargaretB(Person margaretB) {
		this.margaretB = margaretB;
	}
	
	@Autowired
	public void setPapaB(Person papaB) {
		this.papaB = papaB;
	}
	
	@Autowired
	public void setMamaB(Person mamaB) {
		this.mamaB = mamaB;
	}
	
	@Autowired
	public void setGrandpaA(Person grandpaA) {
		this.grandpaA = grandpaA;
	}
	
	@Autowired
	public void setGrandmaA(Person grandmaA) {
		this.grandmaA = grandmaA;
	}
	
	@Autowired
	public void setFishWiggles(Pet fishWiggles) {
		this.fishWiggles = fishWiggles;
	}
	
	@Autowired
	public void setCatPrissy(LimbedPet catPrissy) {
		this.catPrissy = catPrissy;
	}
	
	@Autowired
	public void setCatNorman(LimbedPet catNorman) {
		this.catNorman = catNorman;
	}
	
	@Autowired
	public void setSpiderJimmy(LimbedPet spiderJimmy) {
		this.spiderJimmy = spiderJimmy;
	}
	
	@Resource // wire by name -- @Autowired @Qualifier("stores") should work, but there seems to be a bug in Spring for lists
	public void setStores(List<Store> stores) {
		this.stores = stores;
	}
	
	@Resource // wire by name -- @Autowired @Qualifier("stores") should work, but there seems to be a bug in Spring for lists
	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}
	
	@Resource // wire by name -- @Autowired @Qualifier("stores") should work, but there seems to be a bug in Spring for lists
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
	public List<Object> getAllModelObjects() {
		
		List<Object> objects = new ArrayList<Object>();
		
		objects.add(grandpaA);
		objects.add(grandmaA);
		objects.add(papaA);
		objects.add(mamaA);
		objects.add(papaB);
		objects.add(mamaB);
		objects.add(joeA);
		objects.add(sallyA);
		objects.add(joeB);
		objects.add(margaretB);
		objects.add(spiderJimmy);
		objects.add(fishWiggles);
		objects.add(catPrissy);
		objects.add(catNorman);
		objects.addAll(stores);
		objects.addAll(recipes);
		objects.addAll(projects);
		
		return objects;
	}
	
	public void resetModelObjects() {
		for (Object object : getAllModelObjects()) {
			resetModelObject(object);
		}
	}
	
	private void resetModelObject(Object object) {
		if (object instanceof Person) {
			Person person = (Person) object;
			person.setId(null);
			person.getHome().setId(null);
			person.getHome().getAddress().setId(null);
			setup(person);
		} else if (object instanceof Pet) {
			((Pet) object).setId(null);
		} else if (object instanceof Store) {
			Store store = (Store) object;
			store.setId(0);
			for (Ingredient ingredient : store.getIngredientsCarried()) {
				ingredient.setIngredientId(0);
			}
		} else if (object instanceof Recipe) {
			((Recipe) object).setId(0);
		} else if (object instanceof Project) {
			((Project) object).setId(null);
		}
	}

	public static Person setup(Person p) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -p.getAge());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		p.setDob(cal.getTime());
		p.setWeight(100.0 + p.getAge() / 100.0);

		return p;
	}
	
	public static Person copy(Person p) {
		Person cpy = new Person();
		cpy.setId(p.getId());
		cpy.setFather(p.getFather());
		cpy.setFirstName(p.getFirstName());
		cpy.setLastName(p.getLastName());
		cpy.setMother(p.getMother());
		cpy.setAge(p.getAge());
		cpy.setDob(p.getDob());
		cpy.setWeight(p.getWeight());
		cpy.setIsMale(p.getIsMale());
		return cpy;
	}
}
