package com.library.ui.screens;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.library.ui.components.MenuBuilder;

public class MainMenuScreen extends BasicWindow {
    public MainMenuScreen(MultiWindowTextGUI gui) {
        super("Library Management System");
        setHints(java.util.List.of(Hint.FULL_SCREEN));

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL));
        content.addComponent(new EmptySpace());

        Panel menuPanel = MenuBuilder.createVerticalMenu(
                "Library Management System",
                "Manage books, members, loans, and reports from one place."
        );

        menuPanel.addComponent(new Button("Books Management", () -> gui.addWindowAndWait(new BookManagementScreen(gui))));
        menuPanel.addComponent(new Button("Member Directory", () -> gui.addWindowAndWait(new MemberManagementScreen(gui))));
        menuPanel.addComponent(new Button("Loan Operations", () -> gui.addWindowAndWait(new LoanManagementScreen(gui))));
        menuPanel.addComponent(new Button("System Reports", () -> gui.addWindowAndWait(new ReportScreen(gui))));
        menuPanel.addComponent(new EmptySpace());
        menuPanel.addComponent(new Button("Exit System", this::close));

        content.addComponent(menuPanel);
        content.addComponent(new EmptySpace());
        content.addComponent(new Label("Use TAB to move focus, ENTER to select, ESC to close windows."));

        Panel wrapper = new Panel(new LinearLayout(Direction.HORIZONTAL));
        wrapper.addComponent(new EmptySpace());
        wrapper.addComponent(content);
        wrapper.addComponent(new EmptySpace());
        setComponent(wrapper);
    }
}
