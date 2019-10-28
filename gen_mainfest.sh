#!/bin/bash
if [ -f manifests.md ]; then
	rm manifests.md
fi
declare -A project_username=( ["hadoop"]="apache" ["hive"]="apache" ["accumulo"]="apache" ["kafka"]="apache" ["junit"]="junit-team" ["elasticsearch"]="elastic" ["fastjson"]="alibaba" ["logstash"]="elastic" ["jenkins"]="jenkinsci" ["spring-framework"]="spring-projects" )

function gen_separate_line { 
	local i=0;
	local result='|';
	for ((i=0;i<$1;i++)); do
		result="${result} --- |"
	done
	echo $result;
}
base_dir="data/ref_infos"
ref_types=($(ls $base_dir))

echo "# Summary" >> manifests.md
line="| Projects |"
for ref_type in ${ref_types[*]}; do
	line="${line} [${ref_type}](./${ref_type}_list.md) |"
done
echo $line >> manifests.md
echo $(gen_separate_line ${#ref_types[@]}+1) >> manifests.md

for project in ${!project_username[@]}; do
	line="| ${project} |"
	for ref_type in ${ref_types[*]}; do
		if [ -d $base_dir/$ref_type/$project ]; then
			files=($(ls $base_dir/$ref_type/$project))
			line="${line} ${#files[@]} |"
		else
			line="${line} 0 |"
		fi
	done
	echo $line >> manifests.md
done

for ref_type in ${ref_types[*]}; do
	if [ -f "${ref_type}_list.md" ]; then
		rm "${ref_type}_list.md"
	fi
	echo "### $ref_type" >> "${ref_type}_list.md"
	projects=$(ls $base_dir/$ref_type/)
	for project in $projects; do
		echo "#### $project" >> "${ref_type}_list.md"
		files=$(ls $base_dir/$ref_type/$project)
		for file in $files; do
			commit_id=$(jq -r .commitId $base_dir/$ref_type/$project/$file)

			echo "$file: [$commit_id](https://github.com/${project_username[$project]}/$project/commit/$commit_id)" >> "${ref_type}_list.md"
		done
	done
done