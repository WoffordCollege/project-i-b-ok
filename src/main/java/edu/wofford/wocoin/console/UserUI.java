package edu.wofford.wocoin.console;

import edu.wofford.wocoin.ConsoleController;
import edu.wofford.wocoin.Message;
import edu.wofford.wocoin.Product;
import edu.wofford.wocoin.WalletUtilities;
import io.bretty.console.view.AbstractView;
import io.bretty.console.view.ViewConfig;

import java.util.ArrayList;
import java.util.Scanner;

public class UserUI extends CustomActionView {
    private ConsoleController cc;

    public UserUI(ConsoleController cc, ViewConfig viewConfig) {
        super("", "user", viewConfig);
        this.cc = cc;
    }

    public UserUI(ConsoleController cc, ViewConfig viewConfig, Scanner keyboard) {
        super("", "user", viewConfig, keyboard);
        this.cc = cc;
    }

    @Override
    public void executeCustomAction() {
        String username = this.prompt("Please enter your username: ", String.class);
        String password = this.prompt("Please enter your password: ", String.class);

        if (!cc.userLogin(username, password)){
            this.println("No such user.");
            this.goBack();
        }
        else {
            new UserRootMenu(this.parentView, username, viewConfig, this.keyboard).display();
        }
    }

    private class UserRootMenu extends CustomMenuView {

        public UserRootMenu(AbstractView parentView, String user, ViewConfig viewConfig, Scanner keyboard) {
            super("Welcome back, " + user, "", viewConfig, keyboard);

            this.parentView = parentView;

            this.addMenuItem(new CreateWalletAction(viewConfig, keyboard));
            this.addMenuItem(new CreateProductAction(viewConfig, keyboard));
            this.addMenuItem(new RemoveProductAction(viewConfig, keyboard));
            this.addMenuItem(new DisplayProductsAction(viewConfig, keyboard));
            this.addMenuItem(new SendMessageAction(viewConfig, keyboard));
            this.addMenuItem(new GetMessagesAction(viewConfig, keyboard));
            this.addMenuItem(new GetBalanceAction(viewConfig, keyboard));
            this.addMenuItem(new BuyProductAction(viewConfig, keyboard));
        }


        @Override
        protected void onBack() {
            cc.doLogout();
            super.onBack();
        }

        private class CreateWalletAction extends CustomActionView {

            public CreateWalletAction(ViewConfig viewConfig, Scanner keyboard) {
                super("Create a Wallet", "create wallet", viewConfig, keyboard);
            }

            @Override
            public void executeCustomAction() {
                boolean userStillCreatingWallet = true;

                if (cc.userHasWallet()) {
                    this.println("Your account has an associated wallet.");
                    userStillCreatingWallet = this.confirmDialog("Would you like to delete this wallet and create a new wallet?");

                    if (userStillCreatingWallet) {
                        this.println("Wallet deleted.");
                    }
                }

                if (userStillCreatingWallet) {
                    String path = this.prompt("Enter the file path for the wallet: ", String.class);

                    WalletUtilities.CreateWalletResult result = cc.addWalletToUser(path);

                    this.println(result == WalletUtilities.CreateWalletResult.SUCCESS ? "Wallet added." : "Action Canceled");

                }
            }
        }

        private class CreateProductAction extends CustomActionView {

            public CreateProductAction(ViewConfig viewConfig, Scanner keyboard) {
                super("Add a Product", "add product", viewConfig, keyboard);
            }

            @Override
            public void executeCustomAction() {
                String name = this.prompt("Enter the product name: ", String.class);
                String description = this.prompt("Enter the product description: ", String.class);
                int price = this.prompt("Enter the product price: ", Integer.class);
                this.println(cc.addNewProduct(name, description, price));
            }
        }

        private class RemoveProductAction extends CustomActionView {

            public RemoveProductAction(ViewConfig viewConfig, Scanner keyboard) {
                super("Select a Product to Remove", "remove product", viewConfig, keyboard);

                this.viewConfig = new ViewConfig.Builder()
                                                .setBackMenuName("cancel")
                                                .setIndexNumberFormatter(index -> (index + 1) + ": ")
                                                .build();
            }

            @Override
            public void executeCustomAction() {
                ArrayList<Product> products = cc.getUserProducts();
                products.sort(Product::compareTo);

                this.println("1: cancel");
                for (int i = 0; i < products.size(); i++) {
                    this.println(String.format("%d: %s", i + 2, products.get(i).toString()));
                }
                int selected = this.prompt("Please select item to remove: ", Integer.class);

                if (selected == 1) {
                    this.println("Action canceled.");
                }
                else if (!cc.userHasWallet()) {
                    this.println("User has no wallet.");
                }
                else if (selected < 1 || selected - 1 > products.size()) {
                    this.println(String.format("Invalid value. Enter a value between 1 and %d.", products.size() + 1));
                }
                else {
                    this.println(cc.removeProduct(products.get(selected - 2)));
                }
                this.goBack();
            }
        }

        private class DisplayProductsAction extends CustomActionView {

            public DisplayProductsAction(ViewConfig viewConfig, Scanner keyboard) {
                super("Products for Sale", "display products", viewConfig, keyboard);
            }

            @Override
            public void executeCustomAction() {
                ArrayList<Product> products = cc.getSqlController().getAllProductsList();
                products.sort(Product::compareToWithPrice);
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    product.setCurrentUser(cc.getCurrentUser());
                    product.setDisplayType(Product.DisplayType.SHOWCURRENTUSER);
                    this.println(String.format("%d: %s", i + 1, product.toString()));
                }
                this.goBack();
            }
        }

        private class SendMessageAction extends CustomActionView {
            public SendMessageAction(ViewConfig viewConfig, Scanner keyboard) {
                super("Pick a Product to send a message to its seller", "send message", viewConfig, keyboard);
	            this.viewConfig = new ViewConfig.Builder()
									            .setBackMenuName("cancel")
									            .setIndexNumberFormatter(index -> (index + 1) + ": ")
									            .build();
            }

            @Override
            public void executeCustomAction() {
            	if (!cc.userHasWallet()) {
            		this.println("User has no wallet.");
	            }
            	else {
		            ArrayList<Product> products = cc.getPurchasableProducts(false);
		            products.sort(Product::compareToWithPrice);

		            this.println("1: cancel");
		            for (int i = 0; i < products.size(); i++) {
			            this.println(String.format("%d: %s", i + 2, products.get(i).toString()));
		            }

		            int selected = this.prompt("Which product number is the subject of the message? ", Integer.class);

		            if (selected == 1) {
			            this.println("Action canceled.");
		            } else if (!cc.userHasWallet()) {
			            this.println("User has no wallet.");
		            } else if (selected < 1 || selected - 1 > products.size()) {
			            this.println(String.format("Invalid value. Enter a value between 1 and %d.", products.size() + 1));
			            this.println("Action canceled.");
		            } else {
			            String userMessage = this.prompt("What is the message? ", String.class);
			            this.println(cc.sendMessage(products.get(selected - 2), userMessage));
		            }
	            }
            }
        }

        private class GetMessagesAction extends CustomActionView {
	        public GetMessagesAction(ViewConfig viewConfig, Scanner keyboard) {
	        	super("Pick a message to reply to or delete.", "check messages", viewConfig, keyboard);
	        }

	        @Override
	        public void executeCustomAction() {
				ArrayList<Message> messages = cc.getUserMessages();

		        this.println("1: cancel");
		        for (int i = 0; i < messages.size(); i++) {
			        this.println(String.format("%d: %s", i + 2, messages.get(i).toString()));
		        }

		        int selected = 0;
		        boolean validInput = false;

		        while (!validInput) {
			        selected = this.prompt("Which message? ", Integer.class);
			        validInput = selected >= 1 && selected - 1 <= messages.size();
			        if (!validInput){
				        this.println(String.format("Invalid value. Enter a value between 1 and %d.", messages.size() + 1));
			        }
		        }

		        if (selected == 1) {
			        this.println("Action canceled.");
		        }
		        else if (!cc.userHasWallet()) {
			        this.println("User has no wallet.");
		        }
		        else {
			        this.println("1: cancel");
			        this.println("2: reply");
			        this.println("3: delete");
			        int messageOption = this.prompt("What do you want to do? ", Integer.class);
			        if (messageOption == 1) {
			        	this.println("Action canceled.");
			        }
			        else if (messageOption == 2) {
			        	String reply = this.prompt("What would you like to reply? ", String.class);
						this.println(cc.replyToMessage(messages.get(selected - 2), reply));
			        }
			        else if (messageOption == 3) {
						this.println(cc.deleteMessage(messages.get(selected - 2)));
			        }
		        }
		        this.goBack();
	        }
        }

        private class GetBalanceAction extends CustomActionView {

	        public GetBalanceAction(ViewConfig viewConfig, Scanner keyboard) {
		        super("Current WoCoin Balance", "check balance", viewConfig, keyboard);
	        }

	        @Override
	        public void executeCustomAction() {
				this.println(cc.getUserBalance());
	        }
        }

        private class BuyProductAction extends CustomActionView {

	        public BuyProductAction(ViewConfig viewConfig, Scanner keyboard) {
		        super("Pick a product to buy", "purchase product", viewConfig, keyboard);
	        }

	        @Override
	        public void executeCustomAction() {
	        	if (cc.userHasWallet()) {
	        		String walletPath = this.prompt("What is the directory to your wallet? ", String.class);
					if (!cc.walletInDBMatchesGivenPath(walletPath)) {
							this.println("Invalid wallet.");
					}
					else {
						ArrayList<Product> products = cc.getPurchasableProducts(true);
						products.sort(Product::compareToWithPrice);

						this.println("1: cancel");
						for (int i = 0; i < products.size(); i++) {
							this.println(String.format("%d: %s", i + 2, products.get(i).toString()));
						}

						int selected = this.prompt("Which product would you like to buy? ", Integer.class);

						if (selected == 1) {
							this.println("Action canceled.");
						} else if (selected < 1 || selected - 1 > products.size()) {
							this.println(String.format("Invalid value. Enter a value between 1 and %d.", products.size() + 1));
							this.println("Action canceled.");
						} else {
							this.println(cc.buyProduct(walletPath, products.get(selected - 2)));
						}
					}
		        }
	        	else {
			        this.println("User has no wallet.");
		        }
	        }
        }
    }
}
