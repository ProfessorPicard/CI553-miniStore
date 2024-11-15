package catalogue;

import debug.DEBUG;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Used to hold the following information about
 * a product: Product number, Description, Price, Stock level, Image URL.
 * @author Mike Smith University of Brighton
 * @author Peter Blackburn
 * @version 2.1
 */
public class Product implements Serializable, Comparable<Product> {
    @Serial
    private static final long serialVersionUID = 20092506;
    private String productNumber;
    private String description;
    private double unitPrice;
    private int quantity;
    private String pictureURL;

    /**
     * Product Constructor
     * @param productNumber Product number
     * @param description Description of product
     * @param unitPrice The unit price of the product
     * @param quantity The Quantity of the product involved
     */
    public Product(String productNumber, String description,
                   double unitPrice, int quantity) {
        this.productNumber = productNumber;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductNum() {
        return productNumber;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setProductNum(String aProductNum) {
        productNumber = aProductNum;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setPrice(double aPrice) {
        unitPrice = aPrice;
    }

    public void setQuantity(int aQuantity) {
        quantity = aQuantity;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    /**
     * @param pr The product being compared.
     * @return The result of the comparison (1 if higher, 0 if equal, -1 if lower)
     */
    @Override
    public int compareTo(Product pr) {
        //Parse the product numbers from strings into longs
        long productNum = Long.parseLong(productNumber);
        long productNumCompare = Long.parseLong(pr.productNumber);
        //Use the built-in Long compare function to return an ascending comparison
        return Long.compare(productNum, productNumCompare);
    }

    /**
     * Overrides the toString output for the Product displaying a description, total price and the stock level
     * @return String showing the description, total price and stock level
     */
    @Override
    public String toString() {
        return productNumber + " - " + description + " | Total Price: " + unitPrice * quantity + " - Qty: " + quantity;
    }

    /**
     * Returns the total value of the product as a double
     * @return The total value of the product as a double
     */
    public double getTotalPrice() {
      BigDecimal bigDecimal = new BigDecimal(unitPrice * quantity);
      BigDecimal roundedWithScale = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
      return roundedWithScale.doubleValue();
    }

}
