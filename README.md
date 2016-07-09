AndroidScreencast
=================
[![Build Status Travis-CI](https://travis-ci.org/xSAVIKx/AndroidScreencast.svg?branch=master)](https://travis-ci.org/xSAVIKx/AndroidScreencast)
#Description

AndroidScreencast application was developed to view and control your android iDevice on PC.

This project gives you opportunity to use your phone even with broken screen.

##Features
  - No client needed
  - Support for Tap and Swipe gestures
  - Write messages using PC keyboard
  - Support for landscape mode
  - Browse your phone files on PC


[Small wiki of project](https://github.com/xSAVIKx/AndroidScreencast/wiki)

## JNLP

Application is available using Java web start technology via [androidscreencast.jnlp](http://xsavikx.github.io/AndroidScreencast/jnlp/androidscreencast.jnlp).

### Java security configuration

Due to Java security restriction policy, that was updated in java 7 and
is restricted even more in java 8, we're now not able to run JNLP without some security "hacks".

To use JNLP, please follow this article: [How can I configure the Exception Site List?](https://www.java.com/en/download/faq/exception_sitelist.xml)

##Building and running from source

This project requires at least Java 7 and Maven 3.2.5

After cloning the project, run `mvn package`

The resulting artifacts will be created in the `target` subdirectory

You can run the executable jar via `java -jar target/androidscreencast-VERSION-executable.jar`, replacing VERSION with the current version.

For example, `java -jar target/androidscreencast-0.0.7s-executable.jar`
