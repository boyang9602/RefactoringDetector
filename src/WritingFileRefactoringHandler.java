import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;

public class WritingFileRefactoringHandler extends RefactoringHandler {
	private Project project;
	private int counts = 0;
	private Map<String, Integer> refTypeCount = new HashMap<String, Integer>();
	
	public WritingFileRefactoringHandler(Project project) {
		this.project = project;
	}
	
	@Override
	public void handle(String commitId, List<Refactoring> refactorings, 
		  Map<String, String> fileContentsBefore, Map<String, String> fileContentsCurrent) {
		if(refactorings.size() > 0) {
			for (Refactoring ref : refactorings) {
				try {
					writeRefInfo(commitId, ref, this.project);
					writeFileContents(commitId, "before", fileContentsBefore);
					writeFileContents(commitId, "current", fileContentsCurrent);
					if (++counts % 100 == 0) {
						System.out.println("Project: " + this.project.getName() + ", " + counts + " refactorings detected");
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Exception in writing file, ignore current commit: " + commitId);
				} catch (JSONException e) {
					System.out.println("Exception in JSON process, ignore current commit: " + commitId);
				}
			}
		}
	}
	
	@Override
    public void handleException(String commitId, Exception e) {
		System.out.println("Commit: " + commitId + " handle error, project: " + this.project.getName() + ", ignored");
		e.printStackTrace();
    }
	
	@Override
	public void onFinish(int refactoringsCount, int commitsCount, int errorCommitsCount) {
		System.out.println(this.project.getName() + " finished: " + refactoringsCount + " refactorings, " + commitsCount + " commits, " + errorCommitsCount + " error commits");
	}
	
	private void writeRefInfo(String commitId, Refactoring ref, Project project) throws IOException, JSONException {
		int count = this.refTypeCount.getOrDefault(ref.getName(), 0);
		this.refTypeCount.put(ref.getName(), count + 1);
		
		// file name format: data/{refactoring name}/{project name}/{ID}.json
		StringBuilder refFileName = new StringBuilder();
		refFileName.append("data/ref_infos/").append(ref.getName().replaceAll(" ", "_")).append("/");
		refFileName.append(project.getName()).append("/").append(count).append(".json");
		
		Util.write(refFileName.toString(), insertCommitIdToJson(ref.toJSON(), commitId));
	}
	
	private void writeFileContents(String commitId, String flag, Map<String, String> fileContents) throws IOException {
		for (Map.Entry<String, String> pair : fileContents.entrySet()) {
			String key = pair.getKey();
			StringBuilder filename = new StringBuilder();
			filename.append("data/src_code/").append(flag).append("/").append(commitId).append("/");
			filename.append(key.substring(0, key.lastIndexOf('/') + 1)).append(key.substring(key.lastIndexOf('/') + 1));
			Util.write(filename.toString(), pair.getValue());
		}
	}
	
	private String insertCommitIdToJson(String refJSON, String commitId) throws JSONException {
		JSONObject jObj = new JSONObject(refJSON);
		jObj.put("commitId", commitId);
		return jObj.toString();
	}
}
