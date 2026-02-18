package com.library.ui.screens;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowListenerAdapter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractScreenWindow extends BasicWindow {
    protected final MultiWindowTextGUI gui;

    protected AbstractScreenWindow(String title, MultiWindowTextGUI gui) {
        super(title);
        this.gui = gui;
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIT_TERMINAL_WINDOW));
        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window window, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    window.close();
                }
            }
        });
    }

    protected Panel createVerticalContent() {
        return new Panel(new LinearLayout(Direction.VERTICAL));
    }

    protected Panel createActionRow(Button... buttons) {
        Panel row = new Panel(new LinearLayout(Direction.HORIZONTAL));
        for (int i = 0; i < buttons.length; i++) {
            row.addComponent(buttons[i]);
            if (i < buttons.length - 1) {
                row.addComponent(new EmptySpace(new TerminalSize(2, 1)));
            }
        }
        return row;
    }

    protected Label createHintLabel(String text) {
        return new Label(text).addStyle(SGR.BOLD);
    }
}
