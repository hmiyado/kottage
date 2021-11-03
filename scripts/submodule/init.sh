#!/bin/sh

git submodule update --init --recursive
git -C kottage config core.sparsecheckout true
cp scripts/submodule/sparse-checkout .git/modules/kottage/info/sparse-checkout
git -C kottage read-tree -mu HEAD
