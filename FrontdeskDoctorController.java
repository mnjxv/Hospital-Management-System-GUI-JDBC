package edp.controller;

import edp.model.RoomModel;
import edp.view.FrontdeskRoomView;

import javax.swing.*;
import java.util.List;

public class FrontdeskRoomController {

    private final FrontdeskRoomView view;

    public FrontdeskRoomController(FrontdeskRoomView view) {
        this.view = view;
        initActions();
        loadRooms();
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

    private void initActions() {

        view.btnRefreshList.addActionListener(e -> loadRooms());

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
                JOptionPane.showMessageDialog(view, "Room updated successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Error updating room: " + ex.getMessage());
            }
        });

        view.btnClear.addActionListener(e -> view.clearFields());

        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.table.getSelectedRow();
            if (row != -1) {
                view.txtRoomId.setText(view.model.getValueAt(row, 0).toString());
                view.cmbRoomCategory.setSelectedItem(view.model.getValueAt(row, 1).toString());
            }
        });
    }
}