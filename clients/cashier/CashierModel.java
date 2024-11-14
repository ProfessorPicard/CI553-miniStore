package clients.cashier;

import catalogue.BetterBasket;
import catalogue.Enums.OrderState;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
import debug.DEBUG;
import middle.*;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Implements the Model of the cashier client
 */
public class CashierModel extends Observable {

  private OrderState orderState = OrderState.PROCESS;

  private BetterBasket theBasket  = null;
  private BetterBasket searchBasket = null;

  private SearchSelection searchSelection = SearchSelection.PRODUCT_NUMBER;

  private StockReadWriter theStock     = null;
  private OrderProcessing theOrder     = null;

  /**
   * Construct the model of the Cashier
   * @param mf The factory to create the connection objects
   */
  public CashierModel(MiddleFactory mf) {
    try {
      //Access the stock database
      theStock = mf.makeStockReadWriter();

      //Process our order
      theOrder = mf.makeOrderProcessing();

    } catch ( Exception e ) {
      DEBUG.error("CashierModel.constructor\n%s", e.getMessage() );
    }

    //Update the current state
    orderState = OrderState.PROCESS;
  }

  /**
   * Get the Basket of products
   * @return Basket
   */
  public BetterBasket getBasket() {
    return theBasket;
  }

  /**
   * Get the Search Result of products
   * @return Search Results
   */
  public BetterBasket getSearchBasket() {
    if(searchBasket == null)
      searchBasket = new BetterBasket();
    return searchBasket;
  }

  /**
   * Check if the product is in Stock by Product Number
   * @param productNum The product number
   */
  public void doCheck(String productNum) {

    //Clear the search basket and reset the state and action strings
    String theAction = "";
    searchBasket.clear();
    orderState = OrderState.PROCESS;

    // Product being processed
    String pn = productNum.trim();

    if(pn.isEmpty()) {
      theAction = "No product number entered";
      askForUpdate(theAction);
      return;
    }

    try
    {

      //If the stock exists, add our product to the search basket.
      if (theStock.exists(pn))
      {
        Product pr = theStock.getDetails(pn);
        searchBasket.add(pr);
      } else {

        //Update our action text
        theAction = "Product number not found | " + pn;
      }

      //Update our state to product checked
      orderState = OrderState.CHECKED;

    } catch( StockException e )
    {

      //Send error message to debug and action text
      DEBUG.error( "%s\n%s", 
            "CashierModel.doProductNumberCheck", e.getMessage() );
      theAction = e.getMessage();
    }

    //Update our observers with any changes made to the dataset
    askForUpdate(theAction);
  }

  /**
   * Adds products matching the search terms to our search basket
   * @param pSearch Space seperated list of search terms
   */
  public void doProductSearch(String pSearch) {

    //Clear the search basket and reset the state and action strings
    searchBasket.clear();
    orderState = OrderState.PROCESS;
    String theAction = "";

    try {

      //Get the results of our search into an arraylist.
      //If the arrayList is not empty, add all the items to our search basket
      ArrayList<Product> products = theStock.searchProducts(pSearch);
      if (!products.isEmpty()) {
        searchBasket.addAll(products);
        theAction = products.size() + " products found";
      } else {
        theAction = "No Products Found";
      }

      //Update the state to show we checked the items and their stock
      orderState = OrderState.CHECKED;

    } catch (StockException e) {

      //Send error message to debug and to the action text
      DEBUG.error("%s\n%s",
              "CashierModel.doProductSearch", e.getMessage());
      theAction = e.getMessage();

    }

    //Notify our observers about the changes we made
    askForUpdate(theAction);

  }

  /**
   * Add product to the basket
   * @param product The product to add to the basket
   */
  public void addToBasket(Product product) {
    String theAction = "";

    //If we checked the state of the product add it to the basket
    //If not let the cashier know
    try {
      if (orderState != OrderState.CHECKED) {
        theAction = "Please check availability first";
      } else {



        //remove the quantity of product from the database
        boolean stockBought = theStock.buyStock(
                  product.getProductNum(),
                  product.getQuantity());

        //if successful, make a new basket if needed and add the product
        if ( stockBought ) {
          makeBasketIfReq();
          Product p = new Product(product.getProductNum(), product.getDescription(), product.getPrice(), product.getQuantity());
          p.setPictureURL(product.getPictureURL());
          DEBUG.trace(p.getPictureURL());
          theBasket.add(p);

          //update the action text
          theAction = "Added item number " +
                  product.getProductNum() +
                  " to the basket.";
        } else {

          //update the action text on failure to add product
          theAction = "Couldn't Add to Basket";
        }

        //reset the search basket
        searchBasket = null;
      }
    } catch( StockException e ) {

      //Send error to debug and to action text
      DEBUG.error( "%s\n%s", 
            "CashierModel.doBuy", e.getMessage() );
      theAction = e.getMessage();
    }

    //Update the order state and notify any observers of changes
    orderState = OrderState.PROCESS;
    askForUpdate(theAction);
  }
  
  /**
   * Customer checks out the basket
   */
  public void checkoutBasket() {
    String theAction = "";

    //If the basket is not null and contains 1 or more items, checkout the order.
    try {
      if (theBasket != null && !theBasket.isEmpty()) {

        //Checkout the order and set our state and action text
        theOrder.newOrder(theBasket);
        theAction = "Order checked out";
        orderState = OrderState.PROCESS;

        //Reset the baskets
        searchBasket = null;
        theBasket = null;

      } else {

        //Set action text for no basket found
        theAction = "No Basket Found";
      }
    } catch( OrderException e ) {

      //Send error message to debug and action text
      DEBUG.error( "%s\n%s", 
            "CashierModel.doCancel", e.getMessage() );
      theAction = e.getMessage();
    }

    //Notify our observers of any changes to the dataset
    askForUpdate(theAction);
  }

  /**
   * ask for update of view called at start of day
   * or after system reset
   */
  public void askForUpdate(String updateText) {
    setChanged();
    notifyObservers(updateText);
  }
  
  /**
   * make a Basket with a unique serial number when required
   */
  private void makeBasketIfReq() {

    //If the basket is null, create a new unique basket
    if ( theBasket == null )
    {
      try
      {

        //Generates a unique order number and makes a new basket with it
        int uon = theOrder.uniqueNumber();
        theBasket = makeBasket();
        theBasket.setOrderNum(uon);

      } catch ( OrderException e ) {

        //Send error message to debug
        DEBUG.error("""
                Comms failure
                CashierModel.makeBasket()
                %s""", e.getMessage() );
      }
    }
  }

  /**
   * return an instance of a new Basket
   * @return an instance of a new Basket
   */
  protected BetterBasket makeBasket() {
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


  /**
   * Removes selected product from the shopping cart
   * @param product The product to remove from the shopping cart
   */
  public void removeFromCart(Product product) {

    for(Product p : theBasket) {
      if(p.getProductNum().equals(product.getProductNum())) {

        //If product is in the basket remove it
        for(Product pr : theBasket) {
          if(pr.getProductNum().equals(product.getProductNum())) {
            theBasket.remove(pr);
            break;
          }
        }
        String theAction = "";

        try {

          //add back the stock that was removed from the basket
          theStock.addStock(product.getProductNum(), product.getQuantity());
          theAction = "Item number " + product.getProductNum() + " removed from cart.";

        } catch (StockException e) {

          //Send error message to debug and to the action text
          DEBUG.error( "%s\n%s",
                  "CashierModel.removeFromCart", e.getMessage() );
          theAction = e.getMessage();
        }

        //Notify our observers of any changes made
        setChanged();
        notifyObservers(theAction);
      }
      return;
    }
  }
}
  
