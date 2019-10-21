Feature: Allow users to add products to the database.
  
  The user should be able to add products to the database as long as the user
  is valid with an active wallet. The program should take a single command-line
  argument for the database file. It should present the root menu as in the
  previous feature:
  
  1: exit
  2: administrator
  3: user
  
  Option 2 should work exactly as in the previous feature. As before, option 3
  should present a valid user with a menu of numbered options as follows:
  
  1: back
  2: create wallet
  3: add product

  Choosing options 1 and 2 work as described in the previous feature. If
  option 3 is chosen, the program should check to see if the user has an active
  wallet. If not, the program should respond with "User has no wallet." 
  Otherwise, the program should prompt for the name (a nonempty string),
  description (a nonempty string), and price (a positive integer) of the
  product. That product should be added to the database along with the
  user's wallet's public key, and the program should print the message
  "Product added."


  Scenario: A nonexistent user attempts to add a product.
    Given the feature is 4
    And the database is empty
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    Then I should be told "No such user."

  Scenario: A user with no wallet attempts to add a product.
    Given the feature is 4
    And the database contains the user "jsmith" with password "jsmith"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I add a product named "my title" with description "my description" and price 3
    Then I should be told "User has no wallet."
    And the database should contain the product "my name" with description "my description" and price 3 added by "whatever" 0 times

  Scenario: A valid user with a wallet attempts to add a product.
    Given the feature is 4
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I add a product named "my name" with description "my description" and price 3
    Then I should be told "Product added."
    And the database should contain the product "my name" with description "my description" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 1 times

  Scenario: A valid user with a wallet attempts to add a product with an empty name.
    Given the feature is 4
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I add a product named "" with description "my description" and price 3
    Then I should be told "Invalid value."
    And I should be told "Expected a string with at least 1 character."

  Scenario: A valid user with a wallet attempts to add a product with an empty description.
    Given the feature is 4
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I add a product named "my name" with description "" and price 3
    Then I should be told "Invalid value."
    And I should be told "Expected a string with at least 1 character."

  Scenario: A valid user with a wallet attempts to add a product with a non-positive price.
    Given the feature is 4
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I add a product named "my name" with description "my description" and price 0
    Then I should be told "Invalid value."
    And I should be told "Expected an integer value greater than or equal to 1."



