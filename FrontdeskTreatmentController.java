package edp.controller;

import edp.model.BedModel;
import edp.view.AdminBedView;
import javax.swing.*;

public class AdminBedController {

    private final AdminBedView view;

    public AdminBedController(AdminBedView view) {
        this.view = view;
        init();
        loadBeds();
    }

    private void init() {

        // Refresh Room List Button
        view.btnRefreshRooms.addActionListener(e -> {
            view.refreshRoomCombo();
            JOptionPane.showMessageDialog(view, "Room list refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        view.btnAdd.addActionListener(e -> {
            try {
                String roomId = (String) view.cboRoomId.getSelectedItem();
                String status = (String) view.cboBStatus.getSelectedItem();
                if (roomId == null) {
                    JOptionPane.showMessageDialog(view, "No rooms available! Please add a room first.");
                    return;
                }
                new BedModel(null, roomId, status).add();
                loadBeds();
                clear();
                JOptionPane.showMessageDialog(view, "Bed added successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error adding bed: " + ex.getMessage());
            }
        });

        view.btnUpdate.addActionListener(e -> {
            int row = view.table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(view, "Select a bed to update.");
                return;
            }
            try {
                int numericId = BedModel.parseFormattedBedId(
                    view.model.getValueAt(row, 0).toString());
                String roomId = (String) view.cboRoomId.getSelectedItem();
                String status = (String) view.cboBStatus.getSelectedItem();
                new BedModel(numericId, roomId, status).update();
                loadBeds();
                clear();
                JOptionPane.showMessageDialog(view, "Bed updated successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error updating bed: " + ex.getMessage());
            }
        });

        view.btnDelete.addActionListener(e -> {
            int row = view.table.getSelectedRow();
            if (row == -1) return;
            try {
                int numericId = BedModel.parseFormattedBedId(
                    view.model.getValueAt(row, 0).toString());
                BedModel.delete(numericId);
                loadBeds();
                clear();
                JOptionPane.showMessageDialog(view, "Bed deleted successfully.");
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.toLowerCase().contains("constraint")) {
                    JOptionPane.showMessageDialog(view, "Cannot delete: This record is linked to other data. Please remove related records first.", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view, "Error deleting bed: " + ex.getMessage());
                }
            }
        });

        view.btnClear.addActionListener(e -> clear());

        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.table.getSelectedRow();
            if (row != -1) {
                view.cboRoomId.setSelectedItem(
                    view.model.getValueAt(row, 1).toString());
                view.cboBStatus.setSelectedItem(
                    view.model.getValueAt(row, 2).toString());
            }
        });
    }

    private void loadBeds() {
        view.refreshRoomCombo();
        view.model.setRowCount(0);
        for (BedModel b : BedModel.getAll()) {
            view.model.addRow(new Object[]{
                b.getFormattedBedId(),
                b.getRoomId(),
                b.getStatus()
            });
        }
    }

    private void clear() {
        if (view.cboRoomId.getItemCount() > 0)
            view.cboRoomId.setSelectedIndex(0);
        view.cboBStatus.setSelectedIndex(0);
        view.table.clearSelection();
    }
}
