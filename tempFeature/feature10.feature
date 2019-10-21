Feature: Allow users to purchase products with their WoCoins.
  
  The user should be able to purchase products in the database with their WoCoins.
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
  6. send message
  7. check messages
  8. check balance
  9. purchase product

  Choosing options 1--8 work as described in the previous feature. If option 9
  is chosen, the program should check to see if the user has an active
  wallet. If not, the program should respond with "User has no wallet."
  Otherwise, the program should prompt the user for the root wallet directory,
  with the default being the system's user home directory. If a valid wallet
  file (in subdirectory of the username) in that root does not exist, the
  program should output "Wallet does not exist." and return to the user menu.
  Finally, if the wallet file does exist in that subdirectory, but the public
  key (address) does not match that stored in the database, the program should
  output "Invalid wallet."
  
  If the user has a valid wallet file, the program should determine the user's
  current WoCoin balance and then display a list of all currently available
  products that were not added by the user (as the seller) and whose price is
  less than or equal to the user's balance. The numbered list of those items
  should be a part of a prompt for the item to be purchased. The numbered list
  of items should always start with item number 1 called "cancel", which simply
  outputs "Action canceled." and returns to the user menu if chosen.
  
  The remaining items should be numbered from 2 on, sorted in ascending order by
  price and, within the same price, alphabetically by title (case insensitive).
  If the user chooses an actual item (2 or above) in this list, the program
  should then submit a transaction from the user (using their wallet file) to
  the item's seller for the price of the item and remove the item from the
  purchasable products. The program should output "Item purchased."


  Scenario: A nonexistent purchases a product.
    Given the feature is 10
    And the database contains the user "jsmith" with password "jsmith"
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I purchase a product
    Then I should be told "No such user."

  Scenario: A user with no wallet purchases a product.
    Given the feature is 10
    And the database contains the user "jsmith" with password "jsmith"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I purchase a product
    Then I should be told "User has no wallet."

  Scenario: A user with a wallet file that does not match the database purchases a product.
    Given the feature is 10
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "someotherpublickey"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I purchase a product
    And I use the wallet home directory "./samples"
    Then I should be told "Invalid wallet."

  @rungeth
  Scenario: A user with a valid wallet file cancels the purchase of a product.
    Given the feature is 10
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I purchase a product
    And I use the wallet home directory "./samples"
    And I choose to cancel
    Then I should be told "Action canceled."
    
  @rungeth
  Scenario: A user with enough coins purchases a product.
    Given the feature is 10
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "A Wrinkle in Time" with description "a book" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "Time magazine" with description "issue 42" and price 7 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    # jsmith has 7 WoCoins from feature 8
    And the user "jdoe" has 2 WoCoins using administrator password "adminpwd"
    # jdoe now has 3 WoCoins because 1 was added to escape the invalid scenario from feature 8
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I purchase a product
    And I use the wallet home directory "./samples"
    And I choose product 3
    Then I should be told "1: cancel"
    And I should be told "2: bicycle: nothing fancy  [1 WoCoin]"
    And I should be told "3: A Wrinkle in Time: a book  [3 WoCoins]"
    And I should not be told "4: Time magazine: issue 42  [7 WoCoins]"
    And I should be told "Item purchased."
    And the wallet "a615316333ba8622fd5bb60fe39758b3515f774d" should contain 10 WoCoins
    And the wallet "587888ea2b080656816aad7e0bc8f1cf3cf0bced" should contain 0 WoCoins

