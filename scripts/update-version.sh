NEW_VERSION=$1
echo "set version to ${NEW_VERSION}"
# update build.gradle
sed -e "s/^version = \".*\"/version = \"${NEW_VERSION}\"/g" build.gradle.kts > build.gradle.kts.tmp
mv build.gradle.kts.tmp build.gradle.kts
# update infra/variables.tf
sed -e "s|\"miyado/kottage:.*\"|\"miyado/kottage:${NEW_VERSION}\"|g" infra/variables.tf > variables.tf.tmp
mv variables.tf.tmp infra/variables.tf
# commit version bump
git add build.gradle.kts infra/variables.tf
git commit -m "version bump to ${NEW_VERSION}"
echo "successfully version is set to ${NEW_VERSION}"
