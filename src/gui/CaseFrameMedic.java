package gui;

import controller.CaseController;
import controller.MedicController;
import dao.exceptions.NotFoundException;
import model.Medic;
import model.Medication;
import model.Symptom;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class CaseFrameMedic extends CaseFrameBase {
    public CaseFrameMedic(String token, int caseId) {
        super(token, caseId);
        JFrame frame = this;

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(380, 550, 100, 30); add(nameLabel);
        JLabel fromLabel = new JLabel("From");
        fromLabel.setBounds(380, 600, 100, 30); add(fromLabel);
        JLabel toLabel = new JLabel("To");
        toLabel.setBounds(380, 650, 100, 30); add(toLabel);

        JTextField nameText = new JTextField();
        nameText.setBounds(480, 550, 200, 30); add(nameText);

        Properties p = new Properties();
        p.put("text.today", "today");
        p.put("text.month", "month");
        p.put("text.year", "year");
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl fromPicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());
        fromPicker.setBounds(480, 600, 200, 30); add(fromPicker);
        JDatePickerImpl toPicker = new JDatePickerImpl(new JDatePanelImpl(new UtilDateModel(), p), new DateComponentFormatter());
        toPicker.setBounds(480, 650, 200, 30); add(toPicker);

        JButton addMedicationBtn = new JButton("Add");
        addMedicationBtn.setBounds(380, 700, 100, 30); add(addMedicationBtn);
        JButton removeMedicationBtn = new JButton("Remove");
        removeMedicationBtn.setBounds(510, 700, 100, 30); add(removeMedicationBtn);

        addMedicationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CaseController.getInstance().addMedication(token, caseId,
                            new Medication(nameText.getText(), "daily", (Date) fromPicker.getModel().getValue(),
                                    (Date) toPicker.getModel().getValue()));
                    medicationModel.addRow(new Object[]{nameText.getText(),
                            (new java.sql.Date(((Date) fromPicker.getModel().getValue()).getTime())).toString(),
                            (new java.sql.Date(((Date) toPicker.getModel().getValue()).getTime())).toString()
                    });
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, e1.getMessage());
                }
            }
        });

        removeMedicationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = medicationTable.getSelectedRow();
                if (i < 0)
                    return;
                try {
                    CaseController.getInstance().removeMedication(token, caseId,
                            new Medication(medicationModel.getValueAt(i, 0).toString(), null, null, null));
                    medicationModel.removeRow(i);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, e1.getMessage());
                }
            }
        });

        JTextField addMedicText = new JTextField();
        addMedicText.setBounds(800, 550, 200, 30); add(addMedicText);
        JButton addMedicBtn = new JButton("Add");
        addMedicBtn.setBounds(1050, 550, 100, 30); add(addMedicBtn);

        addMedicBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = addMedicText.getText();
                if (id == null || id.equals(""))
                    return;
                try {
                    CaseController.getInstance().addMedic(token, caseId, Integer.parseInt(id));
                    Medic medic = MedicController.getInstance().getMedicById(Integer.parseInt(id));
                    medicModel.addRow(new Object[]{id, medic.getUserDetails().getFirstName() + " " +
                        medic.getUserDetails().getLastName() + medic.getSpecializations().toString()});
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, e1.getMessage());
                }
            }
        });

        JButton setCompleted = new JButton("Set completed");
        setCompleted.setBounds(450, 20, 200, 30); add(setCompleted);

        setCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CaseController.getInstance().setCompleted(token, caseId);
                    title.setText("Case #" + caseId + " (completed)");
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, e1.getMessage());
                }
            }
        });
    }
}
