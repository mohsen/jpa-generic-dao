package sample.googlecode.genericdao.oldworld.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import sample.googlecode.genericdao.oldworld.model.Citizen;
import sample.googlecode.genericdao.oldworld.model.Town;

@Service
@Transactional
public class BulkDataInitializationServiceImpl implements
		BulkDataInitializationService {
	
	@Autowired
	private TownService townService;
	
	@Autowired
	private CitizenService citizenService;

	@Override
	public void loadOldTimeDataSet() {
		Town melody = new Town("Melody", 57);
		Town rockyRidge = new Town("Rocky Ridge", 20);
		Town rainport = new Town ("Rainport", 203);
		
		Citizen[] citizens = new Citizen[] {
			new Citizen("Michael", "Minstrel", melody),
			new Citizen("Linda", "Lyricist", melody),
			new Citizen("Pete", "Piano Tuner", melody),
			
			new Citizen("Shep", "Sheep Herder", rockyRidge),
			new Citizen("Sandy", "Sheep Herder's wife", rockyRidge),
			
			new Citizen("Judy, Sir.", "Judge", rainport),
			new Citizen("Silvester", "Jurer #1", rainport),
			new Citizen("Dwight", "Sherif", rainport),
			new Citizen("Rambo", "Parole Officer", rainport),
			new Citizen("Moe", "Blacksmith", rainport),
			new Citizen("Rev. Nicholas", "Priest", rainport),
			new Citizen("Tom Butcher", "Butcher", rainport),
			new Citizen("Dick Baker", "Baker", rainport),
			new Citizen("Harry Chandler", "Chandlestick Maker", rainport)
		};
		
		townService.save(melody);
		townService.save(rockyRidge);
		townService.save(rainport);
		
		for (Citizen citizen : citizens) {
			citizenService.save(citizen);
		}
	}

}
