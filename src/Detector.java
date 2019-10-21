import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

public class Detector {
	private Project[] projects;
	private RefactoringType[] consideredRefactoringTypes;
	
	public Detector(Project[] projects, RefactoringType[] consideredRefactoringTypes) {
		this.projects = projects;
		this.consideredRefactoringTypes = consideredRefactoringTypes;
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
		Repository repo = gitService.cloneIfNotExists("tmp/" + p.getName(), p.getRepoAddr());
		miner.detectBetweenTags(repo, p.getStart(), p.getEnd(), new WritingFileRefactoringHandler(p));
	}

	public static void main(String[] args) throws Exception {
		Detector detector = new Detector(new Project[] {
				new Project("junit", "junit-team", "r4.11", "r4.12", Project.FLAG_TYPE.TAG),
				new Project("kafka", "apache", "2.1.1", "2.2.2-rc1", Project.FLAG_TYPE.TAG),
				new Project("hadoop", "apache", "release-3.2.0-RC1", "release-3.2.1-RC0", Project.FLAG_TYPE.TAG),
				new Project("hive", "apache", "release-2.3.5-rc0", "release-3.1.2-rc0", Project.FLAG_TYPE.TAG),
				new Project("accumulo", "apache", "rel/2.0.0-alpha-1", "rel/2.0.0", Project.FLAG_TYPE.TAG)
			},
				new RefactoringType[] {
					RefactoringType.EXTRACT_OPERATION,
					RefactoringType.MOVE_OPERATION,
					RefactoringType.EXTRACT_SUBCLASS,
					RefactoringType.EXTRACT_AND_MOVE_OPERATION,
					RefactoringType.EXTRACT_VARIABLE,
					RefactoringType.INLINE_VARIABLE
				});
		detector.detectAll();
	}
}
