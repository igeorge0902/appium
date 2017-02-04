Sample Appium TestNG project
---

This contains the source code for running sample [Appium](http://github.com/appium/appium) tests using [TestNG](http://www.testng.org).

In order to run the tests, you will need to install [Apache Maven](http://maven.apache.org), and Appium (according to the Appium [installation instructions](https://github.com/appium/appium).

You will then need to start appium without any configuration, eg:

    grunt appium

To install and run all tests, run:

    mvn -Dmaven.test.skip=true package
    
To use a different .apk on different device, simply update the parameters in the testng.xml. (not all devices are implemented yet)

# Sample codes
This repository contains sample applications which are used mostly by appium functional tests:

https://github.com/appium/sample-code/blob/master/sample-code/examples/java/junit/src/test/java/com/saucelabs/appium/AndroidContactsTest.java

    