package sample.googlecode.genericdao.oldworld.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sample.googlecode.genericdao.oldworld.model.Citizen;
import sample.googlecode.genericdao.oldworld.service.CitizenService;
import sample.googlecode.genericdao.oldworld.webhelps.Util;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Controller
public class CitizenController {
	CitizenService citizenService;

	@Autowired
	public void setCitizenService(CitizenService citizenService) {
		this.citizenService = citizenService;
	}
	
	@RequestMapping
	public void list(HttpServletRequest request, Model model) {
		Search search = new Search();
		search.setMaxResults(10);
		//Fill in the sort, paging, filters from request parameters automatically.
		Util.getSearchFromParams(search, request.getParameterMap());
		
		SearchResult<Citizen> results = citizenService.searchAndCount(search);
		model.addAttribute(results.getResult());
		model.addAttribute("pageCount", (results.getTotalCount() + 9) / 10);
		model.addAttribute("page", search.getPage() < 0 ? 1 : search.getPage() + 1);
	}
	
	@RequestMapping
	public String delete(@RequestParam("id") Long id, HttpServletRequest request) {
		citizenService.delete(id);
		//Preserve the search parameters over redirect by adding them to the URL.
		return Util.addSearchParamsToURL("redirect:list.do", request.getParameterMap(), true, true, true);
	}
}
