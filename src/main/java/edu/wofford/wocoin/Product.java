package edu.wofford.wocoin;

/**
 * This class models the form of a product in the Wocoin database.
 */
public class Product {
    private String seller;
    private int price;
    private String name;
    private String description;

    /**
     * Constructs a new, empty product
     */
    public Product() {
        seller = null;
        name = null;
        description = null;
    }

    /**
     * Constructs a new product with all fields initialized.
     * @param seller The seller of the product
     * @param price The price of the product
     * @param name The name of the product
     * @param description The description of the product
     */
    public Product(String seller, int price, String name, String description) {
        this.seller = seller;
        this.price = price;
        this.name = name;
        this.description = description;
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
}
