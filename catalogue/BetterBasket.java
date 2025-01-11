package catalogue;

import catalogue.Enums.SortType;
import debug.DEBUG;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;

/**
 * An improvement to the existing basket system. Added quantities to products
 * and sorting the basket in ascending order of product number
 * @author Peter Blackburn
 * @version 1.0
 */
public class BetterBasket extends Basket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Product selectedProduct = null;

    /**
     * @param sortType The type of sorting to be applied from SortType Enum
     * @author Peter Blackburn
     */
    public void sort(SortType sortType) {
        switch (sortType) {
            case ASCENDING:
                Collections.sort(this);
                break;
            case DESCENDING:
                Collections.sort(this, Collections.reverseOrder());
                break;
        }
    }

    /**
     * @param pr Product being added to the basket
     * @return Boolean stating whether the collection was modified
     * @author Peter Blackburn
     */
    @Override
    public boolean add(Product pr) {
        //Check if product is already in the basket by product number
        for (Product p : this) {
            DEBUG.trace("PRODUCT NUMBER:" + p.getProductNum());
            if (p.getProductNum().equals(pr.getProductNum())) {
                //If product is in the basket, increase the quantity of the product by the quantity added
                p.setQuantity(p.getQuantity() + pr.getQuantity());
                //return true that the collection has been modified
                return true;
            }
        }
        //if not found in basket, add it as normal, sort the basket and return true
        super.add(pr);
        sort(SortType.ASCENDING);
        return true;
    }

    /**
     * Sets the currently selected product in the basket
     * @param product the Product currently selected
     */
    public void setSelectedProduct(Product product) {
        selectedProduct = product;
    }

    /**
     * Gets the currently selected Product in the basket
     * @return The currently selected Product
     */
    public Product getSelectedProduct() {
        return selectedProduct;
    }

    /**
     * Clears the currently selected Product in the basket
     */
    public void clearSelectedProduct() {
        selectedProduct = null;
    }

    /**
     * Gets the total price of the products in the basket and returns the amount as a double
     * @return The total price of the basket as a double
     */
    public double getBasketTotalPrice() {
        double totalPrice = 0;

        for(Product p : this) {
            totalPrice += p.getTotalPrice();
        }
        BigDecimal bigDecimal = new BigDecimal(totalPrice);
        BigDecimal roundedWithScale = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return roundedWithScale.doubleValue();
    }

}
