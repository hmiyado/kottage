# public/mockServiceWorker.js had diff
# https://github.com/hmiyado/kottage-front/runs/4930491171?check_suite_focus=true
yarn prettier --write --ignore-unknown "public/**/*"
if [ "$(git diff --name-only)" = public/mockServiceWorker.js ]; then
  if [ -n $SOURCE_BRANCH ]; then
    git fetch origin $SOURCE_BRANCH
    git switch $SOURCE_BRANCH
  fi
  git add public/mockServiceWorker.js
  git commit -m 'update public/mockServiceWorker.js'
  git push
  # update file but fail ci. ci re-runs on push above
  exit 1
fi