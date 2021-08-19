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
    "class/13-messages"
    "class/14-external-task"
)

echo "--------------------------------------------------"
echo "Githelper script for propagating changes"
echo "across ${#BRANCHES[*]} branches starting with ${BRANCHES[0]}."
echo "--------------------------------------------------"

git fetch --all

COMMAND=""

case "$1" in
  "build")
    echo "Build command detected, will execute build of every branch"
    COMMAND="./mvnw clean install -T8 -B"
    ;;
  "push")
    echo "Push command detected, will push every branch"
    COMMAND="git push origin"
    ;;
  "pull")
    echo "Pull command detected, will pull every branch"
    COMMAND="git pull origin"
    ;;
  "push-public")
    echo "Push to publi command detected. will push every."
    COMMAND="git push public"
    ;;
  *)
    echo "No command detected, will just merge branches but let them locally. Try $0 build | pull | push | push-public"
    ;;
esac

## now loop through the above array
for (( i = 1; i < ${#BRANCHES[*]}; ++ i ))
do
    PREVIOUS="${BRANCHES[i-1]}"
    CURRENT="${BRANCHES[i]}"

    echo "-----"
    echo "Checking out $CURRENT"
    git checkout $CURRENT
    echo "Merging changes from $PREVIOUS branch to $CURRENT"
    git merge $PREVIOUS --no-edit

    echo "Executing command $COMMAND"
    `$COMMAND`
done

git checkout ${BRANCHES[0]}
