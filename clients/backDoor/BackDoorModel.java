package clients.backDoor;

import catalogue.BetterBasket;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReadWriter;
import middle.StockReader;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Implements the Model of the back door client
 */
public class BackDoorModel extends Observable
{
  //Initialise our search basket and the stock reader
  private BetterBasket stockBasket = null;
  private StockReadWriter theStock = null;

  //The currently selected type of search
  private SearchSelection searchSelection = SearchSelection.PRODUCT_NUMBER;

  /*
   * Construct the model of the back door client
   * @param mf The factory to create the connection objects
   */
  public BackDoorModel(MiddleFactory mf) {
    try  {
      theStock = mf.makeStockReadWriter();        // Database access
    } catch ( Exception e ) {
      DEBUG.error("CustomerModel.constructor\n%s", e.getMessage() );
    }

    stockBasket = makeBasket();                     // Initial Basket
  }
  
  /**
   * Get the Basket of products
   * @return basket
   */
  public BetterBasket getBasket()
  {
    return stockBasket;
  }

  /**
   * Returns an ArrayList of products matching the search terms
   * @param pSearch Space seperated list of search terms
   */
  public void searchByKeyword(String pSearch) {
    //Clear search basket and reset action text
    stockBasket.clear();
    String actionText = "";

    try {

      //Get an arrayList of all matched products and add them to the basket
      //Set action text appropriately
      ArrayList<Product> products = theStock.searchProducts(pSearch);
      if (!products.isEmpty()) {
        stockBasket.addAll(products);
        actionText = products.size() + " Products Found";
      } else {
        actionText = "No Products Found";
      }
    } catch (StockException e) {
      //Send error to DEBUG and action text
      DEBUG.error("%s\n%s",
              "CashierModel.doProductSearch", e.getMessage());
      actionText = e.getMessage();
    }
    //Notify observers of dataset change and send action text
    askForUpdate(actionText);
  }

  /**
   * Check if the product is in Stock by product number
   * @param productNum The product number
   */
  public void searchByNumber(String productNum) {
    //Reset all search variables and trim the product number to remove spaces
    stockBasket.clear();
    String actionText = "";
    String pn = productNum.trim();

    if(pn.isEmpty()) {
      actionText = "No product number entered";
      askForUpdate(actionText);
      return;
    }

    try {
      //If the item is found in the database then add it to the search results
      if (theStock.exists(pn)) {                                         // T
        Product pr = theStock.getDetails(pn);
        stockBasket.add(pr);

        //If the product is in stock or out of stock, set the action text appropriately
        if (pr.getQuantity() > 0) {
          actionText = "Product in stock | " + pr.getDescription();
        } else {
          actionText = "Product not in stock | " + pr.getDescription();
        }
      } else {
        //If product is not found, set the action text
        actionText = "Product number not found | " + pn;
      }

    } catch (StockException e) {

      //Send error to DEBUG and to the action text
      DEBUG.error("CustomerClient.doCheck()\n%s",
              e.getMessage());
      actionText = e.getMessage();
    }

    //Notify observers of dataset change
    askForUpdate(actionText);
  }

  /**
   * Re stock selected product
   * @param selectedProduct The selected product in the basket
   * @param quantity How many to be added
   */
  public void doRStock(Product selectedProduct, String quantity)
  {
    String actionText = "";
    stockBasket = makeBasket();
    if(selectedProduct != null) {
      String pn = selectedProduct.getProductNum().trim();
      int amount = 0;
      try {
        String aQuantity = quantity.trim();
        try {
          amount = Integer.parseInt(aQuantity);
          if ( amount < 0 )
            throw new NumberFormatException("-ve");
        }
        catch ( Exception err) {
          actionText = "Invalid quantity";
          setChanged(); notifyObservers(actionText);
          return;
        }

        //Search for restocked product
        if (theStock.exists(pn)) {
          //If it exists add the amount selected to it
          theStock.addStock(pn, amount);
          //Get details of our updated product and add it to our basket
          Product pr = theStock.getDetails(pn);
          stockBasket.add(pr);
          //update action text
          actionText = "Product stock updated";
        } else {
          //if product not found, update action text
          actionText = "Unknown product number: " + pn;
        }
      } catch(StockException e) {
        //on error, update the action text
        actionText = e.getMessage();
      }
      //Notify observers of dataset change
      askForUpdate(actionText);
    }
  }

  /**
   * Clear the products from the basket
   */
  public void doClear()
  {
    String theAction = "";
    stockBasket.clear();
    theAction = "Search for product by product number or keyword";
    askForUpdate(theAction);
  }

  /**
   * Notify observers of changed dataset
   * @param actionText The string to be displayed in the action text label
   */
  private void askForUpdate(String actionText) {
    setChanged();
    notifyObservers(actionText);
  }
  
  /**
   * return an instance of a Basket
   * @return a new instance of a Basket
   */
  protected BetterBasket makeBasket()
  {
    return new BetterBasket();
  }

  /**
   * returns the type of search selected
   * @return an Enum stating the type of search selected
   */
  public SearchSelection checkSearchSelection() {
    return searchSelection;
  }

  /**
   * Sets the type of search required
   * @param searchSelection an Enum stating the type of search required
   */
  public void setSearchSelection(SearchSelection searchSelection) {
    this.searchSelection = searchSelection;
    DEBUG.trace("setSearchSelection", searchSelection);
  }
}

