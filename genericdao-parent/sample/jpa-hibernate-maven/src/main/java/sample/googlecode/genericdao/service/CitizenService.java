package sample.googlecode.genericdao.service;

import java.util.List;

import sample.googlecode.genericdao.model.Citizen;


public interface CitizenService {

	public void persist(Citizen citizen);

	public List<Citizen> findAll();

	public Citizen findByName(String name);

	public void flush();
}