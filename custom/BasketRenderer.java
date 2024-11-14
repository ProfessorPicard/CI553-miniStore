package custom;

import catalogue.Product;
import clients.Picture;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BasketRenderer extends JPanel implements ListCellRenderer<Product> {

    public BasketRenderer() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Product> list, Product value, int index, boolean isSelected, boolean cellHasFocus) {

        for(Component c : getComponents()) {
            remove(c);
        }

        ImageIcon icon = new ImageIcon(value.getPictureURL());
        icon.setImage(icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        JLabel title = new JLabel(icon);
        title.setText(value.getDescription());
        add(title, BorderLayout.LINE_START);

        JLabel price = new JLabel();
        price.setText("£" + value.getPrice() + " | Qty: " + value.getQuantity());
        add(price, BorderLayout.LINE_END);

//        setIcon(icon);
//        setText(value.toString());
//        Picture picture = new Picture(80,80);
//        picture.set(new ImageIcon(value.getPictureURL()));
//        picture.setBounds(0,0, 80, 80);
//
//        JTextPane description = new JTextPane();
//        description.setText(value.getDescription());
//        description.setBounds(80,0, 200, 40);
//
//        JTextPane price = new JTextPane();
//        price.setText("" + value.getPrice());
//        price.setBounds(80,40, 100, 40);
//
//        JTextPane stock = new JTextPane();
//        stock.setText("" + value.getQuantity());
//        stock.setBounds(180,40, 100, 40);

//        this.add(picture);
//        this.add(description);
//        this.add(price);
//        this.add(stock);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }

}
