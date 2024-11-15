package clients.cashier;

import catalogue.Enums.SearchSelection;
import catalogue.Product;

/**
 * The Cashier Controller
 * @author Peter Blackburn
 */

public class CashierController {
    private CashierModel model = null;
    private CashierView view = null;

    /**
     * Constructor
     * @param model The model
     * @param view  The view from which the interaction came
     */
    public CashierController(CashierModel model, CashierView view) {
        this.view = view;
        this.model = model;
    }

    /**
     * Check interaction from view
     * @param search The product number or keyword to be searched
     */
    public void doCheck(String search) {
        switch (model.checkSearchSelection()) {
            case KEYWORD:
                model.searchByKeyword(search);
                break;
            case PRODUCT_NUMBER:
                model.searchByNumber(search);
                break;
        }
    }

    /**
     * Adds the selected product to the cart
     * @param product The product to be added to the order cart
     */
    public void addToCart(Product product) {
        model.addToBasket(product);
    }

    /**
     * Checks out the current order
     */
    public void checkoutOrder() {
        model.checkoutBasket();
    }

    /**
     * Removes the selected product from the order
     * @param product the product to be removed
     */
    public void removeFromCart(Product product) {
        model.removeFromCart(product);
    }

    /**
     * Set search type via radio button
     * @param searchType the searchType enum that has been selected
     */
    public void setSearchType(SearchSelection searchType) {
        model.setSearchSelection(searchType);
    }

}
