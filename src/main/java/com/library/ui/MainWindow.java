package com.library.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.graphics.SimpleTheme.Definition;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.library.ui.screens.MainMenuScreen;

import java.util.Arrays;

public class MainWindow {
    private final Screen screen;
    private final MultiWindowTextGUI gui;

    public MainWindow() throws Exception {
        this.screen = new DefaultTerminalFactory().createScreen();
        this.screen.startScreen();

        TextColor baseForeground = TextColor.Factory.fromString("#D6DBF0");
        TextColor baseBackground = TextColor.Factory.fromString("#1F2540");
        TextColor accentBackground = TextColor.Factory.fromString("#33406B");
        TextColor buttonFocusForeground = TextColor.Factory.fromString("#2A120B");
        TextColor buttonFocusBackground = TextColor.Factory.fromString("#F3B6A4");
        TextColor tableFocusForeground = TextColor.Factory.fromString("#1E0F09");
        TextColor tableFocusBackground = TextColor.Factory.fromString("#FFC6B5");
        TextColor actionForeground = TextColor.Factory.fromString("#101522");
        TextColor actionBackground = TextColor.Factory.fromString("#8BD3FF");
        SimpleTheme customTheme = new SimpleTheme(baseForeground, baseBackground);
        customTheme.addOverride(Label.class, baseForeground, baseBackground);
        customTheme.addOverride(Panel.class, baseForeground, baseBackground);
        customTheme.addOverride(TextBox.class, TextColor.ANSI.WHITE, accentBackground);

        Definition buttonTheme = customTheme.addOverride(Button.class, actionForeground, actionBackground);
        buttonTheme
                .setActive(buttonFocusForeground, buttonFocusBackground, SGR.BOLD)
                .setSelected(buttonFocusForeground, buttonFocusBackground, SGR.BOLD)
                .setPreLight(buttonFocusForeground, buttonFocusBackground, SGR.BOLD);

        Definition tableTheme = customTheme.addOverride(
                com.googlecode.lanterna.gui2.table.Table.class,
                baseForeground,
                baseBackground
        );
        tableTheme
                .setActive(tableFocusForeground, tableFocusBackground, SGR.BOLD)
                .setSelected(tableFocusForeground, tableFocusBackground, SGR.BOLD)
                .setPreLight(tableFocusForeground, tableFocusBackground, SGR.BOLD);

        this.gui = new MultiWindowTextGUI(
                screen,
                new DefaultWindowManager(),
                new EmptySpace(baseBackground)
        );
        
        this.gui.setTheme(customTheme);
    }

    public void start() {
        MainMenuScreen mainMenu = new MainMenuScreen(gui);
        mainMenu.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        
        gui.addWindowAndWait(mainMenu);
        
        try {
            screen.stopScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
