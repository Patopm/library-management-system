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
                TextColor.ANSI.WHITE,
                TextColor.ANSI.BLACK,
                TextColor.ANSI.BLACK,
                TextColor.ANSI.BLACK,
                TextColor.ANSI.GREEN,
                TextColor.ANSI.BLACK,
                TextColor.ANSI.WHITE
        );

        this.gui = new MultiWindowTextGUI(
                screen,
                new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLACK)
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
