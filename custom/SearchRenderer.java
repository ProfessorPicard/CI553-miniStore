package custom;

import catalogue.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchRenderer extends AbstractRenderer {

    public SearchRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Product> list, Product value, int index, boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        ImageIcon icon = new ImageIcon(value.getPictureURL());
        icon.setImage(icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        JLabel title = new JLabel(icon);
        title.setText(value.getDescription() + " (" + value.getQuantity() + " in stock)");
        add(title, BorderLayout.LINE_START);

        JLabel price = new JLabel();
        price.setText("£" + value.getPrice() + " each");
        add(price, BorderLayout.LINE_END);

        return this;
    }

}
