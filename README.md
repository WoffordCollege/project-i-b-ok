# WoCoin
This repository has been set up to get you started in completing your capstone project.


## Project Details
------------------
### Introduction
In this project, you will be creating a marketplace for verified users to buy and sell items using your own cryptocurrency --- WoCoin. For this project, you will maintain a database of verified users and then deploy and interact with a private, permissioned blockchain using Ethereum that will serve as your WoCoin network. This network will allow only a single node (the managing node) to perform mining. All users will have to request funds from this node in order to transact business. This is a bit of a contrived example, but it keeps the problem tractable and allows the focus to be on learning. 

### Features Overview
The core features for this project are as follows:

1. Allow administrators to add users to the database.
2. Allow administrators to remove users from the database.
3. Allow users to generate a WoCoin wallet.
4. Allow users to add products to the database.
5. Allow users to remove products that they have added to the database.
6. Allow users to display products in the database.
7. Allow users to send messages to and check messages from other users.
8. Allow administrators to transfer WoCoins to a user's wallet.
9. Allow users to check their WoCoin balance.
10. Allow users to purchase products with their WoCoins.
11. Allow users/administrators to interact with the system through a Java Swing GUI.


### Grading Scheme
This project is worth 50 points (50% of the total grade for the course). These points are awarded in two ways. First, each of the 4 sprints end in a sprint review, where I evaluate your team's work. Each sprint is worth a maximum of 10 points for each member of the team. (Except in highly unusual circumstances, all members receive the same grade for a sprint.) Second, the final 10 points are determined according to how many features you complete by the project's final deadline. That scale is as follows, determined by the final feature that your team completes:

| Feature | Points |
|---------|--------|
| 1       | 0      |
| 2       | 0      |
| 3       | 0      |
| 4       | 0      |
| 5       | 1      |
| 6       | 2      |
| 7       | 4      |
| 8       | 6      |
| 9       | 7      |
| 10      | 10     |
| 11      | 13     |

Note that it is possible to receive more than 10 points for this component, with any additional points counting as "bonus" toward the final course grade.



## Technical Details
--------------------
### Building the Project
After you have cloned the repository, you should be able to navigate to the directory containing the **gradle.build** file. There, you can build the project by running the command

`gradlew build`

Then, you can run the unit test coverage report.

`gradlew jacocoTestReport`

Then, you can run the acceptance tests. 

`gradlew cucumber`

You can even do multiple things in one statement:

`gradlew build jacocoTestReport cucumber`

When you want to get rid of all of the temporary files (like compiled class files and such), you can say

`gradlew clean`

If you want to do a full build and reporting from a clean project, you can just string it all together:

`gradlew clean build jacocoTestReport cucumber`

If you want to create the generated documentation (based on your Javadoc comments), you can say

`gradlew javadoc`

And if you want to run the application you have created, you can say

`gradlew run`

Note that the application you are directed to create requires command-line arguments when it runs (for the feature number and the database file). To pass those values when running through Gradle, you would say

`gradlew run --args="0 testdb.db"`

Since I've already implemented a "feature 0" main function, this particular command will make a pre-populated test database (in the file "testdb.db") that you can play around with as you run your code.


### Useful Command-line Examples

To increase the maximum amount of memory that the JVM is allwed to use (512 MB in this case):
`JAVA_TOOL_OPTIONS="-Xmx512m" ./gradlew --console=plain --no-daemon run --args="1 mydb.db"`

To run only the acceptance tests that are tagged with **@rungeth**:
`CUCUMBER_OPTIONS='--tags "@rungeth"' ./gradlew --no-daemon cucumber`

To run only the acceptance tests that are *not* tagged with **@rungeth**:
`CUCUMBER_OPTIONS='--tags "not @rungeth"' ./gradlew --no-daemon cucumber`

The `JAVA_TOOL_OPTIONS` and `CUCUMBER_OPTIONS` can be combined in a single command:
`JAVA_TOOL_OPTIONS="-Xmx512m" CUCUMBER_OPTIONS='--tags "@rungeth"' ./gradlew --no-daemon cucumber`


### Directory Structure
The directory structure that is assumed by Gradle (it can be changed if you want, but that requires changes to the Gradle build file) is as follows:

    project root  (root directory of project)
               |
                - build.gradle  (contains the instructions for the build tasks)
               |
                - gradlew  (the Unix Gradle script)
               |
                - gradlew.bat  (the Windows Gradle script)
               |
                - LICENSE  (the license file, MIT in this case)
               |
                - settings.gradle  (the Gradle settings file)
               |
                - textio.properties  (the configuration file for the textio library)
               |
                - ethereum  (information and configuration for the Ethereum network goes here)
               |
                - gradle  (all configuration and scripts for the Gradle wrapper go here)
               |
                - samples  (sample files go here; e.g. Ethereum wallets)
               |
                - src  (root directory of the source code; acceptance, main, test)
                    |
                     - main  (root directory of normal source code)
                    |     |
                    |      - java  (all packages go here)
                    |     |     |
                    |     |      - edu    
                    |     |          |
                    |     |           - wofford
                    |     |                   |
                    |     |                    - wocoin  (source code goes here)
                    |     |                           |
                    |     |                            - main  (main classes go here)
                    |     |      
                    |      - resources  (possibly unnecessary/nonexistent resource directory)
                    |
                     - test  (root directory of unit and acceptance test code)
                          |
                           - java  (all packages go here)
                          |     |
                          |      - edu    
                          |     |    |
                          |     |     - wofford
                          |     |             |
                          |     |              - wocoin  (unit test source code goes here)
                          |     |
                          |      - gradle
                          |     |       |
                          |     |        - cucumber  (cucumber step definition source code goes here)
                          |
                           - resources
                                     |
                                      - gradle
                                             |
                                              - cucumber  (cucumber feature files go here)


After you run `gradlew build`, a new **build** directory will automatically be created. 
This will contain all of the generated files (compiled class files, JAR files, reports, 
etc.). The most important things here are as follows:

**build/reports/tests/index.html**
: This file holds the results of all of the unit tests.

**build/libs/*name*.jar**
: This file (where *name* is specified in the jar settings of **build.gradle**) 
  is the fully bundled code for the project. You can run this by saying
  `java -jar build/libs/<name>.jar`
  from the project root. However, if you intend to run the program this way, you
  will need to uncomment the lines in **build.gradle** that introduce the plugin
  **com.github.johnrengelman.shadow**. This plugin will run as a part of the build
  step and create a "fat jar" that contains all dependency code in a single jar
  file. Then you can run this jar by saying
  `java -jar build/libs/<name>-all.jar`

After you run `gradlew cucumber`, a **reports/cucumber** directory will be 
created in the **build** directory. This will contain the reports for the 
acceptance tests.

**build/reports/cucumber/index.html**
: This file holds the Cucumber acceptance test results.

After you run `gradlew jacocoTestReport`, a **reports/jacoco/test/html** directory 
will be created in the **build** directory. This will contain the reports for the 
Jacoco code coverage.
  
**build/reports/jacoco/test/html/index.html**
: This file holds the unit test code coverage results from JaCoCo.

After you run `gradlew javadoc`, a **docs** directory will be created in the project 
root. This will contain all of the generated Javadoc documentation for your 
source files.  

**docs/javadoc/index.html**
: This file is the index to the generated documentation.


### IDE Setup

#### Intellij
If you use Intellij, you can set up gradle for it as follows:

`gradlew cleanIdea idea`


#### Eclipse
If you use Eclipse, you can set up gradle for it as follows:

`gradlew cleanEclipse eclipse`

