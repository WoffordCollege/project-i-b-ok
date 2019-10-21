Feature: Allow users to check their WoCoin balance.
  
  The user should be able to check their WoCoin balance.
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

  Choosing options 1--7 work as described in the previous feature. If option
  8 is chosen, the program should check to see if the user has an active
  wallet. If not, the program should respond with "User has no wallet."
  Otherwise, the program should determine the user's current WoCoin balance and
  then display that balance in the following form: "User has X WoCoins." where
  X is the balance. The program should then return to the user menu.


  Instructor's Note
  -----------------
  As in the previous feature, the Ethereum node must be running on a new
  blockchain in order to run and pass the scenario tagged with @rungeth. To run
  this scenario (and the other geth scenarios), you would start the Ethereum
  node in a different terminal (using your script) and then issue the following
  command:
  
  JAVA_TOOL_OPTIONS="-Xmx512m" CUCUMBER_OPTIONS='--tags "@rungeth"' ./gradlew --no-daemon cucumber



  Scenario: A nonexistent user checks their balance.
    Given the feature is 9
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I check my balance
    Then I should be told "No such user."

  Scenario: A user with no wallet checks their balance.
    Given the feature is 9
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I check my balance
    Then I should be told "User has no wallet."

  @rungeth
  Scenario: A user with a single coin checks their balance.
    Given the feature is 9
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the user "hjones" has 1 WoCoins using administrator password "adminpwd"
    And I attempt to perform as a user with username "hjones" and password "hjones"
    When I check my balance
    Then I should be told "User has 1 WoCoin."

  @rungeth
  Scenario: A user with multiple coins checks their balance.
    Given the feature is 9
    And the database contains the user "srogers" with password "srogers"
    And the database contains the wallet "srogers" with public key "fab258997f9b8f33892e111515b21164205ae02a"
    And the user "srogers" has 3 WoCoins using administrator password "adminpwd"
    And I attempt to perform as a user with username "srogers" and password "srogers"
    When I check my balance
    Then I should be told "User has 3 WoCoins."

