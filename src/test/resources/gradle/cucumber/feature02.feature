Feature: Allow administrators to remove users from the database.
  
  The administrator should be able to remove users from the database.
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
  3: remove user
  
  Choosing options 1 and 2 work as in the previous feature.
  Choosing option 3 prompts the administrator for the username of the user to
  be removed. A user can only be removed if the username is in the database.
  

  Scenario: The user requests to perform as administrator with the wrong password.
    Given the feature is 2
    And I attempt to perform as administrator with password "wrong"
    Then I should be told "Incorrect administrator password."

  Scenario: The user to be removed is not in the database because the database is empty.
    Given the feature is 2
    And the database is empty
    And I attempt to perform as administrator with password "adminpwd"
    When I remove the user "jsmith"
    Then I should be told "jsmith does not exist."
    And the database should not contain the user "jsmith"

  Scenario: The user to be removed is not in the database but another user is.
    Given the feature is 2
    And the database contains the user "hjones" with password "hjones"
    And I attempt to perform as administrator with password "adminpwd"
    When I remove the user "jsmith"
    Then I should be told "jsmith does not exist."
    And the database should not contain the user "jsmith"
    And the database should contain the user "hjones" with password "hjones"

  Scenario: The user to be removed is in the database alone.
    Given the feature is 2
    And the database contains the user "jsmith" with password "jsmith"
    And I attempt to perform as administrator with password "adminpwd"
    When I remove the user "jsmith"
    Then I should be told "jsmith was removed."
    And the database should not contain the user "jsmith"

  Scenario: The user to be removed is in a populated database.
    Given the feature is 2
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the user "hjones" with password "hjones"
    And I attempt to perform as administrator with password "adminpwd"
    When I remove the user "jsmith"
    Then I should be told "jsmith was removed."
    And the database should not contain the user "jsmith"
    And the database should contain the user "hjones" with password "hjones"
