package clients.customer;

import catalogue.Enums.SearchSelection;

/**
 * The Customer Controller
 * @author Peter Blackburn
 */

public class CustomerController {
  private CustomerModel model = null;
  private CustomerView  view  = null;

  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public CustomerController( CustomerModel model, CustomerView view ) {
    this.view  = view;
    this.model = model;
  }

  /**
   * Check interaction from view
   * @param search The product number or keyword to be searched
   */
  public void doSearch(String search) {
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
   * Clears search basket
   */
  public void clearSearch() {
    model.doClear();
  }

  /**
   * Set search type via radio button
   * @param searchType the searchType enum that has been selected
   */
  public void setSearchType(SearchSelection searchType) {
    model.setSearchSelection(searchType);
  }

}

