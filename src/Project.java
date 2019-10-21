import org.json.JSONObject;

public class Project {
	private final String remoteAddr = "https://github.com/";
	private String name;
	private String repoOwner;
	private String start;
	private String end;
	private FLAG_TYPE flagType;
	
	public enum FLAG_TYPE {
		TAG,
		COMMIT
	}
	
	public String getName() {
		return name;
	}

	public String getRepoOwner() {
		return repoOwner;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}
	
	public FLAG_TYPE getFlagType() {
		return flagType;
	}
	
	public String getRepoAddr() {
		return this.remoteAddr+ this.repoOwner + "/" + this.name + ".git";
	}
	
	public String toJSON() {
		JSONObject jObj = new JSONObject();
		jObj.put("remoteAddr", this.remoteAddr);
		jObj.put("name", this.name);
		jObj.put("repoOwner", this.repoOwner);
		switch(flagType) {
			case TAG:
				jObj.put("startTag", this.start);
				jObj.put("endTag", this.end);
				break;
			case COMMIT:
				jObj.put("startCommit", this.start);
				jObj.put("endCommit", this.end);
				break;
		}
		return jObj.toString();
	}
	
	public Project(String name, String repoOwner, String start, String end, FLAG_TYPE flagType) {
		this.name = name;
		this.repoOwner = repoOwner;
		this.start = start;
		this.end = end;
		this.flagType = flagType;
	}
}
