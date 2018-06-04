AndroidScreencast
=================

[![Build Status Travis-CI][travis_badge]][travis]&nbsp;
[![Dependency Status][versioneye_badge]][versioneye]&nbsp;
[![Codacy Badge][codacy_badge]][codacy]&nbsp;
[![Join the chat at https://gitter.im/AndroidScreencast/Lobby][gitter_badge]][gitter]&nbsp;
[![Apache License][license_badge]][license]

# Description

AndroidScreencast was developed to view and control your android device from a PC.

This project gives the opportunity to use your phone even with a broken screen.

## Features

- No client needed
- Support for Tap and Swipe gestures
- Write messages using PC keyboard
- Support for landscape mode
- Browse your phone files on PC
- Record video of your phone screen while browsing

[Small wiki of project][wiki]

[wiki]: https://github.com/xSAVIKx/AndroidScreencast/wiki

## Configuration

There are 2 ways to run application:
1. Run ADB server on your own
2. Provide correct `app.properties` file

### Run ADB server on your own

In order to run server on your own you have to run already installed (or one from our OS-specific bundle) `adb`
with following command:
```
adb start-server
```

**Note**:

If `app.properties` will have `adb.path` filled - AndroidScreencast will shutdown ADB server on application termination.

Additionally, in order to use, for example, remote ADB server with ssh forwarding you should not use `app.properties` or `adb.path`
property should be commented.

### Provide correct `app.properties` file

Right now to successfully run application you **should** create `app.properties` file in the same folder as
AndroidScreencast's jar and provide at least one property - `adb.path` which should point to the `adb` executable file
within your OS, for example such location could be configured for Windows OS:
```properties
adb.path=adb/windows/adb.exe
```

Both absolute and relative paths are supported.

Example of valid `app.properties` could be find [here](app.properties).

Also we have got some valid OS-specific examples:
* [OSX app.properties](adb/macosx/app.properties)
* [Windows app.properties](adb/windows/app.properties)
* [Linux app.properties](adb/linux/app.properties)

They are actually the same, just `adb.path` is OS-specific. Files listed above are automatically included into OS-specific bundles.


### Additional properties
Here is the full list of available properties for application configuration.
Properties should be places in the `app.properties` file in the same folder as AndroidScreencast's jar file.

```properties
#relative or absolute path to ADB
adb.path=./adb
#maximum time to wait for device (in seconds)
adb.device.timeout=30
#maximum time to execute adb command (in seconds)
adb.command.timeout=5
#initial application window width (in pixels)
app.window.width=1024
#initial application window height (in pixels)
app.window.height=768
#Defines whether application should look 'natively' to OS. Possible values: true/false
app.native.look=false
```
## JNLP

Application is available using Java web start technology via [androidscreencast.jnlp][jnlp].

[jnlp]: http://xsavikx.github.io/AndroidScreencast/jnlp/androidscreencast.jnlp

### Java security configuration

Due to Java security restriction policy, that was updated in java 7 and is restricted even more in java 8, we're now not
able to run JNLP without some security "hacks".

To use JNLP, please follow this article: [How can I configure the Exception Site List?][exception]

[exception]: https://www.java.com/en/download/faq/exception_sitelist.xml

## Building and running from source

This project requires at least Java 8.

After cloning the project, run `mvnw install`.

The resulting artifacts will be created in the `target` subdirectory.

You can run the executable jar via `java -jar target/androidscreencast-VERSION-executable.jar`, replacing VERSION with
the current version.

For example, `java -jar target/androidscreencast-0.0.10s-executable.jar`.

Additionally OS-packages would be created with ADB executables bundled:
* `androidscreencast-VERSION-windows.zip`
* `androidscreencast-VERSION-linux.tar.gz`
* `androidscreencast-VERSION-macosx.tar.gz`

In order to provide correct configuration see [configuration][configuration_section] section.

[configuration_section]: https://github.com/xSAVIKx/AndroidScreencast/blob/develop/README.md#configuration

# Requirements

Currently AndroidScreencast works directly with `adb input` program through `ddmlib` and abuse functionality of:
* `adb input key`
* `adb input tap`
* `adb input swipe`

Regarding this point, to use AndroidScreencast you need Smartphone running on Android with specific `input` program version. `adb input tap` and `adb input swipe` were introduced in [Android 4.1.1][Android_4_1_1_Input].

So, right now AndroidScreencast support all Android versions equal or greater than Android 4.1.1.

Also, to run AndroidScreencast you will need *adb* installed (or you can use bundled in OS bundles adb).

# Similar Projects

* [Seven Square][seven_square] - QT implementation of Android Screencast (actively developed)
* [Droid@Screen][droid_at_screen] - implementation of Android Screencast in Java (fancy one, last release in 2013)
* [Android Screen Monitor][android_screen_monitor] - implementation of Android Screencast in Java (latest release in 2013)

[Android_4_1_1_Input]: http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.1.1_r1/com/android/commands/input/Input.java#Input
[seven_square]: https://github.com/yangh/sevensquare
[droid_at_screen]: http://droid-at-screen.org/droid-at-screen/
[android_screen_monitor]: https://github.com/adakoda/android-screen-monitor

[travis_badge]: https://travis-ci.org/xSAVIKx/AndroidScreencast.svg?branch=master
[travis]: https://travis-ci.org/xSAVIKx/AndroidScreencast

[versioneye_badge]: https://www.versioneye.com/user/projects/58a746d8b4d2a20055fcb887/badge.svg?style=flat
[versioneye]: https://www.versioneye.com/user/projects/58a746d8b4d2a20055fcb887

[codacy_badge]: https://api.codacy.com/project/badge/Grade/064bbd2582b544bb9659a01a2872317c
[codacy]: https://www.codacy.com/app/xSAVIKx/AndroidScreencast?utm_source=github.com&utm_medium=referral&utm_content=xSAVIKx/AndroidScreencast&utm_campaign=badger

[license_badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0

[gitter_badge]: https://badges.gitter.im/AndroidScreencast/Lobby.svg?style=flat
[gitter]: https://gitter.im/AndroidScreencast/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
