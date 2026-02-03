package com.library.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;
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

        Theme customTheme = SimpleTheme.makeTheme(
                true,
                TextColor.ANSI.WHITE,     // Foreground
                TextColor.ANSI.BLUE,      // Background
                TextColor.ANSI.CYAN,      // Editable background
                TextColor.ANSI.BLACK,     // Focus foreground
                TextColor.ANSI.YELLOW,    // Focus background
                TextColor.ANSI.BLUE,      // Post-renderer background
                TextColor.ANSI.DEFAULT    // Post-renderer foreground
        );

        this.gui = new MultiWindowTextGUI(
                screen,
                new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLUE)
        );
        
        this.gui.setTheme(customTheme);
    }

    public void start() {
        MainMenuScreen mainMenu = new MainMenuScreen(gui);
        mainMenu.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.FIT_TERMINAL_WINDOW));
        
        gui.addWindowAndWait(mainMenu);
        
        try {
            screen.stopScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}