Feature: Allow users to send messages to and receive messages from other users.
  
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
  6. send message
  7. check messages

  Choosing options 1--5 work as described in the previous feature.
  If the user chooses option 6, the program first verifies that the user has a
  wallet. (This is because messages are sent about products to be purchased, and
  only users with wallets can purchase things. It also maintains anonymity on
  both sides of the conversation.) If the user has no wallet, then the program
  outputs "User has no wallet." and returns to the user menu.
  
  Otherwise, option 6 displays the products that are not being sold by the user
  as a numbered list as in the previous feature, and that numbered list should
  be part of a prompt asking the user which product they want to communicate
  about. As before, the numbered list should start with "cancel" as item number
  1, which aborts the command and prints "Action canceled.", returning to the
  user menu. Choosing any of the other numbered options displays a prompt for
  the message (a nonempty string). After the user enters the message, the
  program responds with "Message sent." and returns to the user menu.
  
  The following is an example of the menu progression of sending a message:
  
  1: cancel
  2: chalk: taken from a classroom  [2 WoCoins]
  3: apple: small  [3 WoCoins]
  4: paper: a ream for a printer  [4 WoCoins]
  5: trip to Charlotte: no questions asked  [4 WoCoins]
  Which product is the subject of the message?  3
  What is the message?  is it a red apple?
  Message sent.
  
  

  Choosing option 7 displays, as part of a prompt, a numbered list of all
  existing messages that have been sent to the user, along with the product that
  is the subject of the message, and the date and time that the message was
  sent. This list should be in ascending order by date/time, with the newest
  message at the top. The prompt should ask which message the user wants to
  interact with. As before, the numbered list should start with "cancel" as
  option 1, which aborts the command, prints "Action canceled.", and returns to
  the user menu. Any other choice brings a new prompt of numbered options for
  the action to take. Once again, the first option is "cancel" (which behaves
  as described above, returning to the user menu), and the other options are
  "reply" and "delete". If "reply" is chosen, the program prompts for the
  message (a nonempty string). After the user enters the message, the program
  responds with "Message sent." and returns to the user menu. If "delete" is
  chosen, the message is deleted from the user's inbox and the program outputs
  "Message deleted." and returns to the user menu.
  
  The following is an example of the menu progression of displaying messages:
  
  1: cancel
  2: is it a red apple?  [apple]  2019-08-11 16:06:04 
  3: day trip or overnight?  [trip to Charlotte]  2019-08-15 09:42:35 
  Which message?  2
  1: cancel
  2: reply
  3: delete
  What do you want to do?  2
  What is the message?  no it's green
  Message sent.
  

  Scenario: A nonexistent user wants to send a message.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I send a message
    Then I should be told "No such user."

  Scenario: A user with no wallet wants to send a message.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I send a message
    Then I should be told "User has no wallet."

  Scenario: A user wants to send a message and cancels.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I send a message
    And I choose to cancel
    Then I should be told "1: cancel"
    And I should be told "2: coffee: a drink  [2 WoCoins]"
    And I should be told "3: bicycle: nothing fancy  [3 WoCoins]"
    And I should be told "Action canceled."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database should contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19" 

  Scenario: A user wants to send a message.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "paper" with description "one sheet" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 3 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I send a message
    And I choose product 4
    And I provide the message "i want it"
    Then I should be told "1: cancel"
    And I should be told "2: coffee: a drink  [2 WoCoins]"
    And I should be told "3: paper: one sheet  [2 WoCoins]"
    And I should be told "4: bicycle: nothing fancy  [3 WoCoins]"
    And I should be told "Message sent."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-12-03 09:17:02"
    And the database should contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 3 on "2019-11-25 18:42:19" 
    And the database should contain the message "i want it" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "now" 

  Scenario: A user wants to send a message but chooses an invalid product.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "paper" with description "one sheet" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 3 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I send a message
    And I choose product 5
    And I choose to cancel
    Then I should be told "1: cancel"
    And I should be told "2: coffee: a drink  [2 WoCoins]"
    And I should be told "3: paper: one sheet  [2 WoCoins]"
    And I should be told "4: bicycle: nothing fancy  [3 WoCoins]"
    And I should be told "Invalid value. Enter a value between 1 and 4."
    And I should be told "Action canceled."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-12-03 09:17:02"
    And the database should contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 3 on "2019-11-25 18:42:19" 

  Scenario: A nonexistent user wants to display their messages.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains a product "bicycle" with description "nothing fancy" and price 1 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And I attempt to perform as a user with username "jdoe" and password "jdoe"
    When I check my messages
    Then I should be told "No such user."

  Scenario: A user wants to display their messages without replying or deleting.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I check my messages
    And I choose to cancel
    Then I should be told "1: cancel"
    And I should be told "2: can i get it iced?  [coffee]  2019-11-25 18:42:19"
    And I should be told "3: what color?  [bicycle]  2019-12-03 09:17:02"
    And I should be told "Action canceled."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database should contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19" 

  Scenario: A user wants to display their messages and reply.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I check my messages
    And I choose to reply to message 3
    And I provide the message "blue"
    Then I should be told "1: cancel"
    And I should be told "2: can i get it iced?  [coffee]  2019-11-25 18:42:19"
    And I should be told "3: what color?  [bicycle]  2019-12-03 09:17:02"
    And I should be told "Message sent."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database should contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19" 
    And the database should contain the message "blue" sent from "a615316333ba8622fd5bb60fe39758b3515f774d" to "587888ea2b080656816aad7e0bc8f1cf3cf0bced" about product 1 on "now" 

  Scenario: A user wants to display their messages and reply to an invalid message.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I check my messages
    And I choose to reply to message 4
    And I choose to cancel
    Then I should be told "1: cancel"
    And I should be told "2: can i get it iced?  [coffee]  2019-11-25 18:42:19"
    And I should be told "3: what color?  [bicycle]  2019-12-03 09:17:02"
    And I should be told "Invalid value. Enter a value between 1 and 3."
    And I should be told "Action canceled."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database should contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19" 

  Scenario: A user wants to display their messages and delete.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I check my messages
    And I choose to delete message 2
    Then I should be told "1: cancel"
    And I should be told "2: can i get it iced?  [coffee]  2019-11-25 18:42:19"
    And I should be told "3: what color?  [bicycle]  2019-12-03 09:17:02"
    And I should be told "Message deleted."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database should not contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19" 

  Scenario: A user wants to display their messages and delete an invalid message.
    Given the feature is 7
    And the database contains the user "jsmith" with password "jsmith"
    And the database contains the wallet "jsmith" with public key "a615316333ba8622fd5bb60fe39758b3515f774d"
    And the database contains the user "jdoe" with password "jdoe"
    And the database contains the wallet "jdoe" with public key "587888ea2b080656816aad7e0bc8f1cf3cf0bced"
    And the database contains the user "hjones" with password "hjones"
    And the database contains the wallet "hjones" with public key "e9d572572eaed1550f57bd41eec8105ad1f082b1"
    And the database contains a product "bicycle" with description "nothing fancy" and price 3 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a product "coffee" with description "a drink" and price 2 added by "a615316333ba8622fd5bb60fe39758b3515f774d" 
    And the database contains a message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database contains a message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19"
    And I attempt to perform as a user with username "jsmith" and password "jsmith"
    When I check my messages
    And I choose to delete message 4
    And I choose to cancel
    Then I should be told "1: cancel"
    And I should be told "2: can i get it iced?  [coffee]  2019-11-25 18:42:19"
    And I should be told "3: what color?  [bicycle]  2019-12-03 09:17:02"
    And I should be told "Invalid value. Enter a value between 1 and 3."
    And I should be told "Action canceled."
    And the database should contain the message "what color?" sent from "587888ea2b080656816aad7e0bc8f1cf3cf0bced" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 1 on "2019-12-03 09:17:02"
    And the database should contain the message "can i get it iced?" sent from "e9d572572eaed1550f57bd41eec8105ad1f082b1" to "a615316333ba8622fd5bb60fe39758b3515f774d" about product 2 on "2019-11-25 18:42:19" 

