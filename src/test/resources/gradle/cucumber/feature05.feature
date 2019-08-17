Feature: Allow users to remove products that they have added to the database.
  
  The user should be able to remove products from the database as long as that
  user is the one that added the product. The program should take a single
  command-line argument for the database file. It should present the root menu
  as in the previous feature:
  
  1: exit
  2: administrator
  3: user
  
  Option 2 should work exactly as in the previous feature. Option 3 should
  present a valid user with a menu of numbered options as follows:
  
  1: back
  2: create wallet
  3: add product
  4: remove product

  Choosing options 1--3 work as described in the previous feature.  If
  option 4 is chosen, the program should check to see if the user has an active
  wallet. If not, the program should respond with "User has no wallet."
  Otherwise, the program should use the stored public key to find all products
  added by the user. The numbered list of those items should be a part of a
  prompt for the item to be removed. The numbered list of items should always
  start with item number 1 called "cancel", which, if chosen, outputs
  "Action canceled." and returns to the user menu. The remaining items should be
  numbered from 2 on, sorted alphabetically by name (case insensitive).
  For instance:
  
  1: cancel
  2: A Wrinkle in Time: a book  [3 WoCoins]
  3: bicycle: nothing fancy  [1 WoCoin]
  4: Time magazine: issue 42  [7 WoCoins]
  
  If the user chooses an actual item (2 or above) in this list, that item is
  removed from the database, the program outputs "Product removed.", and the
  program returns to the user menu.
  

  Scenario: A nonexistent user wants to remove a product.
    Given the feature is 5
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I remove product 2
    Then I should be told "No such user."

  Scenario: A user with no wallet wants to remove a product.
    Given the feature is 5
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I remove product 2
    Then I should be told "User has no wallet."

  Scenario: A user with no added products wants to remove a product.
    Given the feature is 5
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I remove product 2
    Then I should be told "Invalid value. Enter a value between 1 and 1."

  Scenario: A user wants to cancel attempting to remove a product.
    Given the feature is 5
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I remove product 1
    Then I should be told "Action canceled."
  
  Scenario: A user wants to remove a product they added.
    Given the feature is 5
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I remove product 3
    Then I should be told "1: cancel"
    And I should be told "2: A Wrinkle in Time: a book  [3 WoCoins]"
    And I should be told "3: bicycle: nothing fancy  [1 WoCoin]"
    And I should be told "4: Time magazine: issue 42  [7 WoCoins]"
    And I should be told "Product removed."
    And the database should contain the product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 0 times

  Scenario: A user wants to remove one copy of a product they added three times.
    Given the feature is 5
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I remove product 2
    Then I should be told "1: cancel"
    And I should be told "2: A Wrinkle in Time: a book  [3 WoCoins]"
    And I should be told "3: A Wrinkle in Time: a book  [3 WoCoins]"
    And I should be told "4: A Wrinkle in Time: a book  [3 WoCoins]"
    And I should be told "5: bicycle: nothing fancy  [1 WoCoin]"
    And I should be told "6: Time magazine: issue 42  [7 WoCoins]"
    And I should be told "Product removed."
    And the database should contain the product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 2 times

  Scenario: A user wants to remove a product that does not exist.
    Given the feature is 5
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I remove nonexistent product 5
    Then I should be told "Invalid value. Enter a value between 1 and 4."

