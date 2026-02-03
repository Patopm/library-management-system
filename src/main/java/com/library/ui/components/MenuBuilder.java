package com.library.ui.components;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

public class MenuBuilder {
    public static Panel createVerticalMenu(MultiWindowTextGUI gui, String title) {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        
        panel.addComponent(new Label(title)
                .addStyle(SGR.BOLD)
                .addStyle(SGR.REVERSE));
        
        panel.addComponent(new Separator(Direction.HORIZONTAL)
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        
        return panel;
    }
}