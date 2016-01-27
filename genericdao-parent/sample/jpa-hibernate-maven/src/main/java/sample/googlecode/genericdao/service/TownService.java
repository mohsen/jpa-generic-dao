package sample.googlecode.genericdao.service;

import java.util.List;

import sample.googlecode.genericdao.model.Town;


public interface TownService {

	public void persist(Town citizen);

	public List<Town> findAll();

	public Town findByName(String name);

}