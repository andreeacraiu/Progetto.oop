package it.univpm.twitterProject.controller;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import it.univpm.twitterProject.model.Coord;
import it.univpm.twitterProject.model.Metadata;
import it.univpm.twitterProject.model.Tweet;
import it.univpm.twitterProject.service.AppFilter;
import it.univpm.twitterProject.utils.stats.Stat;
import it.univpm.twitterProject.utils.stats.StatCity;
import it.univpm.twitterProject.utils.stats.StatNumb;
import it.univpm.twitterProject.utils.stats.TweetForCity;
import it.univpm.twitterProject.database.StartClass;
import it.univpm.twitterProject.exception.DataIllegalArgumentException;
import it.univpm.twitterProject.exception.FilterNotFoundException;
import it.univpm.twitterProject.exception.TweetsNotFoundException;
import it.univpm.twitterProject.utils.filter.GenericFilterTweet;

@RestController
public class simpleRestController {

	@GetMapping("/getAllTweet")
	public JSONObject getData() {
		return StartClass.getAllTweetJO();
	}

	@GetMapping("/getAllCity")
	public HashMap<String, Coord> getCity() {
		return StartClass.getAllCityJO();
	}

	@GetMapping("/getAllMetadata")
	public JSONObject getMetadata() {
		return StartClass.getAllMetadataJO();
	}

	@GetMapping("/setTweet")
	public ResponseEntity<String> setTweet(@RequestParam(name = "arg", defaultValue = "terremoto") String arg,
			@RequestParam(name = "qt", defaultValue = "100") int qt) throws ParseException {
		if (qt <= 0) {
			return new ResponseEntity<String>("Qt deve essere maggiore di 0", HttpStatus.OK);
		}
		try {
			StartClass.setAllTweet(arg, qt);
		} catch (TweetsNotFoundException e) {
			return new ResponseEntity<String>("Non ci sono tweet per questi valori", HttpStatus.OK);
		}
		if (arg.equals("terremoto") && qt == 100) {
			return new ResponseEntity<String>("Nessun parametro trovato, array impostato a default", HttpStatus.OK);
		}
		return new ResponseEntity<String>("Array modificato", HttpStatus.OK);
	}

	@GetMapping("/data")
	public JSONObject data(@RequestParam(name = "filter", defaultValue = "no filter") String f)
			throws FilterNotFoundException, TweetsNotFoundException {

		if (f.equals("no filter")) {
			return StartClass.getAllTweetJO();
		}
		AppFilter af = new AppFilter();
		GenericFilterTweet gft = new GenericFilterTweet(f);
		af.Filtring(gft);
		return af.getAllFilteredTweetJO();

	}

	@GetMapping("/Stat")
	public JSONArray stat(@RequestParam(name = "field") JSONArray field,
			@RequestParam(name = "filter", defaultValue = "no filter") String filter,
			@RequestParam(name = "dist", defaultValue = "-1") double dist)
			throws FilterNotFoundException, DataIllegalArgumentException, TweetsNotFoundException {
		ArrayList<Tweet> filteredTweet = new ArrayList<Tweet>();
		ArrayList<String> multiField = new ArrayList<String>();
		Stat stat;
		JSONArray out = new JSONArray();
		if (filter.equals("no filter")) {
			filteredTweet = StartClass.getAllTweet();
		} else {
			AppFilter af = new AppFilter();
			GenericFilterTweet gft = new GenericFilterTweet(filter);
			af.Filtring(gft);
			filteredTweet = af.getFilteredTweet();
		}
		for (Object obj : field) {
			multiField.add((String) obj);
		}
		for (String s : multiField) {
			boolean b = false;
			for (Metadata meta : StartClass.getAllMetadata()) {
				if (meta.getAlias().equals(s)) {
					b = true;
				}
			}
			if (!(b || StartClass.getAllCity().containsKey(s))) {
				s = s.substring(0, 1).toUpperCase() + s.substring(1);
				if (!StartClass.getAllCity().containsKey(s))
					throw new DataIllegalArgumentException("Città o metadato non presente");
			}
			if (StartClass.getAllCity().containsKey(s)) {
				if (dist <= 0) {
					throw new DataIllegalArgumentException("La distanza deve essere maggiore di 0");
				}

				stat = new StatCity(filteredTweet, dist, s);
			} else {
				stat = new StatNumb<Tweet>(filteredTweet, s);
			}
			out.add(stat.getStatJo());
		}
		return out;
	}

	@GetMapping("/getStats")
	public JSONArray getStats(@RequestParam(name = "distanza") int distanza) {
		TweetForCity tfc = new TweetForCity(distanza);
		return tfc.AppStat();
	}
}