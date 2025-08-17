git status --porcelain
if [ -n "$(git status --porcelain)" ]; then
  echo "repository must be clean before patch-package" 1>&2
  exit 1
fi

yarn patch-package next
if [ -n "$(git status --porcelain)" ]; then
  if [ -n $SOURCE_BRANCH ]; then
    git fetch origin $SOURCE_BRANCH
    git switch $SOURCE_BRANCH
  fi
  git add -A
  git commit -m 'update patch-package next'
  git push
  echo "update file but fail ci. ci re-runs on push above" 1>&2
  exit 1
fi
