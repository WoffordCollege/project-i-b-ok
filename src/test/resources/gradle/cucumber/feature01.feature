Feature: Allow administrators to add users to the database.
  
  The administrator should be able to add users to the database.
  The program should take a single command-line argument for the database file.
  It should present the user with a menu of three numbered options
  as follows:
  
  1: exit
  2: administrator

  Choosing option 1 exits the program. Choosing option 2 prompts for the
  administrator password ("adminpwd" in this case) and, if the correct password
  is given, shows the following menu:
  
  1: back
  2: add user

  Choosing option 1 returns the administrator to the top-level menu (exit/administrator).
  Choosing option 2 prompts the administrator for the username and password
  of the user to be added. A user can only be added if the username is not
  currently in the database.


  Instructor's Note
  -----------------
  You will want to use the sqlite-jdbc library for this feature. You will need
  to find the documentation and learn how to interact with a sqlite database.
  The Utilities.java file contains the schema for the database in the static
  method createNewDatabase. This schema should not be changed in any way; it is
  a requirement of the product owner.



  Scenario: The user requests to perform as administrator with the wrong password.
    Given the feature is 1
    And I attempt to perform as administrator with password "wrong"
    Then I should be told "Incorrect administrator password."

  Scenario: The user to be added is not in the database because the database is empty.
    Given the feature is 1
    And the database is empty
    And I attempt to perform as administrator with password "adminpwd"
    When I add the user "jsmith" with password "jsmith"
    Then I should be told "jsmith was added."
    And the database should contain the user "jsmith" with password "jsmith"

  Scenario: The user to be added is not in the database but another user is.
    Given the feature is 1
    And the database contains the user "hjones" with password "hjones"
    And I attempt to perform as administrator with password "adminpwd"
    When I add the user "jsmith" with password "jsmith"
    Then I should be told "jsmith was added."
    And the database should contain the user "jsmith" with password "jsmith"
    And the database should contain the user "hjones" with password "hjones"

  Scenario: The user to be added already exists in the database with a different password.
    Given the feature is 1
    And the database contains the user "jsmith" with password "jsmith"
    And I attempt to perform as administrator with password "adminpwd"
    When I add the user "jsmith" with password "abcdef"
    Then I should be told "jsmith already exists."
    And the database should contain the user "jsmith" with password "jsmith"

  Scenario: The user to be added already exists in the database with the same password.
    Given the feature is 1
    And the database contains the user "jsmith" with password "jsmith"
    And I attempt to perform as administrator with password "adminpwd"
    When I add the user "jsmith" with password "jsmith"
    Then I should be told "jsmith already exists."
    And the database should contain the user "jsmith" with password "jsmith"

