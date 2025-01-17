package clients.customer;

import catalogue.BetterBasket;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
import custom.SearchRenderer;
import middle.MiddleFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Customer view.
 */

public class CustomerView implements Observer {

    //Page Dimensions
    private static final int H = 500;       // Height of window pixels
    private static final int W = 400;       // Width  of window pixels

    //Page Labels
    private static final String SEARCH_BTN = "Search";
    private static final String CLEAR_BTN = "Clear";
    private static final String PRODUCT_NUMBER_RADIO = "Product Number";
    private static final String KEYWORD_RADIO = "Keyword Search";

    //Search Page Controls
    private JLabel titleLabel;
    private JLabel actionTxtLabel;
    private JTextField searchInput;
    private JScrollPane searchScrollPane;
    private JButton btnSearch;
    private JButton btnClear;
    private JList<Product> searchList;
    private JRadioButton productNoRadio;
    private JRadioButton keywordSearch;
    private ButtonGroup searchGroup;

    //Basket for search
    private BetterBasket searchBasket;

    //Customer controller
    private CustomerController cont = null;

    /**
     * Construct the view
     *
     * @param rpc Window in which to construct
     * @param mf  Factor to deliver order and stock objects
     * @param x   x-cordinate of position of window on screen
     * @param y   y-cordinate of position of window on screen
     */
    public CustomerView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {

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
        searchScrollPane = new JScrollPane();
        btnSearch = new JButton(SEARCH_BTN);
        btnClear = new JButton(CLEAR_BTN);
        searchList = new JList<>();
        productNoRadio = new JRadioButton(PRODUCT_NUMBER_RADIO);
        keywordSearch = new JRadioButton(KEYWORD_RADIO);
        searchGroup = new ButtonGroup();
    }

    /**
     * Sets up the look and feel of our page controls
     */
    private void setupUI(Container contentContainer) {
        //Label Styling
        titleLabel.setBounds(10, 10, 365, 20);
        titleLabel.setText("Thank You for Shopping at Mini-Store");
        contentContainer.add(titleLabel);

        actionTxtLabel.setBounds(10, 40, 365, 20);
        actionTxtLabel.setText("");
        contentContainer.add(actionTxtLabel);

        //Input Text Styling
        searchInput.setBounds(10, 70, 365, 40);
        searchInput.setText("");
        contentContainer.add(searchInput);

        btnSearch.setBounds(10, 120, 80, 40);
        contentContainer.add(btnSearch);

        btnClear.setBounds(10, 410, 80, 40);
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
      });

      //Search button event handler
      btnSearch.addActionListener(e -> {
        cont.doSearch(searchInput.getText());
      });

    }

    /**
     * The controller object, used so that an interaction can be passed to the controller
     * @param c The controller
     */
    public void setController(CustomerController c) {
        cont = c;
    }

    /**
     * Update the view
     * @param modelC The observed model
     * @param arg    Specific args
     */
    public void update(Observable modelC, Object arg) {
        CustomerModel model = (CustomerModel) modelC;
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
