import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
					if (++counts % 10 == 0) {
						System.out.println("Project: " + this.project.getName() + ", " + counts + " refactorings detected");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void writeRefInfo(String commitId, Refactoring ref, Project project) throws IOException {
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
			filename.append("data/src_code/").append(commitId).append("/").append(key.substring(0, key.lastIndexOf('/') + 1));
			filename.append(flag).append("_").append(key.substring(key.lastIndexOf('/') + 1));
			Util.write(filename.toString(), pair.getValue());
		}
	}
	
	private String insertCommitIdToJson(String refJSON, String commitId) {
		JSONObject jObj = new JSONObject(refJSON);
		jObj.put("commitId", commitId);
		return jObj.toString();
	}
}
