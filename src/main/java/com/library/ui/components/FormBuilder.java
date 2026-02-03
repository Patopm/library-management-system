package com.library.ui.components;

import com.googlecode.lanterna.gui2.*;
import java.util.HashMap;
import java.util.Map;

public class FormBuilder {
    private final Panel panel;
    private final Map<String, TextBox> fields = new HashMap<>();

    public FormBuilder() {
        this.panel = new Panel(new GridLayout(2));
    }

    public void addField(String label, String key) {
        panel.addComponent(new Label(label));
        TextBox textBox = new TextBox();
        panel.addComponent(textBox);
        fields.put(key, textBox);
    }

    public String getValue(String key) {
        return fields.get(key).getText();
    }

    public Panel getPanel() {
        return panel;
    }
}