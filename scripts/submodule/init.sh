#!/bin/sh

git submodule update --init --recursive
cp scripts/submodule/sparse-checkout .git/modules/kottage/info/sparse-checkout
git -C kottage read-tree -mu HEAD
