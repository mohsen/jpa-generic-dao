package sample.googlecode.genericdao.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Town {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String name;
	int population;
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

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
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
