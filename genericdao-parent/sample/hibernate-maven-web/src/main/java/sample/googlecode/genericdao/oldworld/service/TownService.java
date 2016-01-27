package sample.googlecode.genericdao.oldworld.service;

import java.util.List;
import java.util.Map;

import sample.googlecode.genericdao.oldworld.model.Town;

import com.googlecode.genericdao.search.ISearch;


/**
 * This is the interface for our Town Service. As a matter of best practice
 * we reference this interface in other components rather than the
 * implementation itself.
 * 
 * @author dwolverton
 * 
 */
public interface TownService {

	public void save(Town town);

	public List<Town> findAll();

	public Town findByName(String name);

	public List<Map<String,Object>> findAllWithForDropdown();
	
	public List<Town> search(ISearch search);
	
	public void delete(Long id);

	public Town findById(Long id);
}