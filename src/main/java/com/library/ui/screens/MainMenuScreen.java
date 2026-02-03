package com.library.ui.screens;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.library.ui.components.MenuBuilder;
import com.library.ui.screens.MemberManagementScreen;

public class MainMenuScreen extends BasicWindow {
    public MainMenuScreen(MultiWindowTextGUI gui) {
        super("Main Menu");
        setHints(java.util.List.of(Hint.CENTERED));

        Panel content = MenuBuilder.createVerticalMenu(gui, "Main Menu");

        content.addComponent(new Button("Books Management", () -> {
            MessageDialog.showMessageDialog(gui, "Info", "Opening Books Management...");
        }));

        content.addComponent(new Button("Member Directory", () -> {
            MemberManagementScreen memberManagementScreen = new MemberManagementScreen(gui);
            gui.addWindow(memberManagementScreen);
        }));

        content.addComponent(new Button("System Reports", () -> {
             MessageDialog.showMessageDialog(gui, "Reports", "Generating CSV reports in root...");
        }));

        content.addComponent(new EmptySpace());
        content.addComponent(new Button("Exit System", this::close));

        setComponent(content);
    }
}