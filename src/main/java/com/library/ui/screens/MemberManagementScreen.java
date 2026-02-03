package com.library.ui.screens;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.concurrent.atomic.AtomicBoolean;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.library.dao.MemberDAO;
import com.library.model.Member;
import com.library.ui.components.TableBuilder;
import com.library.ui.components.FormBuilder;

import java.time.LocalDate;
import java.util.List;

public class MemberManagementScreen extends BasicWindow {
    private final MemberDAO memberDAO = new MemberDAO();
    private final MultiWindowTextGUI gui;
    private final Table<String> table;

    public MemberManagementScreen(MultiWindowTextGUI gui) {
        super("Member Directory");
        this.gui = gui;
        this.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window window, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    window.close();
                }
            }
        });

        Panel content = new Panel(new LinearLayout());
        
        this.table = TableBuilder.createEntityTable("Members", "ID", "Full Name", "Email", "Join Date");
        refreshTable();

        content.addComponent(table);
        content.addComponent(new Button("Register New Member", this::openAddMemberForm));
        content.addComponent(new Button("Back", this::close));

        setComponent(content);
    }

    private void refreshTable() {
        table.getTableModel().clear();
        List<Member> members = memberDAO.findAll();
        for (Member m : members) {
            table.getTableModel().addRow(
                m.getId().toString(),
                m.getFullName(),
                m.getEmail(),
                m.getJoinDate().toString()
            );
        }
    }

    private void openAddMemberForm() {
        final BasicWindow formWindow = new BasicWindow("Register Member");
        FormBuilder fb = new FormBuilder();
        fb.addField("Full Name:", "name");
        fb.addField("Email:", "email");

        Panel panel = fb.getPanel();
        panel.addComponent(new Button("Save", () -> {
            Member m = new Member();
            m.setFullName(fb.getValue("name"));
            m.setEmail(fb.getValue("email"));
            memberDAO.save(m);
            refreshTable();
            formWindow.close();
        }));
        
        panel.addComponent(new Button("Cancel", formWindow::close));
        formWindow.setComponent(panel);
        gui.addWindow(formWindow);
    }
}