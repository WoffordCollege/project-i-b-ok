package edu.wofford.wocoin;

/**
 * This class models the form of a product in the Wocoin database.
 */
public class Product implements Comparable<Product>{
    private int id;
    private String seller;
    private int price;
    private String name;
    private String description;
    private String currentUser;
    private boolean ownedByUser;
    private DisplayType displayType;

    /**
     * This allows the user to choose a display type for the products using {@link Product#toString()}
     */
    public enum DisplayType {SHOWCURRENTUSER, HIDECURRENTUSER}


    public Product(String seller, int price, String name, String description) {
        this(-1, seller, price, name, description, null);
    }

    public Product(int id, String seller, int price, String name, String description) {
        this(id, seller, price, name, description, null);
    }

    /**
     * Works with the main constructor to set the default values of the comparison type and display type
     * Constructs a new product with all fields initialized.
     * @param seller The seller of the product
     * @param price The price of the product
     * @param name The name of the product
     * @param description The description of the product
     * @param currentUser The wallet address of the user using this product
     */
    public Product(int id, String seller, int price, String name, String description, String currentUser) {
        this(-1, seller, price, name, description, currentUser, DisplayType.HIDECURRENTUSER);
    }

    /**
     * Constructs a new product with all fields initialized.
     * @param seller The seller of the product
     * @param price The price of the product
     * @param name The name of the product
     * @param description The description of the product
     * @param currentUser The wallet address of the user using this product
     * @param displayType Chooses the method {@link Product#toString()} uses to show on display
     */
    public Product(int id, String seller, int price, String name, String description, String currentUser, DisplayType displayType) {
        this.id = id;
        this.seller = seller;
        this.price = price;
        this.name = name;
        this.description = description;
        this.currentUser = currentUser;

        if (this.currentUser != null) {
            this.ownedByUser = this.seller.equals(currentUser);
        }

        this.displayType = displayType;
    }

    public int getId() {
        return id;
    }

    public String getSeller() {
        return seller;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    /**
     * Takes the information from the class to determine the string representation of the object.
     * If currentUser is set, and the seller of the item is the same as the currentUser, and the displayType is SHOWCURRENTUSER,
     * returns a string in the form <code>&gt;&gt;&gt;  name: description  [n WoCoin(s)]</code>
     * Otherwise returns a string in the form <code>name: description  [n WoCoin(s)]</code>
     * @return a string representing the Product object.
     */
    @Override
    public String toString() {
        String angleBracketsForCurrentUser = displayType == DisplayType.SHOWCURRENTUSER && this.ownedByUser ? ">>>  " : "";
        String wocoinOrWocoins = this.price == 1 ? "WoCoin" : "WoCoins";
        return String.format("%s%s: %s  [%d %s]", angleBracketsForCurrentUser, this.name, this.description, this.price, wocoinOrWocoins);
    }

    /**
     * Compares two products based on their price, then their name.
     * If the price of the two objects are different, and the CompareType is price,
     * returns a number &lt; 0 if this product has a lower price and a a number &gt; 0 if this product has a higher price.
     * Otherwise it compares the two product names lexicographically using the {@link String#compareTo(String)}.
     * @param otherProduct the Product to be compared to
     * @return a negative integer, zero, or a positive integer as this product is less than, equal to, or greater than the specified product.
     */
    public int compareToWithPrice(Product otherProduct) {
        if (this.price != otherProduct.getPrice()) {
            return this.price - otherProduct.getPrice();
        }
        else {
            return this.name.compareToIgnoreCase(otherProduct.getName());
        }
    }

    /**
     * Compares two products based on their name
     * @param otherProduct the Product to be compared to
     * @return a negative integer, zero, or a positive integer as this product is less than, equal to, or greater than the specified product.
     */
    @Override
    public int compareTo(Product otherProduct) {
        return this.name.compareToIgnoreCase(otherProduct.getName());
    }

    /**
     * Returns true if this and the Product have the same price, seller, name, and description
     * Returns false otherwise
     * @param o the object to be compared
     * @return true if the Products are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getPrice() == product.getPrice() &&
                getSeller().equals(product.getSeller()) &&
                getName().equals(product.getName()) &&
                getDescription().equals(product.getDescription());
    }
}
