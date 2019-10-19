# RefactoringDetector
A refactoring history detector based on a slightly **modified** [RefactoringMiner](https://github.com/boyang9602/RefactoringMiner). It will detect specified types refactoring activities, collect the java files before refactoring and classcify the files by refactoring type.  

**data** folder is all the collected data, below is the file orgnazition structure:  
data/
  ref_infos/ *refactoring infos*
  	{RefactoringType}/
  		{Project name}/
  			{id}.json *refactoring info, added the commitId on the orginal structure of [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner)*
  src_code/
  	{commitId}/
  		{package folder}/
  			{flag}/ *before or current*
  				*.java

The src code file location can be inferred from the ref_info file.
