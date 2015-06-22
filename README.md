AndroidScreencast
=================
[![Build Status](https://drone.io/github.com/xSAVIKx/AndroidScreencast/status.png)](https://drone.io/github.com/xSAVIKx/AndroidScreencast/latest)

This project is fork of https://code.google.com/p/androidscreencast/.



If you see image, but don't have mouse or keyboard control, you can try this:

Run androidscreencast.jar: 
```
java -jar androidscreencast.jar port 8776
```
after that go to adb shell and execute: 
```
adb shell 
su 
export CLASSPATH=/data/local/tmp/MyInjectEventApp.jar 
exec app_process /system/bin com.github.xsavikx.android.screencast.client.Main 8776
```
if `exec app_process` doesn't work, try this one:
```
/system/bin/dalvikvm -classpath /data/local/tmp/MyInjectEventApp.jar com.github.xsavikx.android.screencast.client.Main 8776
```
You should see in adb shell logs from inject app, if so - everything should work in GUI
