package it.univpm.twitterProject.controller;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import it.univpm.twitterProject.model.Coord;
import it.univpm.twitterProject.model.Metadata;
import it.univpm.twitterProject.model.Tweet;
import it.univpm.twitterProject.service.AppFilter;
import it.univpm.twitterProject.utils.stats.TweetForCity;
import it.univpm.twitterProject.database.StartClass;
import it.univpm.twitterProject.utils.filter.Filter;
import it.univpm.twitterProject.utils.filter.FilterDistCap;
import it.univpm.twitterProject.utils.filter.GenericFilterTweet;

@RestController
public class simpleRestController {

	@GetMapping("/getAllTweet")
	public ArrayList<Tweet> getData() {
		return StartClass.getAllTweet();
	}

	@GetMapping("/getAllCity")
	public HashMap<String, Coord> getCity() {
		return StartClass.getAllCity();
	}

	@GetMapping("/setTweet")
	public void setTweet(@RequestParam(name = "arg", defaultValue = "terremoto") String arg,
			@RequestParam(name = "qt", defaultValue = "100") int qt) throws ParseException {
		StartClass.setAllTweet(arg, qt);
	}

	

	@GetMapping("/data")
	public ArrayList<Tweet> data (@RequestParam(name = "filter", defaultValue = "no filter") String f) throws ParseException {
		if (f.equals("no filter")) {return StartClass.getAllTweet();}
		AppFilter af = new AppFilter();
		GenericFilterTweet gft = new GenericFilterTweet(f);
		af.Filtring(gft);
		return af.getFilteredTweet();
	}

	
	@PostMapping("/getStats")
	public JSONArray getStats(@RequestBody JSONObject distanza) {
		TweetForCity tfc = new TweetForCity((int) distanza.get("dist"));
		return tfc.AppStat();
	}
}