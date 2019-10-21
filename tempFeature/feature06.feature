Feature: Allow users to display products in the database.
  
  The user should be able to display products in the database.
  The program should take a single command-line argument for the database file.
  It should present the root menu as in the previous feature:
  
  1. exit
  2. administrator
  3. user
  
  Option 2 should work exactly as in the previous feature. Option 3 should
  present a valid user with a menu of numbered options as follows:
  
  1. back
  2. create wallet
  3. add product
  4. remove product
  5. display products

  Choosing options 1--4 work as described in the previous feature. Choosing
  option 5 displays a list of all products currently available in the database.
  The products should be numbered and sorted in ascending order by price and,
  within the same price, alphabetically by title (case insensitive). Any
  products that were added by the current user should be prepended with ">>>".
  After displaying the products, the program should return to the user menu.
  For instance, an example of the products displayed is as follows:

  1: >>>  skittles: a half-eaten bag  [1 WoCoin]
  2: chalk: taken from a classroom  [2 WoCoins]
  3: >>>  Zombieland: DVD  [2 WoCoins]
  4: apple: small  [3 WoCoins]
  5: paper: a ream for a printer  [4 WoCoins]
  6: >>>  Risk: board game  [4 WoCoins]
  7: trip to Charlotte: no questions asked  [4 WoCoins]


  Scenario: A nonexistent user wants to display the products.
    Given the feature is 6
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I display products
    Then I should be told "No such user."
  
  Scenario: A user with no added products wants to display the products.
    Given the feature is 6
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains a product "skittles" with description "a half-eaten bag" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "Zombieland" with description "DVD" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "Risk" with description "board game" and price 4 added by "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "paper" with description "a ream for a printer" and price 4 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "chalk" with description "taken from a classroom" and price 2 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "apple" with description "small" and price 3 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "trip to Charlotte" with description "no questions asked" and price 4 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And I attempt to perform as a user with username "hjones" and password "hjones"
    When I display products
    Then I should be told "1: skittles: a half-eaten bag  [1 WoCoin]"
    And I should be told "2: chalk: taken from a classroom  [2 WoCoins]"
    And I should be told "3: Zombieland: DVD  [2 WoCoins]"
    And I should be told "4: apple: small  [3 WoCoins]"
    And I should be told "5: paper: a ream for a printer  [4 WoCoins]"
    And I should be told "6: Risk: board game  [4 WoCoins]"
    And I should be told "7: trip to Charlotte: no questions asked  [4 WoCoins]"

  Scenario: A user with three added products wants to display the products.
    Given the feature is 6
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains a product "skittles" with description "a half-eaten bag" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "Zombieland" with description "DVD" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "Risk" with description "board game" and price 4 added by "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "paper" with description "a ream for a printer" and price 4 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "chalk" with description "taken from a classroom" and price 2 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "apple" with description "small" and price 3 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "trip to Charlotte" with description "no questions asked" and price 4 added by "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I display products
    Then I should be told "1: >>>  skittles: a half-eaten bag  [1 WoCoin]"
    And I should be told "2: chalk: taken from a classroom  [2 WoCoins]"
    And I should be told "3: >>>  Zombieland: DVD  [2 WoCoins]"
    And I should be told "4: apple: small  [3 WoCoins]"
    And I should be told "5: paper: a ream for a printer  [4 WoCoins]"
    And I should be told "6: >>>  Risk: board game  [4 WoCoins]"
    And I should be told "7: trip to Charlotte: no questions asked  [4 WoCoins]"

