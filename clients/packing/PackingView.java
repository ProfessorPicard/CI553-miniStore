package clients.packing;

import catalogue.BetterBasket;
import catalogue.Product;
import custom.SearchRenderer;
import middle.MiddleFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Packing view.

 */

public class PackingView implements Observer
{
  //Pack Order Button Text
  private static final String PACKED = "Pack Order";

  //Page Dimensions

  private static final int H = 500;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels

  //Order Packing Page Controls
  private JLabel titleLbl;
  private JLabel actionTxtLbl;
  private JButton btnPackOrder;
  private JList<Product> orderProductList;
  private JScrollPane orderScrollPane;

  //Basket for current order to be packed
  private BetterBasket orderBasket;

  //Packing controller
  private PackingController cont = null;

  /**
   * Construct the view
   *
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-cordinate of position of window on screen 
   * @param y     y-cordinate of position of window on screen  
   */
  public PackingView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {

    Container contentContainer = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    contentContainer.setLayout(null);                             // No layout manager
    rootWindow.setSize(W, H);                     // Size of Window
    rootWindow.setLocation(x, y);

    initialiseControls();
    setupUI(contentContainer);
    setupEventHandlers();

    rootWindow.setVisible(true);                  // Make visible);
  }

  /**
   * Initialises all of our page controls
   */
  private void initialiseControls() {
    titleLbl = new JLabel();
    actionTxtLbl = new JLabel();
    btnPackOrder = new JButton(PACKED);
    orderProductList = new JList<>();
    orderScrollPane  = new JScrollPane();
  }

  /**
   * Sets up the look and feel of our page controls
   */
  private void setupUI(Container contentContainer) {
    //Label Styling
    titleLbl.setBounds(10, 10, 365, 20);
    titleLbl.setText("Order packing system");
    contentContainer.add(titleLbl);

    actionTxtLbl.setBounds(10, 40, 365, 20);
    actionTxtLbl.setText("Waiting for new orders");
    contentContainer.add(actionTxtLbl);

    btnPackOrder.setBounds(10, 410, 150, 40);
    btnPackOrder.setEnabled(false);
    contentContainer.add(btnPackOrder);

    //List Styling
    orderProductList.setBounds(0, 0, 360, 220);
    orderProductList.setCellRenderer(new SearchRenderer());

    //ScrollPane Styling
    orderScrollPane.setBounds(10, 70, 365, 330);
    orderScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    orderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    orderScrollPane.getViewport().add(orderProductList);
    orderScrollPane.getViewport().setBackground(Color.YELLOW);
    contentContainer.add(orderScrollPane);
  }

  /**
   * Sets up all the event handlers for page controls
   */
  private void setupEventHandlers() {
    //Clear button event handler
    btnPackOrder.addActionListener(e -> {
      cont.doPacked();
      btnPackOrder.setEnabled(false);
    });
  }

  /**
   * The controller object, used so that an interaction can be passed to the controller
   * @param c The controller
   */
  public void setController(PackingController c) {
    cont = c;
  }

  /**
   * Update the view
   * @param modelP   The observed model
   * @param arg      Specific args 
   */
  @Override
  public void update(Observable modelP, Object arg )
  {
    PackingModel model = (PackingModel) modelP;
    String message = (String) arg;
    actionTxtLbl.setText(message);
    orderBasket = model.getBasket();

    if (orderBasket != null) {
      orderProductList.setListData(orderBasket.toArray(new Product[0]));
      btnPackOrder.setEnabled(true);
    } else {
      orderProductList.setListData(new Product[0]);
      btnPackOrder.setEnabled(false);
    }
  }

}

