# Compare Code
> A CLI to identify how similar student code is using Levenshtein distance

This program compares all student code submissions to each other and generates a normalized [levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance) to identify how similar each file is.

## Features

This project makes it easy to:
* Identify if several students turned in the same assignment
* If several assignments are the same, but with a few cosmetic changes

## Usage

All you need to use this project are the jar files from the `/dist` folder, java installed on your machine, and access to the command line.

## Structure of Student Code

If your students wrote their code in the file Foo.java then CodeCheker expects the student files to be stored in the following structure:

```
/Path/To/Labs/LabName/StudentName1/Foo.java
                     /StudentName2/Foo.java
                     /StudentName2/Foo.java
```

The `labPath` in this case would be `/Path/To/LabName`

The `testFile` would be `Foo.java`

Conveniently, this folder structure is exactly the same as how the [Turn CS In](https://github.com/hdctambien/turncsin) web application stores student assignments.

## Comparing Code

```
java -jar CodeChecker.jar labPath testFile > similarity.csv
```

The results of the comparisons will be stored in the similarity.csv file. You can name this file whatever you like.

Each *pair of* students will be assigned a Similarity score between 0 and 100. Each student's name will appear in its own column.

Note: each pair of students will appear in this data twice:

* Student1, Student2
* Student2, Student1

> A high Levenshtein score is to be expected when students are all writing the same code.

You can also generate some statistics about the code similarities to help identify what an appropriate Levenshtein distance would be for unique code.

Include the optional `stats` argument to enable stats-mode.

```
java -jar CodeChecker.jar labPath testFile stats > similarity-stats.csv
```

This will create a file called similarity-stats.csv which includes:

* Total number of assignments checked
* Sum of all assignment Levenshtein scores
* The minimum Levenshtein score
* The maximum Levenshtein score
* mean
* mode
* median
* standard deviation
* range of 1 standard deviation from the mean
* range of 2 standard deviations from the mean
* range of 3 standard deviations from the mean
* count of assignments that fall within 1 standard deviation of the mean
* count of assignments that fall within 2 standard deviations of the mean (and outside 1)
* count of assignments that fall outside 2 standard deviations of the mean

## Building the Project

This project uses the [java-string-similarity](https://github.com/tdebatty/java-string-similarity) library version 1.0.1

To compile it, use:

```
javac -cp .;java-string-similarity-1.0.1.jar MainWindow.java
```

You can create runnable jar files after you compile the code by creating a file called `Manifest` with the following content

```
Manifest-Version: 1.0
Main-Class: MainWindow
```

You then need to extract the java-string-similarity.jar file into the `/info` folder and run the following command

```
jar cvfm CodeChecker.jar Manifest *.class info
```

## Licensing

This project is licensed under MIT license. A short and simple permissive license with conditions only requiring preservation of copyright and license notices. Licensed works, modifications, and larger works may be distributed under different terms and without source code.
