package clients.cashier;


import catalogue.Enums.SearchSelection;
import catalogue.Product;

/**
 * The Cashier Controller
 */

public class CashierController
{
  private CashierModel model = null;
  private CashierView  view  = null;

  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public CashierController( CashierModel model, CashierView view )
  {
    this.view  = view;
    this.model = model;
  }

  /**
   * Check interaction from view
   * @param search The product number or keyword to be searched
   */
  public void doCheck(String search)
  {
    switch (model.checkSearchSelection()) {
      case KEYWORD:
        model.doProductSearch(search);
        break;
      case PRODUCT_NUMBER:
        model.doCheck(search);
        break;
    }
  }

   /**
   * Buy interaction from view
   */
  public void doBuy(Product product)
  {
    model.addToBasket(product);
  }
  
   /**
   * Bought interaction from view
   */
  public void doBought()
  {
    model.checkoutBasket();
  }

  /**
   *
   */
  public void removeFromCart(Product product) {
    model.removeFromCart(product);
  }

  /**
   * Set search type via radio button
   */
  public void setSearchType(SearchSelection searchType) { model.setSearchSelection(searchType); }

}
