# ReMap - How to contribute

First of all: Feel free to contribute to this project! We appreciate any help to develop this library and make it nice, stable and feature complete.

The ReMap project was started to minimize the need of converter classes and corresponding unit tests. There are features that require a user to write tests and therefore are not added to the library. We try to check every new feature carefully before merging pull requests!

This project provides a checkstyle rule configuration as well as formatter configurations for Eclipse and IntelliJ IDEA.

The formatter configurations of both supported IDEs were created to produce similar outputs. Although there are slightly different formattings, the outputs should be compliant to the Checkstyle rule set.

## Contributing Features

When contributing features, any suggestions to extend the README of this project are welcome :-)

### Testing features

When writing test for this project, please make sure to create a new package for your test. This avoids unnecessary dependencies to other test classes.

## Contributing bug fixes

When contributing bug fixes, please first write a test that provokes the bug. Please use the package `com.remondis.remap.regression` for that.

## Contribution resources

You can find the IDE formatter and the Checkstyle rule set in `shared/config`.

## Lombok

This project uses [Lombok](https://projectlombok.org/) to generate `Object.hashCode()`, `Object.equals()` and get/set-methods. Please visit the Lombok website for additional instructions how to setup your IDE to use Lombok. __You might get compiler errors when building the sources while Lombok isn't active!__


