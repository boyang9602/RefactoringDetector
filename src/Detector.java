import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class Detector extends Thread {
	private Project project;
	private RefactoringType[] consideredRefactoringTypes;
	
	public Detector(Project project, RefactoringType[] consideredRefactoringTypes) {
		this.project = project;
		this.consideredRefactoringTypes = consideredRefactoringTypes;
	}
	
	public void run() {
		System.out.println("project " + this.project.getName() + "'s processing started");
		try {
			GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
			((GitHistoryRefactoringMinerImpl)miner).setRefactoringTypesToConsider(consideredRefactoringTypes);
			GitService gitService = new GitServiceImpl();
			Repository repo = gitService.cloneIfNotExists("tmp/" + this.project.getName(), this.project.getRepoAddr());
			miner.detectBetweenTags(repo, this.project.getStart(), this.project.getEnd(), new WritingFileRefactoringHandler(this.project));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("project " + this.project.getName() + "'s processing finished");
	}

	public static void main(String[] args) throws Exception {
		Project[] projects = new Project[] {
			new Project("junit", "junit-team", "r4.11", "r4.12", Project.FLAG_TYPE.TAG),
			new Project("kafka", "apache", "2.1.1", "2.2.2-rc1", Project.FLAG_TYPE.TAG),
			new Project("hadoop", "apache", "release-3.2.0-RC1", "release-3.2.1-RC0", Project.FLAG_TYPE.TAG),
			new Project("hive", "apache", "release-2.3.5-rc0", "release-3.1.2-rc0", Project.FLAG_TYPE.TAG),
			new Project("accumulo", "apache", "rel/2.0.0-alpha-1", "rel/2.0.0", Project.FLAG_TYPE.TAG)
		};
		RefactoringType[] consideredRefactoringTypes = new RefactoringType[] {
			RefactoringType.EXTRACT_OPERATION,
			RefactoringType.MOVE_OPERATION,
			RefactoringType.EXTRACT_SUBCLASS,
			RefactoringType.EXTRACT_AND_MOVE_OPERATION,
			RefactoringType.EXTRACT_VARIABLE,
			RefactoringType.INLINE_VARIABLE
		};
		
		for(Project p : projects) {
			new Detector(p, consideredRefactoringTypes).start();
		}
	}
}
