package clients.cashier;

import catalogue.Enums.SearchSelection;
import catalogue.Product;
import custom.BasketRenderer;
import custom.SearchRenderer;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * View of the model 
 */
public class CashierView implements Observer {

  private static final int H = 500;
  private static final int W = 400;
  
  private static final String  CHECK  = "Search";
  private static final String  BUY    = "Add";
  private static final String  BOUGHT = "Checkout";
  private static final String  REMOVE = "Remove";

  private final JLabel         pageTitle  = new JLabel();
  private final JLabel         theAction  = new JLabel();

  private final JTabbedPane    sections   = new JTabbedPane();
  private final JPanel         basketPanel = new JPanel();
  private final JPanel         searchPanel = new JPanel();

  private final JRadioButton   productNoRadio = new JRadioButton("Product Number");
  private final JRadioButton   keywordSearch = new JRadioButton("Keyword Search");
  private final ButtonGroup    searchGroup = new ButtonGroup();

  private final JList<Product> searchList = new JList<>();
  private final JList<Product> basketList = new JList<>();

//  private final Integer[] quantityValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

  private final JTextField             theInput = new JTextField();
  private final JScrollPane            searchSP = new JScrollPane();
  private final JScrollPane            basketSP = new JScrollPane();
  private final JButton                btnCheck = new JButton(CHECK);
  private final JButton                btnAddToCart = new JButton(BUY);
  private final JButton                btnCheckout = new JButton(BOUGHT);
  private final JButton                btnRemove = new JButton(REMOVE);
  private JComboBox<Integer>     quantity = new JComboBox<>();

  private ArrayList<Product> searchBasket = new ArrayList<>();
  private ArrayList<Product> orderBasket = new ArrayList<>();

  private Product searchSelectedProduct = null;
  private Product basketSelectedProduct = null;

  private StockReadWriter     theStock     = null;
  private OrderProcessing     theOrder     = null;
  private CashierController   cont       = null;
  
  /**
   * Construct the view
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-coordinate of position of window on screen 
   * @param y     y-coordinate of position of window on screen  
   */
  public CashierView(  RootPaneContainer rpc,  MiddleFactory mf, int x, int y  ) {
    try                                           // 
    {
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      System.out.println("Exception: " + e.getMessage() );
    }
    Container cp = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout(null);                             // No layout manager
    rootWindow.setSize( W, H );                     // Size of Window
    rootWindow.setLocation( x, y );

    searchPanel.setLayout(new GroupLayout(searchPanel));
    basketPanel.setLayout(new GroupLayout(basketPanel));

    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is

    pageTitle.setBounds( 10, 10 , 380, 20 );
    pageTitle.setText( "Thank You for Shopping at Mini-Store" );
    cp.add( pageTitle );

    theAction.setBounds( 10, 40 , 380, 20 );       // Message area
    theAction.setText( "" );                        // Blank
    cp.add(theAction);                            //  Add to canvas

    basketPanel.setBackground(Color.BLUE);
    searchPanel.setBackground(Color.RED);

    sections.add("Basket", basketPanel);
    sections.add("Search", searchPanel);

    sections.setBounds(10, 70, 365, 380);
    cp.add(sections);

    theInput.setBounds( 10, 10, 340, 40 );         // Input Area
    theInput.setText("");                                          // Blank
    searchPanel.add(theInput);                                        // Add to canvas

    btnCheck.setBounds( 10, 55, 80, 40 );    // Check Button
    btnCheck.addActionListener(                   // Call back code
      e -> cont.doCheck(theInput.getText()));
    searchPanel.add(btnCheck);                           //  Add to canvas

    productNoRadio.setBounds(100, 55, 120, 40);
    productNoRadio.setSelected(true);
    productNoRadio.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent event) {
        if(cont != null) {
          int state = event.getStateChange();
          if (state == ItemEvent.SELECTED) {
            cont.setSearchType(SearchSelection.PRODUCT_NUMBER);
          } else if (state == ItemEvent.DESELECTED) {
            cont.setSearchType(SearchSelection.KEYWORD);
          }
        }
      }
    });
    keywordSearch.setBounds(230, 55, 120, 40);
    productNoRadio.setSelected(true);
    keywordSearch.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent event) {
        if(cont != null) {
          int state = event.getStateChange();
          if (state == ItemEvent.SELECTED) {
            cont.setSearchType(SearchSelection.KEYWORD);
          } else if (state == ItemEvent.DESELECTED) {
            cont.setSearchType(SearchSelection.PRODUCT_NUMBER);
          }
        }
      }
    });

    searchGroup.add(productNoRadio);
    searchGroup.add(keywordSearch);

    searchPanel.add(productNoRadio);
    searchPanel.add(keywordSearch);

    btnAddToCart.setBounds( 10, 300, 100, 40 );      // Buy button
    btnAddToCart.setEnabled(false);
    btnAddToCart.addActionListener(                     // Call back code
      e -> cont.doBuy(searchSelectedProduct) );
    searchPanel.add(btnAddToCart);                             //  Add to canvas

    quantity.setBounds(120, 300, 100, 40);
    quantity.setEnabled(false);
    quantity.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent event) {
        int state = event.getStateChange();
        if (state == ItemEvent.SELECTED) {
          if(searchSelectedProduct != null)
            searchSelectedProduct.setQuantity((Integer) quantity.getSelectedItem());
        }
      }
    });
    searchPanel.add(quantity);

    searchList.setBounds(0, 0, 330, 180);
    searchList.setCellRenderer(new SearchRenderer());
    searchList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent event) {


        if (!event.getValueIsAdjusting()) {
          JList source = (JList) event.getSource();
          if(source.getSelectedIndex() == -1)
            searchSelectedProduct = null;
          else {
            Product p = (Product) source.getSelectedValue();
            searchSelectedProduct = new Product(p.getProductNum(), p.getDescription(), p.getPrice(), p.getQuantity());
            searchSelectedProduct.setPictureURL(p.getPictureURL());
            DEBUG.trace(searchSelectedProduct.getDescription());
          }
        }

        if(searchSelectedProduct != null) {

          btnAddToCart.setEnabled(!searchList.isSelectionEmpty() && searchSelectedProduct.getQuantity() > 0);
          quantity.removeAllItems();

          if(searchSelectedProduct.getQuantity() > 0) {
            int val = Math.min(10, searchSelectedProduct.getQuantity());
            for (int i = 0; i < val; i++) {
              quantity.addItem(i + 1);
            }
            quantity.setEnabled(true);
            quantity.setSelectedIndex(0);
          } else {
            quantity.setEnabled(false);
            quantity.setSelectedIndex(-1);
          }

        } else {
          btnAddToCart.setEnabled(false);
          quantity.setEnabled(false);
        }

      }
    });

    searchSP.setBounds( 10, 100, 340, 190 );          // Scrolling pane
    searchSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    searchSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    searchSP.getViewport().add(searchList);
    searchPanel.add(searchSP);

    basketList.setBounds(0, 0, 330, 270);
    basketList.setCellRenderer(new BasketRenderer());
    basketList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent event) {

//        btnCheckout.setEnabled(!basketList.isSelectionEmpty());
        btnRemove.setEnabled(!basketList.isSelectionEmpty());

        if (!event.getValueIsAdjusting()) {
          JList source = (JList) event.getSource();
          if(source.getSelectedIndex() == -1) {
            basketSelectedProduct = null;
          } else {
            Product p = (Product) source.getSelectedValue();
            basketSelectedProduct = new Product(p.getProductNum(), p.getDescription(), p.getPrice(), p.getQuantity());
            basketSelectedProduct.setPictureURL(p.getPictureURL());
            DEBUG.trace(basketSelectedProduct.getDescription());
          }
        }
      }
    });

    basketSP.setBounds( 10, 10, 340, 275 );          // Scrolling pane
    basketSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    basketSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    basketSP.getViewport().add(basketList);
    basketPanel.add(basketSP);

    btnCheckout.setBounds(10, 300, 100, 40);
    btnCheckout.setEnabled(false);
    btnCheckout.addActionListener(
            e -> {
                cont.doBought();
            });
    basketPanel.add(btnCheckout);

    btnRemove.setBounds(120, 300, 100, 40);
    btnRemove.setEnabled(false);
    btnRemove.addActionListener(
            e -> {
              cont.removeFromCart(basketSelectedProduct);
            });
    basketPanel.add(btnRemove);

    rootWindow.setVisible( true );
    theInput.requestFocus();
  }

  /**
   * The controller object, used so that an interaction can be passed to the controller
   * @param c   The controller
   */

  public void setController(CashierController c) {
    cont = c;
  }

  /**
   * Update the view
   * @param modelC   The observed model
   * @param arg      Specific args 
   */
  @Override
  public void update(Observable modelC, Object arg) {
    CashierModel model  = (CashierModel) modelC;
    String      message = (String) arg;
    theAction.setText( message );

    searchBasket = model.getSearchBasket();
    orderBasket = model.getBasket();

    if(searchBasket != null) {
      searchList.setListData(searchBasket.toArray(new Product[0]));
    } else {
      searchList.setListData(new Product[0]);
    }

    if(orderBasket != null) {
      basketList.setListData(orderBasket.toArray(new Product[0]));
      btnCheckout.setEnabled(!orderBasket.isEmpty());
    } else {
      basketList.setListData(new Product[0]);
      btnCheckout.setEnabled(false);
    }
    searchList.clearSelection();
    basketList.clearSelection();
  }
}
