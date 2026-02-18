package com.library.ui.components;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

public class MenuBuilder {
    private MenuBuilder() {
    }

    public static Panel createVerticalMenu(String title, String subtitle) {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label(title)
                .addStyle(SGR.BOLD)
                .addStyle(SGR.UNDERLINE));

        if (subtitle != null && !subtitle.isBlank()) {
            panel.addComponent(new Label(subtitle));
        }

        panel.addComponent(new Separator(Direction.HORIZONTAL)
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        return panel;
    }
}
