#!/bin/bash
rm manifests.md
declare -A project_username=( ["hadoop"]="apache" ["hive"]="apache" ["accumulo"]="apache" ["kafka"]="apache" ["junit"]="junit-team" ["elasticsearch"]="elastic" ["fastjson"]="alibaba" ["logstash"]="elastic" ["jenkins"]="jenkinsci" ["spring-framework"]="spring-projects" )

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
			commit_id=$(jq -r .commitId $base_dir/$ref_type/$project/$file)

			echo "Commit: [$commit_id](https://github.com/${project_username[$project]}/$project/commit/$commit_id)" >> manifests.md
		done
	done
done