package edu.wofford.wocoin;

import java.util.Objects;

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
    private DisplayType displayType;

    /**
     * This allows the user to choose a display type for the products using {@link Product#toString()}
     */
    public enum DisplayType {SHOWCURRENTUSER, HIDECURRENTUSER}


    public Product(String seller, int price, String name, String description) {
        this(seller, price, name, description, null);
    }

    /**
     * Works with the main constructor to set the default values of the comparison type and display type
     */
    public Product(String seller, int price, String name, String description, String currentUser) {
        this(seller, price, name, description, currentUser, DisplayType.HIDECURRENTUSER);
    }

    /**
     * Constructs a new product with all fields initialized.
     * @param seller The seller of the product
     * @param price The price of the product
     * @param name The name of the product
     * @param description The description of the product
     * @param currentUser The wallet address of the user using this product
     * @param compareType Chooses what the {@link Product#compareTo(Product)} function uses to determine which object comes first
     * @param displayType Chooses the method {@link Product#toString()} uses to show on display
     */
    public Product(String seller, int price, String name, String description, String currentUser, DisplayType displayType) {
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

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    /**
     * Takes the information from the class to determine the string representation of the object.
     * If currentUser is set, and the seller of the item is the same as the currentUser, and the displayType is SHOWCURRENTUSER,
     * returns a string in the form <code>>>>  name: description  [n WoCoin(s)]</code>
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

    @Override
    public int hashCode() {
        return Objects.hash(getSeller(), getPrice(), getName(), getDescription());
    }
}
