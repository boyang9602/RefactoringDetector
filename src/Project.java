
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
	
	public String getRepoAddr() {
		return this.remoteAddr+ this.repoOwner + "/" + this.name + ".git";
	}
	
	public Project(String name, String repoOwner, String start, String end, FLAG_TYPE flagType) {
		this.name = name;
		this.repoOwner = repoOwner;
		this.start = start;
		this.end = end;
		this.flagType = flagType;
	}
}
