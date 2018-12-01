#!/bin/bash


#
# Branches
#
declare -a BRANCHES=(
    "master"
    "class/1-process-model"
    "class/2-java-delegate"
    "class/3-test-it"
    "class/4-test-it-to-the-end"
    "class/5-delegate-testing"
    "class/6-automatic-approval"
    "class/7-human-workflow"
    "class/8-data-spin"
    "class/9-dmn"
    "class/10-listeners"
    "class/11-bpmn-error"
    "class/12-timer"
)

git pull origin ${BRANCHES[0]} --rebase

## now loop through the above array
for (( i = 1; i < ${#BRANCHES[*]}; ++ i ))
do
    PREVIOUS="${BRANCHES[i-1]}"
    CURRENT="${BRANCHES[i]}"

    echo "Checking out $CURRENT"
    git checkout $CURRENT
    echo "Getting latest changes"
    git pull origin $CURRENT --rebase
    echo "Merging changes from $PREVIOUS"
    git merge $PREVIOUS --no-edit
    echo "Pushing results"
    git push
done

git checkout ${BRANCHES[0]}