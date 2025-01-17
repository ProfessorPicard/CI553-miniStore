package clients.packing;


import catalogue.Basket;
import catalogue.BetterBasket;
import catalogue.Enums.SortType;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockReadWriter;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implements the Model of the warehouse packing client
 * @author Peter Blackburn
 */
public class PackingModel extends Observable
{
  //Initialise our order basket and the stock reader
  private AtomicReference<BetterBasket> currentOrder = new AtomicReference<>();
  private StockReadWriter theStock = null;
  private OrderProcessing theOrder = null;
  private String actionText = "";
  private StateOf worker   = new StateOf();

  /*
   * Construct the model of the warehouse Packing client
   * @param mf The factory to create the connection objects
   */
  public PackingModel(MiddleFactory mf) {
    try {
      theStock = mf.makeStockReadWriter();  // Database access
      theOrder = mf.makeOrderProcessing();  // Process order
    } catch ( Exception e ) {
      DEBUG.error("CustomerModel.constructor\n%s", e.getMessage() );
    }

    currentOrder.set( null );                  // Initial Basket
    // Start a background check to see when a new order can be packed
    new Thread( () -> checkForNewOrder() ).start();
  }

  /**
   * Semaphore used to only allow 1 order
   * to be packed at once by this person
   */
  class StateOf {
    private boolean held = false;
    
    /**
     * Claim exclusive access
     * @return true if claimed else false
     */
    public synchronized boolean claim() {
      return held ? false : (held = true);
    }
    
    /**
     * Free the lock
     */
    public synchronized void free() {
      assert held;
      held = false;
    }

  }
  
  /**
   * Method run in a separate thread to check if there
   * is a new order waiting to be packed and we have
   * nothing to do.
   */
  private void checkForNewOrder() {
    actionText = "";
    while ( true ) {
      try {
        //Check if the packing worker is available
        boolean isFree = worker.claim();
        if ( isFree ) {
          //Create a better basket and convert the order basket to a better basket if not null.
          BetterBasket sb = new BetterBasket();
          Basket b = theOrder.getOrderToPack();

          //If there is an order, tell the packer. If not keep waiting.
          if(b != null) {
            for(Product p : b){
              sb.add(p);
            }
            //Sort the basket, set the current order and action text.
            sb.sort(SortType.ASCENDING);
            currentOrder.set(sb);
            actionText = "Order found, please pack items";
          } else {
            //Set our worker as free and update our action text.
            worker.free();
            actionText = "Waiting for new orders";
          }

          //Notify observers of dataset change
          askForUpdate(actionText);

        }
        //Idle the thread for 2 secs
        Thread.sleep(2000);
      } catch ( Exception e ) {
        DEBUG.error("%s\n%s",                // Eek!
           "BackGroundCheck.run()\n%s",
           e.getMessage() );
      }
    }
  }
  
  
  /**
   * Return the Basket of products that are to be picked
   * @return the basket
   */
  public BetterBasket getBasket() {
    return currentOrder.get();
  }

  /**
   * Process a packed Order
   */
  public void doPacked() {
    try {
      actionText = "";
      //The current packed basket
      BetterBasket basket =  currentOrder.get();

      if ( basket != null ) {
        //Clear the
        currentOrder.set( null );
        int no = basket.getOrderNum();
        theOrder.informOrderPacked( no );
        actionText = "Order Packed";
        worker.free();
      } else {
        actionText = "Order not packed";
      }
      setChanged(); notifyObservers(actionText);
    }
    catch ( OrderException e ) {
      DEBUG.error( "ReceiptModel.doOk()\n%s\n",//  should not
                            e.getMessage() ); //  happen
    }
    askForUpdate(actionText);
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
}





