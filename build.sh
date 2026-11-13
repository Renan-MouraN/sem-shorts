#!/bin/bash
set -e
T=/home/claude/tools; P=/home/claude/semshorts; cd $P
rm -rf out && mkdir -p out/classes
javac --release 8 -Xlint:-options -cp $T/platform/android-platforms/android-34/android.jar -d out/classes $(find src -name '*.java')
java -cp $T/dex2jar/dex-tools-v2.4/lib/dx-30.0.2.jar com.android.dx.command.Main --dex --min-sdk-version=26 --output=apk/classes.dex out/classes
java -jar $T/apktool/apktool.jar b apk -o out/unsigned.apk
[ -f sign.jks ] || keytool -genkeypair -keystore sign.jks -storepass semshorts -keypass semshorts -alias semshorts -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Sem Shorts" 2>/dev/null
java -jar $T/signer/uber-apk-signer.jar -a out/unsigned.apk --ks sign.jks --ksAlias semshorts --ksPass semshorts --ksKeyPass semshorts -o out/signed
ls -la out/signed
