package remote;

import catalogue.Product;
import middle.StockException;

import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Defines the RMI interface for read access to the stock object.
 * @author  Mike Smith University of Brighton
 * @author  Peter Blackburn
 * @version 2.0
 */

public interface RemoteStockR_I
       extends Remote
{
  boolean   exists(String number)
            throws RemoteException, StockException;

  //New option to search by space seperated search terms
  ArrayList<Product> searchProducts(String pSearch) throws RemoteException, StockException;

  Product   getDetails(String number)
            throws RemoteException, StockException;
  ImageIcon getImage(String number)
            throws RemoteException, StockException;
}

