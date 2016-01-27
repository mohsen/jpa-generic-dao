package sample.googlecode.genericdao.oldworld.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sample.googlecode.genericdao.oldworld.dao.TownDAO;
import sample.googlecode.genericdao.oldworld.model.Town;

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;

/**
 * This is the implementation for our Town Service. The @Service annotation
 * allows Spring to automatically detect this as a component rather than having
 * to comfigure it in XML. The @Autowired annotation tells Spring to inject our
 * Town DAO using the setDao() method.
 * 
 * @author dwolverton
 * 
 */
@Service
@Transactional
public class TownServiceImpl implements TownService {

	TownDAO dao;
	
	@Autowired
	public void setDao(TownDAO dao) {
		this.dao = dao;
	}
	
	public void save(Town town) {
		dao.save(town);
	}
	
	public List<Town> findAll() {
		return dao.findAll();
	}
	
	public Town findByName(String name) {
		return dao.searchUnique(new Search().addFilterEqual("name", name).addFetch("citizens"));
	}

	public List<Map<String, Object>> findAllWithForDropdown() {
		Search s = new Search();
		s.addField("id");
		s.addField("name");
		s.setResultMode(Search.RESULT_MAP);
		s.addSortAsc("name");
		return dao.search(s);
	}

	public void delete(Long id) {
		dao.removeById(id);
	}

	public List<Town> search(ISearch search) {
		return dao.search(search);
	}

	public Town findById(Long id) {
		return dao.find(id);
	}
}
