package com.library.ui.screens;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
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
    private final Label summaryLabel = new Label("");
    private final Table<String> table;

    public MemberManagementScreen(MultiWindowTextGUI gui) {
        super("Member Directory", gui);
        Panel content = createVerticalContent();

        content.addComponent(createScreenHeader(
                "Member Directory",
                "Track registered members and update their contact details quickly."
        ));
        content.addComponent(createVerticalGap());

        content.addComponent(summaryLabel.addStyle(SGR.BOLD));
        content.addComponent(createVerticalGap());

        this.table = TableBuilder.createEntityTable("Members", "ID", "Full Name", "Email", "Join Date");
        this.table.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        refreshTable();
        this.table.setSelectAction(this::openEditMemberFromSelection);

        content.addComponent(table);
        content.addComponent(createVerticalGap());
        content.addComponent(createActionRow(
                new Button("Register New Member", this::openAddMemberForm),
                new Button("Back", this::close)
        ));
        content.addComponent(createHintLabel("List controls: UP/DOWN navigate, ENTER edits selected member, ESC closes this screen."));

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
        summaryLabel.setText("Members: " + members.size() + "  |  Press ENTER on a row to edit");
    }

    private void openAddMemberForm() {
        final BasicWindow formWindow = new BasicWindow("Register Member");
        formWindow.setHints(List.of(Window.Hint.CENTERED));
        FormBuilder fb = new FormBuilder();
        fb.addField("Full Name:", "name");
        fb.addField("Email:", "email");

        Panel panel = fb.getPanel();
        panel.addComponent(createActionRow(
                new Button("Save", () -> {
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
                }),
                new Button("Cancel", formWindow::close)
        ));
        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }

    private void openEditMemberFromSelection() {
        if (table.getTableModel().getRowCount() == 0) {
            MessageDialog.showMessageDialog(gui, "Member Directory", "No members available to edit.");
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= table.getTableModel().getRowCount()) {
            MessageDialog.showMessageDialog(gui, "Member Directory", "Select a member row first.");
            return;
        }

        String memberIdCell = table.getTableModel().getRow(selectedRow).get(0);
        try {
            Long memberId = Long.parseLong(memberIdCell);
            Member member = memberDAO.findById(memberId);
            if (member == null) {
                MessageDialog.showMessageDialog(gui, "Member Directory", "Selected member no longer exists.");
                return;
            }
            openEditMemberForm(member);
        } catch (NumberFormatException ex) {
            MessageDialog.showMessageDialog(gui, "Member Directory", "Selected row has an invalid Member ID.");
        }
    }

    private void openEditMemberForm(Member member) {
        final BasicWindow formWindow = new BasicWindow("Edit Member");
        formWindow.setHints(List.of(Window.Hint.CENTERED));
        FormBuilder fb = new FormBuilder();
        fb.addField("Full Name:", "name");
        fb.addField("Email:", "email");
        fb.setValue("name", member.getFullName());
        fb.setValue("email", member.getEmail());

        Panel panel = fb.getPanel();
        panel.addComponent(createHintLabel("Member ID: " + member.getId()));
        panel.addComponent(createHintLabel("Join Date: " + member.getJoinDate()));
        panel.addComponent(createActionRow(
                new Button("Save", () -> {
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

                    member.setFullName(fullName);
                    member.setEmail(email);
                    memberDAO.update(member);
                    refreshTable();
                    formWindow.close();
                }),
                new Button("Cancel", formWindow::close)
        ));

        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }
}
