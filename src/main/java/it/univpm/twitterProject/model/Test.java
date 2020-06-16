package it.univpm.twitterProject.model;

import org.json.simple.JSONObject;
import it.univpm.twitterProject.database.StartClass;

public class Test {
	private String arg = "";
	private String operetor = "==";
	private String descriptor = "";
	private Object obj = null;

	public Test(JSONObject jO) {
		for (Metadata m : StartClass.getAllMetadata()) {
			if (jO.get(m.getAlias()) != null) {
				arg = m.getAlias();
			}
		}

		if (arg == "") {
			// aggiungere eccezione in caso arg non corrisponde a nessun dato
		}

		if (jO.get(arg) instanceof JSONObject) {
			JSONObject japp = (JSONObject) jO.get(arg);
			for (String s : StartClass.getAllOperetor()) {
				if (japp.get(s) != null) {
					operetor = s;
				}
			}

			if (operetor == "==") {
				for (String s : StartClass.getAllDescriptor()) {
					if (japp.get(s) != null) {
						descriptor = s;
					}
				}
				if (descriptor != "") {
					obj = japp.get(descriptor);
				}
			} else {
				obj = japp.get(operetor);
			}

		} else {
			obj = jO.get(arg);
		}
	}

	public String getArg() {
		return arg;
	}

	public String getOperetor() {
		return operetor;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public Object getObj() {
		return obj;
	}
}