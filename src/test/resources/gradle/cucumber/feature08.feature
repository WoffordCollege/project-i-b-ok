Feature: Allow administrators to transfer WoCoins to a user's wallet.
  
  The administrator should be able to transfer WoCoins to a user's wallet.
  The program should take a single command-line argument for the database file.
  It should present the root menu as in the previous feature:
  
  1. exit
  2. administrator
  3. user
  
  Option 3 should work exactly as in the previous feature. Option 2 should
  present the administrator with a menu of numbered options as follows:
  
  1. back
  2. add user
  3. remove user
  4. transfer WoCoins

  Choosing options 1--3 work as described in the previous feature. Choosing
  option 4 prompts the administrator for the user's username. If the username
  does not exist as a user in the database, the program should output
  "No such user." If the user exists but does not have a public key entry in
  the database, the program should output "User has no wallet." Otherwise, the
  program should prompt the administrator for the number of WoCoins to transfer
  to the user (a positive integer), and that number of WoCoins should be added
  to the user's wallet through the blockchain. The program should then output
  "Transfer complete." In all cases, the program should return to the
  administrator menu.
  
  
  Instructor's Note
  -----------------
  You will now have to set up and deploy your own private Ethereum network. It
  should use Proof-of-Authority as its consensus protocol. Use your web research
  skills to solve this technical challenge. To get you started, I will tell you
  that you will almost certainly want to install geth. I have also included an
  "ethereum" directory in the repository that contains some useful files.
  
  Also, when you run the cucumber tests, you will need to make sure that the
  Ethereum node is running (and has started afresh) before the scenarios tagged
  as @rungeth run. You will need to develop a way to reset and then start the
  node, ideally as a bash script or batch file. Assuming that you run such a
  script to reset/restart the node, you would run the cucumber tests separately
  as follows:
  
  JAVA_TOOL_OPTIONS="-Xmx512m" CUCUMBER_OPTIONS='--tags "not @rungeth"' ./gradlew --no-daemon cucumber
  
  This command runs all the non-Ethereum-required acceptance tests. After that,
  you would start the Ethereum node (in a different terminal) by running the
  script you have created, and then you would run the cucumber test for the geth
  scenarios:
  
  JAVA_TOOL_OPTIONS="-Xmx512m" CUCUMBER_OPTIONS='--tags "@rungeth"' ./gradlew --no-daemon cucumber
  


  Scenario: The administrator transfers funds to a nonexistent user.
    Given the feature is 8
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as administrator with password "adminpwd"
    When I transfer 7 WoCoins to "hjones"
    Then I should be told "No such user."
  
  Scenario: The administrator transfers funds to a user without a wallet.
    Given the feature is 8
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And I attempt to perform as administrator with password "adminpwd"
    When I transfer 7 WoCoins to "jdoe"
    Then I should be told "User has no wallet."

  @rungeth
  Scenario: The administrator transfers non-positive funds to a legitimate user.
    Given the feature is 8
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And I attempt to perform as administrator with password "adminpwd"
    When I transfer 0 WoCoins to "jdoe"
    Then I should be told "Invalid value."
    And I should be told "Expected an integer value greater than or equal to 1."
  
  @rungeth
  Scenario: The administrator transfers funds to a legitimate user.
    Given the feature is 8
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And I attempt to perform as administrator with password "adminpwd"
    When I transfer 7 WoCoins to "jsmith"
    Then I should be told "Transfer complete."
    And the wallet "a615316333ba8622fd5bb60fe39758b3515f774d" should contain 7 WoCoins


