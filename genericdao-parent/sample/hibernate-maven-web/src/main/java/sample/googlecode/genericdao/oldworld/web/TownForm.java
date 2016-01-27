package sample.googlecode.genericdao.oldworld.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sample.googlecode.genericdao.oldworld.model.Town;
import sample.googlecode.genericdao.oldworld.service.TownService;
import sample.googlecode.genericdao.oldworld.webhelps.Util;

@Controller
@RequestMapping("/town/edit")
public class TownForm {
	TownService townService;
	
	@Autowired
	public void setTownService(TownService townService) {
		this.townService = townService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public Town setupForm(@RequestParam(value="id", required=false) Long id) {
		if (id != null)
			return townService.findById(id);
		else
			return new Town();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute Town town, BindingResult result, HttpServletRequest request) {
		if (result.hasErrors())
			return null; //"town/edit";
		townService.save(town);
		//Preserve the search parameters so that we return to the list page just as we left it.
		return Util.addSearchParamsToURL("redirect:/town/list.do", request.getParameterMap(), true, true, true);
	}
}
