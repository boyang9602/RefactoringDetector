import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.refactoringminer.api.*;

public class Detector extends Thread {
	private Project project;
	private RefactoringType[] consideredRefactoringTypes;
	
	public Detector(Project project, RefactoringType[] consideredRefactoringTypes) {
		super(project.getName());
		this.project = project;
		this.consideredRefactoringTypes = consideredRefactoringTypes;
	}
	
	public void run() {
		System.out.println("project " + this.project.getName() + "'s processing started");
		try {
			this.project.detect(consideredRefactoringTypes);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred when open or clone repo. Thread stopped for project: " + this.project.getName());
		}
	}

	public static void main(String[] args) throws Exception {
		Project[] projects = new Project[] {
			new Project("junit", "junit-team", "master"),
			new Project("kafka", "apache", "trunk"),
			new Project("hadoop", "apache", "trunk"),
			new Project("hive", "apache", "master"),
			new Project("accumulo", "apache", "master"),
			new Project("elasticsearch", "elastic", "master"),
			new Project("fastjson", "alibaba", "master"),
			new Project("logstash", "elastic", "master"),
			new Project("jenkins", "jenkinsci", "master"),
			new Project("spring-framework", "spring-projects", "master")
		};
		RefactoringType[] consideredRefactoringTypes = new RefactoringType[] {
			RefactoringType.EXTRACT_OPERATION,
			RefactoringType.MOVE_OPERATION,
			RefactoringType.EXTRACT_AND_MOVE_OPERATION,
			RefactoringType.EXTRACT_VARIABLE,
			RefactoringType.INLINE_VARIABLE
		};
		
		List<JSONObject> projectsInfo = new ArrayList<JSONObject>();
		for(Project p : projects) {
			new Detector(p, consideredRefactoringTypes).start();
			projectsInfo.add(p.toJSON());
		}
		JSONObject jObj = new JSONObject();
		jObj.put("projects", projectsInfo);
		Util.write("data/projects_info.json", jObj.toString());
	}
}
