[![Build Status](https://travis-ci.org/Zomis/Minesweeper-Analyze.svg?branch=master)](https://travis-ci.org/Zomis/Minesweeper-Analyze?branch=master)


# Minesweeper Analyze


Fast, Flexible, Fabulous. Minesweeper analyzing Java library

This library makes it easy to analyze mine probabilities and probabilities for all different numbers on a Minesweeper board. There are abstractions available so that all you need to do is implement an interface that can tell if a specific field is clicked or not, what number is revealed, or if a mine is revealed.

The library takes care of the rest.

The details of how the library work can be found on the [wiki](https://github.com/Zomis/Minesweeper-Analyze/wiki/How-it-works)

# How to use

1. Create a new class that extends [AbstractAnalyze](https://github.com/Zomis/Minesweeper-Analyze/blob/master/src/main/java/net/zomis/minesweeper/analyze/factory/AbstractAnalyze.java)
2. Implement the missing methods
3. Create an instance of that class
4. call the `createRules` method on your instance
5. call the `solve` method and store the result

To also find the *detailed* probabilities:

6. On the `AnalyzeResult` object you got in the last step, call `analyzeDetailed` and pass the instance of the class you created in step 1 to that method.

# Maven dependency

Add a repository definition like this in your pom:

<repositories>
    <repository>
        <id>zomis</id>
        <name>Zomis' Maven Repository</name>
        <url>http://www.zomis.net/maven/</url>
    </repository>
</repositories>

Add this dependency in your pom:

<dependency>
    <groupId>net.zomis</groupId>
    <artifactId>mine-analyze</artifactId>
    <version>1.0</version>
</dependency>
