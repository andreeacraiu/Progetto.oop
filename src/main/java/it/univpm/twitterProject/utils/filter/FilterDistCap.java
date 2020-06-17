package it.univpm.twitterProject.utils.filter;

import it.univpm.twitterProject.database.StartClass;
import it.univpm.twitterProject.model.Coord;
import it.univpm.twitterProject.service.Distanza;

public class FilterDistCap {
	
	private String city;
	private double range;
	
	

	public FilterDistCap(String city, double range) {
		super();
		this.city = city;
		this.range = range;
	}



	public boolean app(Coord tco) {
		Distanza d = new Distanza();
		 double dist = d.CalcDist(StartClass.getAllCity().get(city), tco);
		if (dist<=range) {return true;}
		return false;
	}
}

