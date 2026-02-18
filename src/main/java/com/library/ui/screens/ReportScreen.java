package com.library.ui.screens;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.library.service.ReportGenerator;

public class ReportScreen extends AbstractScreenWindow {
    private final ReportGenerator reportGenerator = new ReportGenerator();

    public ReportScreen(MultiWindowTextGUI gui) {
        super("System Reports", gui);
        Panel panel = createVerticalContent();

        panel.addComponent(new Button("Export Inventory CSV", () -> {
            try {
                String file = reportGenerator.generateInventoryReport();
                MessageDialog.showMessageDialog(gui, "Success", "Exported to: " + file);
            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Error", e.getMessage());
            }
        }));

        panel.addComponent(new Button("Export Loans CSV", () -> {
            try {
                String file = reportGenerator.generateLoanReport();
                MessageDialog.showMessageDialog(gui, "Success", "Exported to: " + file);
            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Error", e.getMessage());
            }
        }));

        panel.addComponent(createActionRow(new Button("Back", this::close)));
        panel.addComponent(createHintLabel("Press ESC to return to main menu."));
        setComponent(panel);
    }
}
