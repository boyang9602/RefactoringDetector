
public class Project {
	private String name;
	private String remoteAddr;
	private String startTag;
	private String endTag;
	
	public String getName() {
		return name;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public String getStartTag() {
		return startTag;
	}

	public String getEndTag() {
		return endTag;
	}
	
	public Project(String name, String remoteAddr, String startTag, String endTag) {
		this.name = name;
		this.remoteAddr = remoteAddr;
		this.startTag = startTag;
		this.endTag = endTag;
	}
}
