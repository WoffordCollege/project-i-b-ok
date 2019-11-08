package edu.wofford.wocoin;

/**
 * This class models the form of a product in the Wocoin database.
 */
public class Product implements Comparable<Product>{
    private String seller;
    private int price;
    private String name;
    private String description;
    private String currentUser;
    private boolean ownedByUser;


    public Product(String seller, int price, String name, String description) {
        this(seller, price, name, description, null);
    }

    /**
     * Constructs a new product with all fields initialized.
     * @param seller The seller of the product
     * @param price The price of the product
     * @param name The name of the product
     * @param description The description of the product
     * @param currentUser The user of the UI using this product
     */
    public Product(String seller, int price, String name, String description, String currentUser) {
        this.seller = seller;
        this.price = price;
        this.name = name;
        this.description = description;
        this.currentUser = currentUser;

        if (this.currentUser != null) {
            this.ownedByUser = this.seller.equals(currentUser);
        }
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * This allows the user to set the current user of the UI.
     * It is used in the toString method to determine if the right angle brackets should be visible.
     * If this remains unset, the toString method returns the standard description.
     * This also sets the ownedByUser flag to true if the currentUser equals the seller of the item.
     * @param currentUser The user currently logged into the UI.
     */
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;

        if (currentUser != null){
            this.ownedByUser = this.currentUser.equals(this.seller);
        }
    }

    /**
     * Takes the information from the class to determine the string representation of the object.
     * If currentUser is set, and the seller of the item is the same
     * @return
     */
    @Override
    public String toString() {
        String angleBracketsForCurrentUser = ownedByUser ? ">>>  " : "";
        String wocoinOrWocoins = this.price == 1 ? "WoCoin" : "WoCoins";
        return String.format("%s%s: %s  [%d %s]", angleBracketsForCurrentUser, this.name, this.description, this.price, wocoinOrWocoins);
    }

    /**
     * Compares two products based on their price, then their description.
     * If the price of the two objects are different, returns a number &lt; 0 if this product has a lower price and a a number &gt; 0 if this product has a higher price.
     * Otherwise it compares the two product names lexicographically using the String compareTo.
     * @param otherProduct the Product to be compared to
     * @return a negative integer, zero, or a positive integer as this product is less than, equal to, or greater than the specified product.
     */
    @Override
    public int compareTo(Product otherProduct) {
        if (this.price != otherProduct.getPrice()) {
            return this.price - otherProduct.getPrice();
        }
        else {
            return this.name.compareToIgnoreCase(otherProduct.getName());
        }
    }
}
