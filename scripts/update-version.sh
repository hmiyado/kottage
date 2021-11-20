NEW_VERSION=$1
echo "set version to ${NEW_VERSION}"
sed -e "s/^version = \".*\"/version = \"${NEW_VERSION}\"/g" build.gradle.kts > build.gradle.kts.tmp
mv build.gradle.kts.tmp build.gradle.kts
git add build.gradle.kts
git commit -m "version bump to ${NEW_VERSION}"
echo "successfully version is set to ${NEW_VERSION}"
