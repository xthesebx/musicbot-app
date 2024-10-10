package com.seb;

import javax.swing.table.DefaultTableModel;

public class CustomTableModel extends DefaultTableModel implements Reorderable {
    @Override
    public void reorder(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            this.moveRow(fromIndex, fromIndex, toIndex);
            ConnectButton.out.println("move " + fromIndex + " " + toIndex);
        } else {
            this.moveRow(fromIndex, fromIndex, toIndex - 1);
            ConnectButton.out.println("move " + fromIndex + " " + (toIndex - 1));
        }
    }

    public CustomTableModel(int s, int f) {
        super(s, f);
    }
}
