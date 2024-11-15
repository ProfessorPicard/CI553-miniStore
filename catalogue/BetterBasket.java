package catalogue;

import catalogue.Enums.SortType;
import debug.DEBUG;

import java.io.Serializable;
import java.util.Collections;

/**
 * An improvement to the existing basket system. Added quantities to products
 * and sorting the basket in ascending order of product number
 * @author              Peter Blackburn
 * @version 1.0
 */
public class BetterBasket extends Basket implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Product selectedProduct = null;

  /**
   * @param sortType    The type of sorting to be applied from SortType Enum
   * @author            Peter Blackburn
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
   * @param pr    Product being added to the basket
   * @return      Boolean stating whether the collection was modified
   * @author      Peter Blackburn
   */
  @Override
  public boolean add(Product pr) {
    //Check if product is already in the basket by product number
    for(Product p : this) {
      DEBUG.trace("PRODUCT NUMBER:" + p.getProductNum());
      if(p.getProductNum().equals(pr.getProductNum())) {
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

  public void setSelectedProduct(Product product) { selectedProduct = product; }
  public Product getSelectedProduct() { return selectedProduct; }
  public void clearSelectedProduct() { selectedProduct = null;}

}
