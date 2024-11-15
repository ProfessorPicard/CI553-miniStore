package clients.customer;

import catalogue.BetterBasket;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReader;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Implements the Model of the customer client
 * @author Peter Blackburn
 */
public class CustomerModel extends Observable {

    //Initialise our search basket and the stock reader
    private BetterBasket searchBasket = null;
    private StockReader theStock = null;

    //The currently selected type of search
    private SearchSelection searchSelection = SearchSelection.PRODUCT_NUMBER;

    /*
     * Construct the model of the Customer
     * @param mf The factory to create the connection objects
     */
    public CustomerModel(MiddleFactory mf) {
        //Make our stock reader and create a search basket
        try {
            theStock = mf.makeStockReader();
        } catch (Exception e) {
            //Send error to DEBUG
            DEBUG.error("""
                    CustomerModel.constructor
                    Database not created?
                    %s
                    """, e.getMessage());
        }
        searchBasket = makeBasket();
    }

    /**
     * return the Basket of products
     * @return the basket of products
     */
    public BetterBasket getBasket() {
        return searchBasket;
    }

    /**
     * Returns an ArrayList of products matching the search terms
     * @param pSearch Space seperated list of search terms
     */
    public void searchByKeyword(String pSearch) {
        //Clear search basket and reset action text
        searchBasket.clear();
        String actionText = "";

        try {

            //Get an arrayList of all matched products and add them to the basket
            //Set action text appropriately
            ArrayList<Product> products = theStock.searchProducts(pSearch);
            if (!products.isEmpty()) {
                searchBasket.addAll(products);
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
        searchBasket.clear();
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
                searchBasket.add(pr);

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
     * Clear the products from the basket
     */
    public void doClear() {
        //Clear the search basket and set action text
        String theAction = "";
        searchBasket.clear();
        theAction = "Search for product by product number or keyword";

        //Notify observers of dataset change
        askForUpdate(theAction);
    }

    /**
     * Notify observers of changed dataset
     * @param actionText The string to be displayed in the action text label
     */
    private void askForUpdate(String actionText) {
        //Notify observers of changed dataset and set action text
        setChanged();
        notifyObservers(actionText);
    }

    /**
     * Make a new Basket
     * @return an instance of a new Basket
     */
    protected BetterBasket makeBasket() {
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

