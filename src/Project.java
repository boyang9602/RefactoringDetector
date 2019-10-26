import org.eclipse.jgit.lib.Repository;
import org.json.JSONObject;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class Project {
	private final String remoteAddr = "https://github.com/";
	private String name;
	private String repoOwner;
	private String start;
	private String end;
	private FLAG_TYPE flagType;
	private SCOPE scope;
	private String branch;
	
	public enum SCOPE {
		INTERVAL,
		ALL
	}
	
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
	
	public SCOPE getScope() {
		return scope;
	}

	public String getBranch() {
		return branch;
	}
	
	public String getRepoAddr() {
		return this.remoteAddr+ this.repoOwner + "/" + this.name + ".git";
	}
	
	public String toJSON() {
		JSONObject jObj = new JSONObject();
		jObj.put("remoteAddr", this.remoteAddr);
		jObj.put("name", this.name);
		jObj.put("repoOwner", this.repoOwner);
		if(this.scope == SCOPE.INTERVAL) {
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
			jObj.put("scope", "INTERVAL");
		} else if(this.scope == SCOPE.ALL) {
			jObj.put("scope", "ALL");
		}
		return jObj.toString();
	}
	
	public void detect(RefactoringType[] consideredRefactoringTypes) throws Exception {
		GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
		((GitHistoryRefactoringMinerImpl)miner).setRefactoringTypesToConsider(consideredRefactoringTypes);
		GitService gitService = new GitServiceImpl();
		Repository repo = gitService.cloneIfNotExists("tmp/" + getName(), getRepoAddr());
		
		try {
			switch(getScope()) {
			case ALL:
				miner.detectAll(repo, getBranch(), new WritingFileRefactoringHandler(this));
				break;
			case INTERVAL:
				switch(getFlagType()) {
					case TAG:
					miner.detectBetweenTags(repo, getStart(), getEnd(), new WritingFileRefactoringHandler(this));
						break;
					case COMMIT:
						miner.detectBetweenCommits(repo, getStart(), getEnd(), new WritingFileRefactoringHandler(this));
						break;
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred in detection process, please check project: " + this.getName());
		}
	}
	
	public Project(String name, String repoOwner, String start, String end, FLAG_TYPE flagType) {
		this.name = name;
		this.repoOwner = repoOwner;
		this.start = start;
		this.end = end;
		this.flagType = flagType;
		this.scope = SCOPE.ALL;
	}
	
	public Project(String name, String repoOwner, String branch) {
		this.name = name;
		this.repoOwner = repoOwner;
		this.branch = branch;
		this.scope = SCOPE.ALL;
	}
}
