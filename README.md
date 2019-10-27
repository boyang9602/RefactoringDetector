# RefactoringDetector
A refactoring history detector based on a slightly **modified** [RefactoringMiner](https://github.com/boyang9602/RefactoringMiner). It will detect specified types refactoring activities, collect the java files before refactoring and classcify the files by refactoring type.  

**data** folder contains the collected data, below is the file orgnazition structure:  

data/  
&nbsp;&nbsp;ref_infos/ #refactoring infos  
&nbsp;&nbsp;&nbsp;&nbsp;{RefactoringType}/  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{Project name}/  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{id}.json #refactoring info, added the commitId on the orginal structure of RefactoringMiner  
&nbsp;&nbsp;src_code/  
&nbsp;&nbsp;&nbsp;&nbsp;{flag}/  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{commitId}/  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{package folder}/  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{original_name}.java  

The src code file location can be inferred from the ref_info file.  

[detected refactorings list](./manifests.md)  
