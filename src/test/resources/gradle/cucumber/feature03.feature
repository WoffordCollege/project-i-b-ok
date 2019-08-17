Feature: Allow users to generate a WoCoin wallet.
  
  A user should be able to create a WoCoin wallet.
  The program should take a single command-line argument for the databse file.
  It should then present a menu with the following options:
  
  1: exit
  2: administrator
  3: user
  
  If option 2 is chosen, the program should prompt for the administrator
  password ("adminpwd" in this case) and, if the correct password is given,
  show the menu from the previous feature with the same functionality.
  
  If option 3 is chosen, the program should prompt for the username and password
  of the user. If that user does not exist in the database, or if the password
  is incorrect, the program should output "No such user." and return to the root
  menu. Otherwise, if the user's credentials are correct, the program should
  provide the following menu:
  
  1: back
  2: create wallet
  
  If option 1 is chosen, the program should show the root menu again.
  If option 2 is chosen, the program should check to see if the user already has
  a wallet in the database. If so, the program should prompt the user for
  confirmation ("y" or "n") that the previous wallet will be discarded and
  replaced with the soon-to-be-created one (default to "n"). If the user
  declines the confirmation, the program should output "Action canceled." and
  present the user menu again.
  
  Otherwise, the program should prompt for the root wallet directory, with the
  default of the system's user home directory. Into this directory, the program
  will create a subdirectory named the same as the username, and in that
  subdirectory it will create a WoCoin wallet file (a JSON file) with the
  public/private key information. Finally, the program will read that newly
  created file to get the public key (the "address"), add the username and
  public key to the wallets table of the database, and print the message
  "Wallet added." 
  
  
  Instructor's Note
  -----------------
  You will want to use the web3j library for this feature. You will need to find
  the documentation and learn how to create a wallet programmatically in Java.
  You will also do the same for the json-simple library in order to read the
  generated wallet file. The web3j and json-simple dependencies are already a
  part of the Gradle build file, so any proper import statements should work.
  Also, I found that the web3j library was fairly resource intensive, so I had
  to increase Java's memory allotment. You can do this in several ways, but on
  a Unix system, the easiest way is to specify it on the command line:
  
  JAVA_TOOL_OPTIONS="-Xmx512m" ./gradlew --no-daemon cucumber
  
  This will set the maximum RAM to 512 MB.
  
  
  
  Scenario: Nonexistent user requests to create a wallet.
    Given the feature is 3
    And the database is empty
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I create a wallet
    Then I should be told "No such user."

  Scenario: The user requests to create a wallet.
    Given the feature is 3
    And the database contains the user "jsmith" with password "jsmith"
    And the directory "tmp" exists
    And the directory "tmp/jsmith" exists
    And the directory "tmp/jsmith" is empty
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I create a wallet
    And I use the directory "tmp"
    Then I should be told "Wallet added."
    And a file exists in the directory "tmp/jsmith" with extension "json"
    And the database should contain a wallet with username "jsmith" and a nonempty public key

  Scenario: The user requests to create a second wallet.
    Given the feature is 3
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the directory "tmp" exists
    And the directory "tmp/jsmith" exists
    And the directory "tmp/jsmith" contains the file "mykeyfile.json"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I create a wallet
    And I accept the default option
    And I use the directory "tmp"
    Then I should be told "Action canceled."
    And a file exists in the directory "tmp/jsmith" with name "mykeyfile.json"
    And the database should contain a wallet with username "jsmith" and a nonempty public key


