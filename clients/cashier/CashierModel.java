package clients.cashier;

import catalogue.BetterBasket;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
import debug.DEBUG;
import middle.*;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Implements the Model of the cashier client
 */
public class CashierModel extends Observable
{
  private enum State { process, checked }

  private State       theState   = State.process;   // Current state
  private Product     theProduct = null;            // Current product
  private BetterBasket theBasket  = null;           // Bought items
  private BetterBasket searchBasket = null;         // The Search Results

  private String pn = "";                      // Product being processed

  private SearchSelection searchSelection = SearchSelection.PRODUCT_NUMBER;

  private StockReadWriter theStock     = null;
  private OrderProcessing theOrder     = null;

  /**
   * Construct the model of the Cashier
   * @param mf The factory to create the connection objects
   */

  public CashierModel(MiddleFactory mf)
  {
    try                                           // 
    {      
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      DEBUG.error("CashierModel.constructor\n%s", e.getMessage() );
    }
    theState   = State.process;                  // Current state
  }

  /**
   * Get the Basket of products
   * @return Basket
   */
  public BetterBasket getBasket()
  {
    return theBasket;
  }

  /**
   * Get the Search Result of products
   * @return Search Results
   */
  public BetterBasket getSearchBasket()
  {
    if(searchBasket == null)
      searchBasket = new BetterBasket();
    return searchBasket;
  }

  /**
   * Check if the product is in Stock by Product Number
   * @param productNum The product number
   */
  public void doCheck(String productNum )
  {
    String theAction = "";
    searchBasket.clear();
    theState  = State.process;
    pn  = productNum.trim();
    try
    {
      if (theStock.exists( pn ))
      {
        Product pr = theStock.getDetails(pn);
        searchBasket.add(pr);
      } else {
        theAction = "Product number not found | " + pn;
      }
      theState = State.checked;
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doProductNumberCheck", e.getMessage() );
      theAction = e.getMessage();
    }
    setChanged();
    notifyObservers(theAction);
  }

  /**
   * Returns an ArrayList of products matching the search terms
   * @param pSearch Space seperated list of search terms
   */
  public void doProductSearch(String pSearch) {

    searchBasket.clear();
    theState  = State.process;
    String theAction = "";

    try {
      ArrayList<Product> products = theStock.searchProducts(pSearch);
      if (products.size() > 0) {
        for(Product product : products) {
          searchBasket.add(product);
        }
        theAction = products.size() + " products found";
      } else {
        theAction = "No Products Found";
      }
      theState = State.checked;
    } catch (StockException e) {
      DEBUG.error("%s\n%s",
              "CashierModel.doProductSearch", e.getMessage());
      theAction = e.getMessage();
    }
    setChanged();
    notifyObservers(theAction);
  }

  /**
   * Buy the product
   */
  public void doBuy(Product product)
  {
    String theAction = "";
    try
    {
      if ( theState != State.checked )          // Not checked
      {                                         //  with customer
        theAction = "Please check availability first";
      } else {
        boolean stockBought =                   // Buy
          theStock.buyStock(                    //  however
                  product.getProductNum(),         //  may fail
                  product.getQuantity() );         //
        if ( stockBought )                      // Stock bought
        {                                       // T
          makeBasketIfReq();                    //  new Basket ?
          theBasket.add(product);          //  Add to bought
          theAction = "Purchased " +            //    details
                  product.getDescription();  //
        } else {                                // F
          theAction = "Couldn't Add to Basket";       //  Now no stock
        }
        searchBasket = null;
      }
    } catch( StockException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doBuy", e.getMessage() );
      theAction = e.getMessage();
    }
    theState = State.process;                   // All Done
    setChanged();
    notifyObservers(theAction);
  }
  
  /**
   * Customer pays for the contents of the basket
   */
  public void doBought()
  {
    String theAction = "";
    try
    {
      if (theBasket != null &&
           theBasket.size() >= 1 )            // items > 1
      {                                       // T
        theOrder.newOrder(theBasket);       //  Process order
        theBasket = null;                     //  reset
      }                                       //
      theAction = "Start New Order";            // New order
      theState = State.process;               // All Done
       theBasket = null;
       searchBasket = null;
    } catch( OrderException e )
    {
      DEBUG.error( "%s\n%s", 
            "CashierModel.doCancel", e.getMessage() );
      theAction = e.getMessage();
    }
    theBasket = null;
    setChanged(); notifyObservers(theAction); // Notify
  }

  /**
   * ask for update of view called at start of day
   * or after system reset
   */
  public void askForUpdate()
  {
    setChanged();
    notifyObservers("Welcome");
  }
  
  /**
   * make a Basket when required
   */
  private void makeBasketIfReq()
  {
    if ( theBasket == null )
    {
      try
      {
        int uon   = theOrder.uniqueNumber();     // Unique order num.
        theBasket = makeBasket();                //  basket list
        theBasket.setOrderNum( uon );            // Add an order number
      } catch ( OrderException e )
      {
        DEBUG.error( "Comms failure\n" +
                     "CashierModel.makeBasket()\n%s", e.getMessage() );
      }
    }
  }

  /**
   * return an instance of a new Basket
   * @return an instance of a new Basket
   */
  protected BetterBasket makeBasket()
  {
    return new BetterBasket();
  }

  /**
   * returns the type of search selected
   * @return an Enum stating the type of search selected
   */
  public SearchSelection checkSearchSelection() { return searchSelection; }

  /**
   * Sets the type of search required
   * @param searchSelection an Enum stating the type of search required
   */
  public void setSearchSelection(SearchSelection searchSelection) {
    this.searchSelection = searchSelection;
    DEBUG.trace("setSearchSelection", searchSelection);
  }

}
  
