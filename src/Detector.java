import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.json.JSONObject;

public class Detector {
	private Project[] projects;
	private RefactoringType[] consideredRefactoringTypes;
	private Map<RefactoringType, Integer> refactoringCounts = new HashMap<RefactoringType, Integer>();
	
	public Detector() {
		this.projects = new Project[] {
			new Project("junit", "https://github.com/junit-team/junit4.git", "r4.11", "r4.12"),
			new Project("kafka", "https://github.com/apache/kafka.git", "2.1.1", "2.2.2-rc1"),
			new Project("hadoop", "https://github.com/apache/hadoop.git", "release-3.2.0-RC1", "release-3.2.1-RC0"),
			new Project("hive", "https://github.com/apache/hive.git", "release-2.3.5-rc0", "release-3.1.2-rc0"),
			new Project("accumulo", "https://github.com/apache/accumulo.git", "rel/2.0.0-alpha-1", "rel/2.0.0")
		};
		this.consideredRefactoringTypes = new RefactoringType[] {
			RefactoringType.EXTRACT_OPERATION,
			RefactoringType.MOVE_OPERATION,
			RefactoringType.EXTRACT_SUBCLASS,
			RefactoringType.EXTRACT_AND_MOVE_OPERATION,
			RefactoringType.EXTRACT_VARIABLE,
			RefactoringType.INLINE_VARIABLE
		};
	}
	
	public void detectAll() throws Exception {
		GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
		((GitHistoryRefactoringMinerImpl)miner).setRefactoringTypesToConsider(consideredRefactoringTypes);
		for (Project p : this.projects) {
			System.out.println("project " + p.getName() + "'s processing started");
			this.detect(miner, p);
			System.out.println("project " + p.getName() + "'s processing finished");
		}
	}
	
	private void detect(GitHistoryRefactoringMiner miner, Project p) throws Exception {
		GitService gitService = new GitServiceImpl();
		Repository repo = gitService.cloneIfNotExists("tmp/" + p.getName(), p.getRemoteAddr());
		miner.detectBetweenTags(repo, p.getStartTag(), p.getEndTag(), new RefactoringHandler() {
			  @Override
			  public void handle(String commitId, List<Refactoring> refactorings, 
					  Map<String, String> fileContentsBefore, Map<String, String> fileContentsCurrent) {
				if(refactorings.size() > 0) {
				    for (Refactoring ref : refactorings) {
						try {
							writeRefInfo(commitId, ref, p.getName());
							writeFileContents(commitId, "before", fileContentsBefore);
							writeFileContents(commitId, "current", fileContentsCurrent);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				}
			  }
			});
	}
	
	private void writeRefInfo(String commitId, Refactoring ref, String projectName) throws IOException {
    	int currCount = refactoringCounts.getOrDefault(ref.getRefactoringType(), 0);
    	refactoringCounts.put(ref.getRefactoringType(), currCount + 1);
    	
		// file name format: data/{refactoring name}/{project name}/{index number}.json
		StringBuilder refFileName = new StringBuilder();
		refFileName.append("data/ref_infos/").append(ref.getName().replaceAll(" ", "_")).append("/");
		refFileName.append(projectName).append("/").append(currCount).append(".json");
		
		write(refFileName.toString(), addCommitIdToJson(ref.toJSON(), commitId));
	}
	
	private void writeFileContents(String commitId, String flag, Map<String, String> fileContents) throws IOException {
		for (Map.Entry<String, String> pair : fileContents.entrySet()) {
			String key = pair.getKey();
			StringBuilder filename = new StringBuilder();
			filename.append("data/src_code/").append(commitId).append("/").append(key.substring(0, key.lastIndexOf('/') + 1));
			filename.append(flag).append(key.substring(key.lastIndexOf('/')));
			write(filename.toString(), pair.getValue());
		}
	}
	
	private String addCommitIdToJson(String refJSON, String commentId) {
		JSONObject jObj = new JSONObject(refJSON);
		jObj.put("commentId", commentId);
		return jObj.toString();
	}
	
	private void write(String path, String content) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		FileWriter writer  = new FileWriter(f);
		writer.write(content);
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws Exception {
		Detector detector = new Detector();
		detector.detectAll();
	}
}
