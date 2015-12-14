#! /bin/bash
set -e

command -v adb >/dev/null 2>&1 || { brew install android-sdk; }

TRUE=1

# FILTER=tool,platform,android-23,build-tools-23.0.1
# ( sleep 5 && while [ $TRUE ]; do sleep 1; echo y; done ) \
#   | android update sdk --no-ui --filter ${FILTER} --all

brew cask install genymotion

~/Applications/Genymotion.app/Contents/MacOS/gmtool config username="$GENYMOTION_USERNAME" password="$GENYMOTION_PASSWORD"
~/Applications/Genymotion.app/Contents/MacOS/gmtool license register "$GENYMOTION_LICENSE"

if [ "$TEST_ENV" = android-16 ]; then
  ~/Applications/Genymotion.app/Contents/MacOS/gmtool admin create 'Google Nexus 7 - 4.1.1 - API 16 - 800x1280' android-test
  ~/Applications/Genymotion.app/Contents/MacOS/gmtool admin start android-test

else
  ~/Applications/Genymotion.app/Contents/MacOS/gmtool admin create 'Google Nexus 7 - 5.0.0 - API 21 - 800x1280' android-test
  ~/Applications/Genymotion.app/Contents/MacOS/gmtool admin start android-test

fi

EMULATOR_BOOT_COMPLETE=$(adb shell getprop sys.boot_completed | tr -d '\r')

echo -n "Waiting for Android emulator to boot ..."

while [ "$EMULATOR_BOOT_COMPLETE" != "1" ]; do
  sleep 2
  EMULATOR_BOOT_COMPLETE=$(adb shell getprop sys.boot_completed | tr -d '\r')
  echo -n '.'
done

echo '.'

echo "Done preparing Android environment"
exit 0
