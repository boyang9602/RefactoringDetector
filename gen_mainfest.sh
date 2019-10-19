#!/bin/bash
rm manifests.md
declare -A project_username=( ["hadoop"]="apache" ["hive"]="apache" ["accumulo"]="apache" ["kafka"]="apache" ["junit"]="junit-team" )

base_dir="data/ref_infos"
ref_types=$(ls $base_dir)

echo "# Summary" >> manifests.md
for ref_type in $ref_types; do
	echo "### $ref_type:" >> manifests.md
	projects=$(ls $base_dir/$ref_type)
	for project in $projects; do
		files=($(ls $base_dir/$ref_type/$project))
		echo "- $project: ${#files[@]}" >> manifests.md
	done
done

echo "# List" >> manifests.md
for ref_type in $ref_types; do
	echo "### $ref_type" >> manifests.md
	projects=$(ls $base_dir/$ref_type/)
	for project in $projects; do
		echo "#### $project" >> manifests.md
		files=$(ls $base_dir/$ref_type/$project)
		for file in $files; do
			echo "##### [$file](./$base_dir/$ref_type/$project/$file):  " >> manifests.md
			commit_id=$(jq -r .commentId $base_dir/$ref_type/$project/$file) # commitId is wrongly written as commitId in json file

			echo "Commit: [$commit_id](https://github.com/${project_username[$project]}/$project/commit/$commit_id)"
		done
	done
done