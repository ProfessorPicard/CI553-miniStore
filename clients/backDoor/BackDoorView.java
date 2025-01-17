package clients.backDoor;

import catalogue.BetterBasket;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
import clients.customer.CustomerController;
import clients.customer.CustomerModel;
import custom.SearchRenderer;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.StockReadWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Customer view.
 */

public class BackDoorView implements Observer
{
  //Page Labels
  private static final String SEARCH_BTN = "Search";
  private static final String CLEAR_BTN = "Clear";
  private static final String ADD_BTN  = "Add";
  private static final String PRODUCT_NUMBER_RADIO = "Product Number";
  private static final String KEYWORD_RADIO = "Keyword Search";

  //Page Dimensions
  private static final int H = 500;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels

  //Search Page Controls
  private JLabel titleLabel;
  private JLabel actionTxtLabel;
  private JTextField searchInput;
  private JTextField amountInput;
  private JScrollPane searchScrollPane;
  private JButton btnSearch;
  private JButton btnClear;
  private JButton btnAdd;
  private JList<Product> searchList;
  private JRadioButton productNoRadio;
  private JRadioButton keywordSearch;
  private ButtonGroup searchGroup;

  //Basket for search
  private BetterBasket searchBasket;

  //Backdoor controller
  private BackDoorController cont = null;
  private StockReadWriter theStock = null;

  /**
   * Construct the view
   *
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-cordinate of position of window on screen 
   * @param y     y-cordinate of position of window on screen  
   */
  public BackDoorView(RootPaneContainer rpc, MiddleFactory mf, int x, int y ) {
    try  {
      theStock = mf.makeStockReadWriter();          // Database access
    } catch ( Exception e ) {
      System.out.println("Exception: " + e.getMessage() );
    }

    Container contentContainer = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    contentContainer.setLayout(null);                             // No layout manager
    rootWindow.setSize(W, H);                     // Size of Window
    rootWindow.setLocation(x, y);

    initialiseControls();
    setupUI(contentContainer);
    setupEventHandlers();

    rootWindow.setVisible(true);                  // Make visible);
    searchInput.requestFocus();                        // Focus is here
  }

  /**
   * Initialises all of our page controls
   */
  private void initialiseControls() {
    titleLabel = new JLabel();
    actionTxtLabel = new JLabel();
    searchInput = new JTextField();
    amountInput = new JTextField();
    searchScrollPane = new JScrollPane();
    btnSearch = new JButton(SEARCH_BTN);
    btnClear = new JButton(CLEAR_BTN);
    btnAdd = new JButton(ADD_BTN);
    searchList = new JList<>();
    productNoRadio = new JRadioButton(PRODUCT_NUMBER_RADIO);
    keywordSearch = new JRadioButton(KEYWORD_RADIO);
    searchGroup = new ButtonGroup();
  }

  /**
   * Sets up all the event handlers for page controls
   */
  private void setupEventHandlers() {
//Keyword radio button event handler
    keywordSearch.addItemListener(event -> {
      if (cont != null) {
        int state = event.getStateChange();
        if (state == ItemEvent.SELECTED) {
          cont.setSearchType(SearchSelection.KEYWORD);
        } else if (state == ItemEvent.DESELECTED) {
          cont.setSearchType(SearchSelection.PRODUCT_NUMBER);
        }
      }
    });

    //Product Number radio button event handler
    productNoRadio.addItemListener(event -> {
      if (cont != null) {
        int state = event.getStateChange();
        if (state == ItemEvent.SELECTED) {
          cont.setSearchType(SearchSelection.PRODUCT_NUMBER);
        } else if (state == ItemEvent.DESELECTED) {
          cont.setSearchType(SearchSelection.KEYWORD);
        }
      }
    });

    //Clear button event handler
    btnClear.addActionListener(e -> {
      cont.clearSearch();
      btnAdd.setEnabled(false);
      amountInput.setText("");
      amountInput.setEnabled(false);
    });

    //Search button event handler
    btnSearch.addActionListener(e -> {
      cont.doSearch(searchInput.getText());
    });

    //Search JList event handler
    searchList.addListSelectionListener(event -> {

      if (!event.getValueIsAdjusting()) {
        JList source = (JList) event.getSource();
        if (source.getSelectedIndex() == -1)
          searchBasket.clearSelectedProduct();
        else {
          Product p = (Product) source.getSelectedValue();

          searchBasket.setSelectedProduct(
                  new Product(p.getProductNum(), p.getDescription(), p.getPrice(), p.getQuantity())
          );
          searchBasket.getSelectedProduct().setPictureURL(p.getPictureURL());
        }
      }

      Product selectedProduct = searchBasket.getSelectedProduct();
      if (selectedProduct != null) {
        btnAdd.setEnabled(true);
        amountInput.setEnabled(true);
      } else {
        btnAdd.setEnabled(false);
        amountInput.setEnabled(false);
      }
    });

    //Event listener for the add button
    btnAdd.addActionListener(e -> {
      String amount = amountInput.getText();
      cont.doRStock(searchBasket.getSelectedProduct(), amount);
      amountInput.setText("");
    });
  }

  /**
   * Sets up the look and feel of our page controls
   */
  private void setupUI(Container contentContainer) {
    //Label Styling
    titleLabel.setBounds(10, 10, 365, 20);
    titleLabel.setText("Backdoor stock system");
    contentContainer.add(titleLabel);

    actionTxtLabel.setBounds(10, 40, 365, 20);
    actionTxtLabel.setText("Search for product by product number or keyword");
    contentContainer.add(actionTxtLabel);

    //Input Text Styling
    searchInput.setBounds(10, 70, 365, 40);
    searchInput.setText("");
    contentContainer.add(searchInput);

    amountInput.setBounds(100, 410, 80, 40);
    amountInput.setEnabled(false);
    contentContainer.add(amountInput);

    //Button Styling
    btnSearch.setBounds(10, 120, 80, 40);
    contentContainer.add(btnSearch);

    btnAdd.setBounds(10, 410, 80, 40);
    btnAdd.setEnabled(false);
    contentContainer.add(btnAdd);

    btnClear.setBounds(190, 410, 80, 40);
    contentContainer.add(btnClear);

    //Radio Button Styling
    productNoRadio.setBounds(100, 120, 120, 40);
    productNoRadio.setSelected(true);

    keywordSearch.setBounds(230, 120, 120, 40);
    productNoRadio.setSelected(true);

    searchGroup.add(productNoRadio);
    searchGroup.add(keywordSearch);

    //List Styling
    searchList.setBounds(0, 0, 360, 220);
    searchList.setCellRenderer(new SearchRenderer());

    contentContainer.add(productNoRadio);
    contentContainer.add(keywordSearch);

    //ScrollPane Styling
    searchScrollPane.setBounds(10, 170, 365, 230);
    searchScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    searchScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    searchScrollPane.getViewport().add(searchList);
    searchScrollPane.getViewport().setBackground(Color.YELLOW);
    contentContainer.add(searchScrollPane);
  }

  /**
   * The controller object, used so that an interaction can be passed to the controller
   * @param c The controller
   */
  public void setController( BackDoorController c )
  {
    cont = c;
  }

  /**
   * Update the view
   * @param modelC The observed model
   * @param arg    Specific args
   */
  public void update(Observable modelC, Object arg) {
    BackDoorModel model = (BackDoorModel) modelC;
    String message = (String) arg;
    actionTxtLabel.setText(message);
    searchBasket = model.getBasket();

    if (searchBasket != null) {
      searchList.setListData(searchBasket.toArray(new Product[0]));
    } else {
      searchList.setListData(new Product[0]);
    }
  }

}