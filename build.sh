#!/bin/bash
export BUILD_NUMBER=101

cd ../GMALauncherCore/
mvn clean install

cd ../GMALauncher/
mvn clean package

cp ./target/launcher-3.0-$BUILD_NUMBER.jar ./build/GMALauncher.jar
cp ./target/launcher-3.0-$BUILD_NUMBER-osx.app.zip ./build/GMALauncherOsx.app.zip

mvn package -P package-win

echo
echo "Built version $BUILD_NUMBER"