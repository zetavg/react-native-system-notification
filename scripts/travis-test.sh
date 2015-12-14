#! /bin/bash

if [ "$TEST_ENV" = android-23 ]; then
  ./scripts/test-android.sh

elif [ "$TEST_ENV" = android-16 ]; then
  ./scripts/test-android.sh

else
  echo "Unknown test envirment: $TEST_ENV"
  exit 1
fi
