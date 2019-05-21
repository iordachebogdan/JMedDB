package gui;

import controller.CaseController;
import dao.exceptions.NotFoundException;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

public class CaseFramePatient extends CaseFrameBase {
    public CaseFramePatient(String token, int caseId) {
        super(token, caseId);
        JFrame frame = this;

        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setBounds(50, 550, 100, 30); add(descriptionLabel);
        JLabel firstLabel = new JLabel("First app");
        firstLabel.setBounds(50, 600, 100, 30); add(firstLabel);

        JTextField descriptionText = new JTextField();
        descriptionText.setBounds(150, 550, 200, 30); add(descriptionText);

        Properties p = new Properties();
        p.put("text.today", "today");
        p.put("text.month", "month");
        p.put("text.year", "year");
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl firstPicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());
        firstPicker.setBounds(150, 600, 200, 30); add(firstPicker);

        JButton addSymptomBtn = new JButton("Add");
        addSymptomBtn.setBounds(50, 650, 100, 30); add(addSymptomBtn);
        JButton removeSymptomBtn = new JButton("Remove");
        removeSymptomBtn.setBounds(180, 650, 100, 30); add(removeSymptomBtn);

        addSymptomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CaseController.getInstance().addSymptom(token, caseId,
                            new Symptom(descriptionText.getText(), (Date) firstPicker.getModel().getValue()));
                    symptomModel.addRow(new Object[]{descriptionText.getText(),
                            (new java.sql.Date(((Date) firstPicker.getModel().getValue()).getTime())).toString()});
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, e1.getMessage());
                }
            }
        });

        removeSymptomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = symptomTable.getSelectedRow();
                if (i < 0)
                    return;
                try {
                    CaseController.getInstance().removeSymptom(token, caseId,
                            new Symptom(symptomModel.getValueAt(i, 0).toString(), null));
                    symptomModel.removeRow(i);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(frame, e1.getMessage());
                }
            }
        });
    }
}
