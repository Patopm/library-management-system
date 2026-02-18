package com.library.ui.screens;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.library.dao.MemberDAO;
import com.library.model.Member;
import com.library.ui.components.TableBuilder;
import com.library.ui.components.FormBuilder;

import java.util.List;

public class MemberManagementScreen extends AbstractScreenWindow {
    private final MemberDAO memberDAO = new MemberDAO();
    private final Table<String> table;

    public MemberManagementScreen(MultiWindowTextGUI gui) {
        super("Member Directory", gui);
        Panel content = createVerticalContent();
        
        this.table = TableBuilder.createEntityTable("Members", "ID", "Full Name", "Email", "Join Date");
        refreshTable();

        content.addComponent(table);
        content.addComponent(createActionRow(
                new Button("Register New Member", this::openAddMemberForm),
                new Button("Back", this::close)
        ));
        content.addComponent(createHintLabel("Press ESC to return to main menu."));

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
        formWindow.setHints(List.of(Window.Hint.CENTERED));
        FormBuilder fb = new FormBuilder();
        fb.addField("Full Name:", "name");
        fb.addField("Email:", "email");

        Panel panel = fb.getPanel();
        panel.addComponent(new Button("Save", () -> {
            String fullName = fb.getValue("name");
            String email = fb.getValue("email");

            if (fullName.isBlank() || email.isBlank()) {
                MessageDialog.showMessageDialog(gui, "Validation", "Name and email are required.");
                return;
            }
            if (!email.contains("@")) {
                MessageDialog.showMessageDialog(gui, "Validation", "Email format looks invalid.");
                return;
            }

            Member m = new Member();
            m.setFullName(fullName);
            m.setEmail(email);
            memberDAO.save(m);
            refreshTable();
            formWindow.close();
        }));
        
        panel.addComponent(new Button("Cancel", formWindow::close));
        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }
}
