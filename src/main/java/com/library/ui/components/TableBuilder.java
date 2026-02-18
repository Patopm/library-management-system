package com.library.ui.components;

import com.googlecode.lanterna.gui2.table.Table;

public final class TableBuilder {
    private TableBuilder() {
    }

    public static Table<String> createEntityTable(String title, String... headers) {
        Table<String> table = new Table<>(headers);
        table.setSelectAction(() -> {}); // Placeholder for selection logic
        return table;
    }
}
