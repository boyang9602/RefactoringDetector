import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class Detector {
	private Project[] projects;
	public final RefactoringType[] consideredRefactoringTypes;
	
	public Detector() {
		this.projects = new Project[] {
			new Project("junit", "https://github.com/junit-team/junit4.git", "r4.11", "r4.12"),
			new Project("kafka", "https://github.com/apache/kafka.git", "2.1.1", "2.2.2-rc1"),
			new Project("hadoop", "https://github.com/apache/hadoop.git", "3.2.0-RC1", "3.2.1-RC0"),
			new Project("hive", "https://github.com/apache/hive.git", "release-2.3.5-rc0", "release-3.1.2-rc0"),
			new Project("accumulo", "https://github.com/apache/accumulo.git", "rel/2.0.0-alpha1", "rel/2.0.0")
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
			this.detect(miner, p);
		}
	}
	
	public void detect(GitHistoryRefactoringMiner miner, Project p) throws Exception {
		GitService gitService = new GitServiceImpl();
		Repository repo = gitService.cloneIfNotExists("/home/bo/projects/refactoring/tmp/" + p.getName(), p.getRemoteAddr());
		miner.detectBetweenTags(repo, p.getStartTag(), p.getEndTag(), new RefactoringHandler() {
			  @Override
			  public void handle(String commitId, List<Refactoring> refactorings, 
					  Map<String, String> fileContentsBefore, Map<String, String> fileContentsCurrent) {
				if(refactorings.size() > 0) {
				    for (Refactoring ref : refactorings) {
						ref.toJSON();
				    }
				}
			  }
			});
	}

	public static void main(String[] args) throws Exception {
		Detector detector = new Detector();
		detector.detectAll();
	}
}
