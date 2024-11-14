package clients.cashier;

import catalogue.Basket;
import catalogue.BetterBasket;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
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
import java.sql.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * View of the model 
 */
public class CashierView implements Observer
{
  private static final int H = 500;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels
  
  private static final String CHECK  = "Search";
  private static final String BUY    = "Add";
  private static final String BOUGHT = "Checkout";

  private final JLabel        pageTitle  = new JLabel();
  private final JLabel        theAction  = new JLabel();

  private final JTabbedPane   sections   = new JTabbedPane();
  private final JPanel        basket     = new JPanel();
  private final JPanel        search     = new JPanel();

  private final JRadioButton  productNoRadio = new JRadioButton("Product Number");
  private final JRadioButton  keywordSearch = new JRadioButton("Keyword Search");
  private final ButtonGroup   searchGroup = new ButtonGroup();

  private final JList<Product> jList     = new JList<>();

  private Integer[] quantityValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

  private final JTextField             theInput   = new JTextField();
  private final JTextArea              theOutput  = new JTextArea();
  private final JScrollPane            theSP      = new JScrollPane();
  private final JButton                btnCheck = new JButton( CHECK );
  private final JButton                btnAddToCart = new JButton( BUY );
  private final JButton                btnCheckout = new JButton( BOUGHT );
  private final JComboBox<Integer>     quantity = new JComboBox<>(quantityValues);

  private ArrayList<Product> searchBasket = new ArrayList<>();
  private Product selectedProduct = null;

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
          
  public CashierView(  RootPaneContainer rpc,  MiddleFactory mf, int x, int y  )
  {
    try                                           // 
    {      
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      System.out.println("Exception: " + e.getMessage() );
    }
    Container cp         = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout(null);                             // No layout manager
    rootWindow.setSize( W, H );                     // Size of Window
    rootWindow.setLocation( x, y );

    search.setLayout(new GroupLayout(search));
    basket.setLayout(new GroupLayout(basket));

    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is

    pageTitle.setBounds( 10, 10 , 380, 20 );
    pageTitle.setText( "Thank You for Shopping at Mini-Store" );
    cp.add( pageTitle );

    theAction.setBounds( 10, 40 , 380, 20 );       // Message area
    theAction.setText( "" );                        // Blank
    cp.add(theAction);                            //  Add to canvas

    basket.setBackground(Color.BLUE);
    search.setBackground(Color.RED);

    sections.add("Basket", basket);
    sections.add("Search", search);

    sections.setBounds(10, 70, 365, 380);
    cp.add(sections);

    theInput.setBounds( 10, 10, 340, 40 );         // Input Area
    theInput.setText("");                                          // Blank
    search.add(theInput);                                        // Add to canvas

    btnCheck.setBounds( 10, 55, 80, 40 );    // Check Button
    btnCheck.addActionListener(                   // Call back code
      e -> cont.doCheck(theInput.getText()));
    search.add(btnCheck);                           //  Add to canvas

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
    productNoRadio.setSelected(false);
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

    search.add(productNoRadio);
    search.add(keywordSearch);

    btnAddToCart.setBounds( 10, 300, 100, 40 );      // Buy button
    btnAddToCart.setEnabled(false);
    btnAddToCart.addActionListener(                     // Call back code
      e -> cont.doBuy(selectedProduct) );
    search.add(btnAddToCart);                             //  Add to canvas

    quantity.setBounds(120, 300, 100, 40);
    quantity.setEnabled(false);
    quantity.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent event) {
        int state = event.getStateChange();
        if (state == ItemEvent.SELECTED) {
          if(selectedProduct != null)
            selectedProduct.setQuantity((Integer) quantity.getSelectedItem());
        }
      }
    });
    search.add(quantity);

    btnCheckout.setBounds( 230, 300, 100, 40 );   // Bought Button
    btnCheckout.setEnabled(false);
    btnCheckout.addActionListener(                  // Call back code
      e -> cont.doBought());
//    search.add(btnCheckout);                          //  Add to canvas

    theSP.setBounds( 10, 100, 340, 190 );          // Scrolling pane

    theSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    theSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    jList.setBounds(0, 0, 330, 180);
    jList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent event) {

        btnAddToCart.setEnabled(!jList.isSelectionEmpty());
        quantity.setEnabled(!jList.isSelectionEmpty());
        quantity.setSelectedIndex(0);

        if (!event.getValueIsAdjusting()) {
          JList source = (JList) event.getSource();
          if(source.getSelectedIndex() == -1)
            selectedProduct = null;
          else {
            Product p = (Product) source.getSelectedValue();
            selectedProduct = new Product(p.getProductNum(), p.getDescription(), p.getPrice(), (Integer) quantity.getSelectedItem());
            DEBUG.trace(selectedProduct.getDescription());
          }
        }
      }
    });

    theOutput.setBounds(10, 10, 365, 380);
    theOutput.setText("");                        //  Blank
    theOutput.setFont(f);                         //  Uses font
    basket.add(theOutput);

    theSP.getViewport().setBackground(Color.YELLOW);


    theSP.getViewport().add(jList);           //  In TextArea
    search.add(theSP);                                //  Add to canvas


    rootWindow.setVisible( true );                  // Make visible
    theInput.requestFocus();                        // Focus is here
  }

  /**
   * The controller object, used so that an interaction can be passed to the controller
   * @param c   The controller
   */

  public void setController( CashierController c )
  {
    cont = c;
  }

  /**
   * Update the view
   * @param modelC   The observed model
   * @param arg      Specific args 
   */
  @Override
  public void update( Observable modelC, Object arg )
  {
    CashierModel model  = (CashierModel) modelC;
    String      message = (String) arg;
    theAction.setText( message );
//    ArrayList<Product> basket = model.getBasket();
    searchBasket = model.getSearchBasket();
    jList.setListData(searchBasket.toArray(new Product[0]));
//    if ( basket == null )
//      theOutput.setText( "Customers order" );
//    else
//      theOutput.setText( basket.getDetails() );
    
    theInput.requestFocus();               // Focus is here
  }

}
