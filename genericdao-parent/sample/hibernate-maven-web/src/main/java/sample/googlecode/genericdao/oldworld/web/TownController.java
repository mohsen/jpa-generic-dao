package sample.googlecode.genericdao.oldworld.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sample.googlecode.genericdao.oldworld.model.Town;
import sample.googlecode.genericdao.oldworld.service.BulkDataInitializationService;
import sample.googlecode.genericdao.oldworld.service.TownService;
import sample.googlecode.genericdao.oldworld.webhelps.Util;

import com.googlecode.genericdao.search.ISearch;

@Controller
public class TownController {
	TownService townService;

	@Autowired
	public void setTownService(TownService townService) {
		this.townService = townService;
	}
	
	BulkDataInitializationService bulkDataInitializationService;

	@Autowired
	public void setBulkDataInitializationService(
			BulkDataInitializationService bulkDataInitializationService) {
		this.bulkDataInitializationService = bulkDataInitializationService;
	}
	
	@RequestMapping
	public List<Town> list(HttpServletRequest request) {
		//Fill in the sort, paging, filters from request parameters automatically.
		ISearch search = Util.getSearchFromParams(request.getParameterMap());
		return townService.search(search);
	}
	
	@RequestMapping
	public String delete(@RequestParam("id") Long id, HttpServletRequest request) {
		townService.delete(id);
		//Preserve the search parameters over redirect by adding them to the URL.
		return Util.addSearchParamsToURL("redirect:list.do", request.getParameterMap(), true, true, true);
	}
	
	@RequestMapping
	public String loadSampleData(HttpServletRequest request) {
		bulkDataInitializationService.loadOldTimeDataSet();
		return "redirect:/town/list.do";
	}
}
