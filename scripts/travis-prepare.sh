#! /bin/bash

if [ "$TEST_ENV" = android-21 ]; then
  ./scripts/prepare-android-test-environment.sh

elif [ "$TEST_ENV" = android-16 ]; then
  ./scripts/prepare-android-test-environment.sh

else
  echo "Unknown test envirment: $TEST_ENV"
  exit 1
fi
