package sample.googlecode.genericdao.oldworld.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * This is one of our DTOs. It is recognized by Hibernate as an entity by the @Entity
 * annotation. Other annotations provide further information to Hibernate about
 * how to map the Entity to a database tables.
 * 
 * @author dwolverton
 * 
 */
@Entity
public class Town {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String name;
	Integer population;
	@OneToMany(mappedBy = "town")
	Set<Citizen> citizens;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPopulation() {
		return population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}

	public Set<Citizen> getCitizens() {
		return citizens;
	}

	public void setCitizens(Set<Citizen> citizens) {
		this.citizens = citizens;
	}

	public Town() {}
	
	public Town(String name, Integer population) {
		this.name = name;
		this.population = population;
	}
	
}
