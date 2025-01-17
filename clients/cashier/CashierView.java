package clients.cashier;

import catalogue.BetterBasket;
import catalogue.Enums.SearchSelection;
import catalogue.Product;
import custom.BasketRenderer;
import custom.SearchRenderer;
import debug.DEBUG;
import middle.MiddleFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;
import java.util.Observer;


/**
 * View of the model
 * @author Peter Blackburn
 */
public class CashierView implements Observer {

    //Page Dimensions
    private static final int H = 500;
    private static final int W = 400;

    //Static text assignments
    private static final String SEARCH_BTN = "Search";
    private static final String BUY_BTN = "Add";
    private static final String BOUGHT_BTN = "Checkout";
    private static final String REMOVE_BTN = "Remove";
    private static final String PRODUCT_NUMBER_RADIO = "Product Number";
    private static final String KEYWORD_RADIO = "Keyword Search";
    private static final String PRICE_HEADER = "Total: ";

    //Container controls
    private JTabbedPane sections;
    private JPanel basketPanel;
    private JPanel searchPanel;

    //Page Labels
    private JLabel pageTitle;
    private JLabel actionTxtLabel;

    //Search Page Controls
    private JRadioButton productNoRadio;
    private JRadioButton keywordSearch;
    private ButtonGroup searchGroup;
    private JList<Product> searchList;
    private JTextField searchInput;
    private JScrollPane searchSP;
    private JButton btnSearch;
    private JButton btnAddToCart;
    private JComboBox<Integer> quantity;

    //Basket Page Controls
    private JList<Product> basketList;
    private JScrollPane basketSP;
    private JButton btnCheckout;
    private JButton btnRemove;
    private JLabel totalPriceLabel;
    private JLabel priceHeader;

    //Baskets for search and ordering
    private BetterBasket searchBasket;
    private BetterBasket orderBasket;

    //Cashier controller
    private CashierController cont = null;

    /**
     * Construct the view
     *
     * @param rpc Window in which to construct
     * @param mf  Factor to deliver order and stock objects
     * @param x   x-coordinate of position of window on screen
     * @param y   y-coordinate of position of window on screen
     */
    public CashierView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {

        Container contentPanel = rpc.getContentPane();
        Container rootWindow = (Container) rpc;
        contentPanel.setLayout(null);
        rootWindow.setSize(W, H);
        rootWindow.setLocation(x, y);

        //Separate loading the page controls into 3 sections for improved readability and maintainability
        initialiseControls();
        setupUI(contentPanel);
        setupEventHandlers();

        rootWindow.setVisible(true);
        searchInput.requestFocus();
    }

    /**
     * Initialises all of our page controls
     */
    private void initialiseControls() {
        sections = new JTabbedPane();
        basketPanel = new JPanel();
        searchPanel = new JPanel();

        //Page Labels
        pageTitle = new JLabel();
        actionTxtLabel = new JLabel();

        //Search Page Controls
        productNoRadio = new JRadioButton(PRODUCT_NUMBER_RADIO);
        keywordSearch = new JRadioButton(KEYWORD_RADIO);
        searchGroup = new ButtonGroup();
        searchList = new JList<>();
        searchInput = new JTextField();
        searchSP = new JScrollPane();
        btnSearch = new JButton(SEARCH_BTN);
        btnAddToCart = new JButton(BUY_BTN);
        quantity = new JComboBox<>();

        //Basket Page Controls
        basketList = new JList<>();
        basketSP = new JScrollPane();
        btnCheckout = new JButton(BOUGHT_BTN);
        btnRemove = new JButton(REMOVE_BTN);
        priceHeader = new JLabel(PRICE_HEADER);
        totalPriceLabel = new JLabel("");
    }

    /**
     * Sets up the look and feel of our page controls
     */
    private void setupUI(Container contentPanel) {

        //Label Styling
        pageTitle.setBounds(10, 10, 380, 20);
        pageTitle.setText("Thank You for Shopping at Mini-Store");
        contentPanel.add(pageTitle);

        actionTxtLabel.setBounds(10, 40, 380, 20);
        actionTxtLabel.setText("");
        contentPanel.add(actionTxtLabel);

        priceHeader.setBounds(130, 245, 100, 40);
        priceHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        basketPanel.add(priceHeader);

        totalPriceLabel.setBounds(240, 245, 100, 40);
        totalPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        basketPanel.add(totalPriceLabel);

        //JPanel Styling
        basketPanel.setLayout(new GroupLayout(basketPanel));
        searchPanel.setLayout(new GroupLayout(searchPanel));

        //Tabbed Panel Styling
        sections.setBounds(10, 70, 365, 380);
        sections.add("Basket", basketPanel);
        sections.add("Search", searchPanel);
        contentPanel.add(sections);

        //Input Text Styling
        searchInput.setBounds(10, 10, 340, 40);
        searchInput.setText("");
        searchPanel.add(searchInput);

        //Radio Button Styling
        productNoRadio.setBounds(100, 55, 120, 40);
        productNoRadio.setSelected(true);

        keywordSearch.setBounds(230, 55, 120, 40);
        productNoRadio.setSelected(true);

        searchGroup.add(productNoRadio);
        searchGroup.add(keywordSearch);

        searchPanel.add(productNoRadio);
        searchPanel.add(keywordSearch);

        //Combobox Styling
        quantity.setBounds(120, 300, 100, 40);
        quantity.setEnabled(false);
        searchPanel.add(quantity);

        //List Styling
        searchList.setBounds(0, 0, 330, 180);
        searchList.setCellRenderer(new SearchRenderer());

        basketList.setBounds(0, 0, 330, 220);
        basketList.setCellRenderer(new BasketRenderer());

        //Scroll Pane Styling
        searchSP.setBounds(10, 100, 340, 190);
        searchSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        searchSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        searchSP.getViewport().add(searchList);
        searchPanel.add(searchSP);

        basketSP.setBounds(10, 10, 340, 225);
        basketSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        basketSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        basketSP.getViewport().add(basketList);
        basketPanel.add(basketSP);

        //BUTTON STYLING
        btnAddToCart.setBounds(10, 300, 100, 40);
        btnAddToCart.setEnabled(false);
        searchPanel.add(btnAddToCart);

        btnCheckout.setBounds(10, 300, 100, 40);
        btnCheckout.setEnabled(false);
        basketPanel.add(btnCheckout);

        btnRemove.setBounds(120, 300, 100, 40);
        btnRemove.setEnabled(false);
        basketPanel.add(btnRemove);

        btnSearch.setBounds(10, 55, 80, 40);
        searchPanel.add(btnSearch);
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

        //Add to cart button event handler
        btnAddToCart.addActionListener(e -> {
            cont.addToCart(searchBasket.getSelectedProduct());
        });

        //Search button event handler
        btnSearch.addActionListener(e -> {
            cont.doCheck(searchInput.getText());
        });

        //Remove from cart button event handler
        btnRemove.addActionListener(e -> {
            cont.removeFromCart(orderBasket.getSelectedProduct());
        });

        //Checkout button event handler
        btnCheckout.addActionListener(e -> {
            cont.checkoutOrder();
        });

        //Basket JList event handler
        basketList.addListSelectionListener(event -> {
            btnRemove.setEnabled(!basketList.isSelectionEmpty());

            if (!event.getValueIsAdjusting()) {
                JList source = (JList) event.getSource();
                if (source.getSelectedIndex() == -1) {
                    orderBasket.clearSelectedProduct();
                } else {
                    Product p = (Product) source.getSelectedValue();
                    orderBasket.setSelectedProduct(
                            new Product(p.getProductNum(), p.getDescription(), p.getPrice(), p.getQuantity())
                    );
                    orderBasket.getSelectedProduct().setPictureURL(p.getPictureURL());
                    DEBUG.trace(orderBasket.getSelectedProduct().getDescription());
                }
            }
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
                    DEBUG.trace(searchBasket.getSelectedProduct().getDescription());

                }
            }

            Product selectedProduct = searchBasket.getSelectedProduct();
            if (selectedProduct != null) {

                btnAddToCart.setEnabled(!searchList.isSelectionEmpty() && selectedProduct.getQuantity() > 0);
                quantity.removeAllItems();

                if (selectedProduct.getQuantity() > 0) {
                    int val = Math.min(10, selectedProduct.getQuantity());
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
        });

        //Quantity combobox event handler
        quantity.addItemListener(event -> {
            int state = event.getStateChange();
            if (state == ItemEvent.SELECTED) {
                Product selectedProduct = searchBasket.getSelectedProduct();
                if (selectedProduct != null) {
                    int quantityVal = (quantity.getSelectedItem() == null) ? 1 : (int) quantity.getSelectedItem();
                    selectedProduct.setQuantity(quantityVal);
                }
            }
        });
    }

    /**
     * The controller object, used so that an interaction can be passed to the controller
     *
     * @param c The controller
     */
    public void setController(CashierController c) {
        cont = c;
    }

    /**
     * Update the view
     *
     * @param modelC The observed model
     * @param arg    Specific args
     */
    @Override
    public void update(Observable modelC, Object arg) {
        CashierModel model = (CashierModel) modelC;
        String message = (String) arg;
        actionTxtLabel.setText(message);

        searchBasket = model.getSearchBasket();
        orderBasket = model.getBasket();

        if (searchBasket != null) {
            searchList.setListData(searchBasket.toArray(new Product[0]));
        } else {
            searchList.setListData(new Product[0]);
        }

        if (orderBasket != null) {
            basketList.setListData(orderBasket.toArray(new Product[0]));
            btnCheckout.setEnabled(!orderBasket.isEmpty());
            totalPriceLabel.setText("£" + orderBasket.getBasketTotalPrice());
        } else {
            basketList.setListData(new Product[0]);
            btnCheckout.setEnabled(false);
            totalPriceLabel.setText("£0.00");
        }
        searchList.clearSelection();
        basketList.clearSelection();


    }
}
