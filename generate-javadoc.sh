#!/bin/bash
echo "Enter version number"
read VERSION
echo "Enter Repo Name (e.g. origin)"
read REPO
mvn javadoc:javadoc
mv -v target/site/apidocs/* ../tmp_javadoc/.
git stash
git checkout gh-pages
find . | grep -v ".git*" | grep -v ".project"| grep -v ".settings*" | grep -v ".classpath" | xargs rm -rf
mv -v ../tmp_javadoc/* .
git add .
git commit -a -m "Add javadoc $VERSION"
git push $REPO gh-pages
git checkout -
git stash apply

