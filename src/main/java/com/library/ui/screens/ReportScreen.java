package com.library.ui.screens;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.concurrent.atomic.AtomicBoolean;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.library.service.ReportGenerator;

public class ReportScreen extends BasicWindow {
    private final ReportGenerator reportGenerator = new ReportGenerator();

    public ReportScreen(MultiWindowTextGUI gui) {
        super("System Reports");
        this.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window window, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    window.close();
                }
            }
        });
        Panel panel = new Panel(new LinearLayout());

        panel.addComponent(new Button("Export Inventory CSV", () -> {
            try {
                String file = reportGenerator.generateInventoryReport();
                MessageDialog.showMessageDialog(gui, "Success", "Exported to: " + file);
            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Error", e.getMessage());
            }
        }));

        panel.addComponent(new Button("Back", this::close));
        setComponent(panel);
    }
}