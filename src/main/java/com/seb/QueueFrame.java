package com.seb;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class QueueFrame extends JFrame {

    JTable table;
    String header[] = new String[] {"title", "interpret", "length"};
    public DefaultTableModel dtm;

    public QueueFrame() {
        super("Queue Frame");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JPanel panel = new JPanel();
        table = new JTable();
        table.addColumn(new TableColumn(0));
        table.addColumn(new TableColumn(1));
        TableColumn column = new TableColumn(2);
        column.setWidth(50);
        table.addColumn(column);
        panel.add(new JScrollPane(table));
        this.add(panel);
        //this.setSize(500, 300);
        table.setDefaultEditor(Object.class, null);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    JSONArray delete = new JSONArray();
                    for (int i : table.getSelectedRows()) {
                        delete.put(i);
                        dtm.removeRow(i);
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("delete", delete);
                    ConnectButton.out.println(obj);
                }
            }
        });
        dtm = new DefaultTableModel(0, 0);
        dtm.setColumnIdentifiers(header);
        table.setModel(dtm);
        this.pack();
    }

    public void updateTable(JSONObject data) {
        if (data.has("clear")) {
            dtm.getDataVector().removeAllElements();
        }
        if (data.has("insert")) {
            JSONObject obj = data.getJSONObject("insert");
            for (String s : obj.keySet()) {
                JSONObject insObj = obj.getJSONObject(s);
                dtm.insertRow(Integer.parseInt(s), new Object[] {
                        insObj.getString("title"), insObj.getString("author"), insObj.getString("duration")});
                insObj.clear();
            }

            obj.clear();
        }
        if (data.has("queue")) {
            JSONArray queue = data.optJSONArray("queue");
            for (Object o : queue) {
                dtm.addRow(new Object[]{
                        ((JSONObject) o).getString("title"), ((JSONObject) o).getString("author"), ((JSONObject) o).getString("duration")
                });
            }
            queue.clear();
        }
        if (data.has("next")) {
            dtm.removeRow(0);
        }
        data.clear();
        this.pack();
    }
}
