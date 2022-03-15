if [ -n "(git diff --name-only)"]; then
  # repository must be clean
  exit 1
fi

yarn patch-package next
if [ -n "$(git diff --name-only)"]; then
  if [ -n $SOURCE_BRANCH ]; then
    git checkout $SOURCE_BRANCH
  fi
  git add -A
  git commit -m 'update patch-package next'
  git push
  # update file but fail ci. ci re-runs on push above
  exit 1
fi
