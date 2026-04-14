package edp.controller;

import edp.model.RoomModel;
import edp.view.AdminRoomView;
import edp.view.FrontdeskRoomView;

import javax.swing.*;
import java.util.List;

public class AdminRoomController {

    private final AdminRoomView view;

    public AdminRoomController(AdminRoomView view) {
        this.view = view;
        initActions();
        loadRooms();
        generateNextId();
    }

    private void generateNextId() {
        view.txtRoomId.setText(RoomModel.generateRoomId());
    }

    private void initActions() {

        // ADD
        view.btnAdd.addActionListener(e -> {
            try {
                RoomModel room = new RoomModel(
                    view.txtRoomId.getText().trim(),
                    (String) view.cmbRoomCategory.getSelectedItem()
                );
                room.add();
                loadRooms();
                view.clearFields();
                generateNextId();
                AdminRoomView.refreshAllRoomLists();
                FrontdeskRoomView.refreshAllRoomLists();
                JOptionPane.showMessageDialog(view, "Room added successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error adding room: " + ex.getMessage());
            }
        });

        // UPDATE
        view.btnUpdate.addActionListener(e -> {
            int row = view.table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(view, "Please select a room to update.");
                return;
            }
            try {
                String roomId = view.model.getValueAt(row, 0).toString();
                RoomModel room = new RoomModel(
                    roomId,
                    (String) view.cmbRoomCategory.getSelectedItem()
                );
                room.update();
                loadRooms();
                view.clearFields();
                generateNextId();
                AdminRoomView.refreshAllRoomLists();
                FrontdeskRoomView.refreshAllRoomLists();
                JOptionPane.showMessageDialog(view, "Room updated successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error updating room: " + ex.getMessage());
            }
        });

        // DELETE
        view.btnDelete.addActionListener(e -> {
            int row = view.table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(view, "Please select a room to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this room?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                String roomId = view.model.getValueAt(row, 0).toString();
                RoomModel.delete(roomId);
                loadRooms();
                view.clearFields();
                generateNextId();
                AdminRoomView.refreshAllRoomLists();
                FrontdeskRoomView.refreshAllRoomLists();
                JOptionPane.showMessageDialog(view, "Room deleted successfully.");
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.toLowerCase().contains("constraint")) {
                    JOptionPane.showMessageDialog(view, "Cannot delete: This record is linked to another data. Please remove related record first.", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view, "Error deleting room: " + ex.getMessage());
                }
            }
        });

        // CLEAR
        view.btnClear.addActionListener(e -> {
            view.clearFields();
            generateNextId();
        });

        // TABLE CLICK → FILL FORM
        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.table.getSelectedRow();
            if (row != -1) {
                view.txtRoomId.setText(view.model.getValueAt(row, 0).toString());
                view.cmbRoomCategory.setSelectedItem(view.model.getValueAt(row, 1).toString());
            }
        });
    }

    private void loadRooms() {
        view.model.setRowCount(0);
        List<RoomModel> list = RoomModel.getAll();
        for (RoomModel r : list) {
            view.model.addRow(new Object[]{
                r.getRoomId(),
                r.getRoomCategory()
            });
        }
    }
}