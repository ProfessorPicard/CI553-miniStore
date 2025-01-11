package clients.backDoor;


import catalogue.Enums.SearchSelection;
import catalogue.Product;

/**
 * The BackDoor Controller
 */

public class BackDoorController
{
  private BackDoorModel model = null;
  private BackDoorView view = null;
  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public BackDoorController( BackDoorModel model, BackDoorView view )
  {
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
   * RStock interaction from view
   * @param selectedProduct The product to be re-stocked
   * @param quantity The quantity to be re-stocked
   */
  public void doRStock(Product selectedProduct, String quantity )
  {
    model.doRStock(selectedProduct, quantity);
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

