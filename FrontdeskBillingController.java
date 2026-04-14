package edp.controller;

import edp.model.BillingModel;
import edp.view.AdminBillingView;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class AdminBillingController {

    private final AdminBillingView view;
    private final ReceiptController receiptController;

    public AdminBillingController(AdminBillingView view) {
        this.view = view;
        this.receiptController = new ReceiptController(view);
        view.loadTreatmentIds();
        loadTable();
        initListeners();
    }

    private void loadTable() {
        try {
            List<Object[]> rows = BillingModel.getAll();
            view.populateTable(rows);
        } catch (Exception e) {
            view.showError("Failed to load billing records:\n" + e.getMessage());
        }
    }

    private void initListeners() {

        view.btnRefreshLists.addActionListener(e -> {
            view.refreshTreatmentIds();
            JOptionPane.showMessageDialog(view, "Treatment list refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        view.table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int viewRow = view.table.getSelectedRow();
            if (viewRow < 0) return;

            // Convert view index → model index (important when table is sorted)
            int row = view.table.convertRowIndexToModel(viewRow);
            if (row < 0 || row >= view.model.getRowCount()) return;

            Object col1 = view.model.getValueAt(row, 1);
            Object col2 = view.model.getValueAt(row, 2);
            Object col3 = view.model.getValueAt(row, 3);
            Object col4 = view.model.getValueAt(row, 4);
            Object col5 = view.model.getValueAt(row, 5);
            Object col6 = view.model.getValueAt(row, 6);

            if (col1 == null || col2 == null || col3 == null || col4 == null || col5 == null || col6 == null) return;

            String treatIdStr    = col1.toString().replaceAll("[^0-9]", "");
            String totalAmount   = col2.toString();
            String amountPaid    = col3.toString();
            String paymentStatus = col4.toString();
            String paymentDate   = col5.toString();
            String paymentMethod = col6.toString();

            // Add to combo if not present
            boolean found = false;
            for (int i = 0; i < view.cmbTreatmentId.getItemCount(); i++) {
                if (view.cmbTreatmentId.getItemAt(i).equals(treatIdStr)) {
                    found = true;
                    break;
                }
            }
            if (!found && !treatIdStr.isEmpty()) {
                view.cmbTreatmentId.addItem(treatIdStr);
            }
            view.cmbTreatmentId.setSelectedItem(treatIdStr);

            view.txtTotalAmount.setText(totalAmount);
            view.txtAmountPaid.setText(amountPaid);
            view.cmbPaymentStatus.setSelectedItem(paymentStatus);
            view.cmbPaymentMethod.setSelectedItem(paymentMethod);
            view.setPaymentDate(paymentDate);
        });

        view.btnAdd.addActionListener(e -> {
            if (!validateInput()) return;
            try {
                BillingModel b = buildFromForm();
                b.add();
                view.showMessage("Billing record added successfully.");
                view.clearFields();
                loadTable();
            } catch (Exception ex) {
                view.showError("Failed to add billing record:\n" + ex.getMessage());
            }
        });

        view.btnUpdate.addActionListener(e -> {
            int viewRow = view.table.getSelectedRow();
            if (viewRow < 0) { view.showError("Please select a record to update."); return; }
            if (!validateInput()) return;
            int row = view.table.convertRowIndexToModel(viewRow);
            try {
                Object idVal = view.model.getValueAt(row, 0);
                if (idVal == null) { view.showError("Could not read Billing ID."); return; }
                int billingId = Integer.parseInt(idVal.toString().replace("B", ""));
                BillingModel b = buildFromForm();
                b.setBillingId(billingId);
                b.update();
                view.showMessage("Billing record updated successfully.");
                view.clearFields();
                loadTable();
            } catch (Exception ex) {
                view.showError("Failed to update billing record:\n" + ex.getMessage());
            }
        });

        view.btnDelete.addActionListener(e -> {
            int viewRow = view.table.getSelectedRow();
            if (viewRow < 0) { view.showError("Please select a record to delete."); return; }

            int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this billing record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            int row = view.table.convertRowIndexToModel(viewRow);
            try {
                Object idVal = view.model.getValueAt(row, 0);
                if (idVal == null) { view.showError("Could not read Billing ID."); return; }
                int billingId = Integer.parseInt(idVal.toString().replace("B", ""));
                BillingModel.delete(billingId);
                view.showMessage("Billing record deleted successfully.");
                view.clearFields();
                loadTable();
            } catch (Exception ex) {
                view.showError("Failed to delete billing record:\n" + ex.getMessage());
            }
        });

        view.btnClear.addActionListener(e -> view.clearFields());

        view.btnReceipt.addActionListener(e -> receiptController.showReceipt());
    }

    private BillingModel buildFromForm() {
        String treatId = view.getSelectedTreatmentId();
        double totalAmount = Double.parseDouble(view.txtTotalAmount.getText().trim());
        double amountPaid = Double.parseDouble(view.txtAmountPaid.getText().trim());
        LocalDate date = LocalDate.parse(view.getPaymentDate());
        return new BillingModel(
            0,
            Integer.parseInt(treatId),
            totalAmount,
            amountPaid,
            view.cmbPaymentStatus.getSelectedItem().toString(),
            date,
            view.cmbPaymentMethod.getSelectedItem().toString()
        );
    }

    private boolean validateInput() {
        if (view.getSelectedTreatmentId().isEmpty()) {
            view.showError("Please select a Treatment ID."); return false;
        }
        String totalAmountText = view.txtTotalAmount.getText().trim();
        if (totalAmountText.isEmpty()) {
            view.showError("Total Amount cannot be empty."); return false;
        }
        try {
            double totalAmount = Double.parseDouble(totalAmountText);
            if (totalAmount < 0) { view.showError("Total Amount must be a positive number."); return false; }
        } catch (NumberFormatException ex) {
            view.showError("Total Amount must be a valid number."); return false;
        }
        String amountText = view.txtAmountPaid.getText().trim();
        if (amountText.isEmpty()) {
            view.showError("Amount Paid cannot be empty."); return false;
        }
        try {
            double amount = Double.parseDouble(amountText);
            if (amount < 0) { view.showError("Amount Paid must be a positive number."); return false; }
        } catch (NumberFormatException ex) {
            view.showError("Amount Paid must be a valid number."); return false;
        }
        String paymentStatus = (String) view.cmbPaymentStatus.getSelectedItem();
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            view.showError("Please select a Payment Status."); return false;
        }
        String paymentMethod = (String) view.cmbPaymentMethod.getSelectedItem();
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            view.showError("Please select a Payment Method."); return false;
        }
        return true;
    }
}
