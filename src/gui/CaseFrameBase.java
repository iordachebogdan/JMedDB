package gui;

import controller.CaseController;
import controller.MedicController;
import controller.PatientController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public abstract class CaseFrameBase extends JFrame {
    static final int width = 1200;
    static final int height = 800;

    protected JTable symptomTable, medicationTable, medicTable;
    protected DefaultTableModel symptomModel, medicationModel, medicModel;
    protected JLabel title;

    public CaseFrameBase(String token, int caseId) {
        JFrame frame = this;
        setSize(width, height);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        title = new JLabel("Case");
        title.setBounds(100, 20, 300, 40); add(title);
        JLabel patientLabel = new JLabel("Patient");
        patientLabel.setBounds(100, 50, 300, 40); add(patientLabel);
        JLabel medicLabel = new JLabel("Owner");
        medicLabel.setBounds(450, 50, 300, 40); add(medicLabel);

        symptomTable = new JTable();
        Object[] columnsSymptom = {"description", "first app"};
        symptomModel = new DefaultTableModel();
        symptomModel.setColumnIdentifiers(columnsSymptom);
        symptomTable.setModel(symptomModel);
        symptomTable.setBackground(Color.LIGHT_GRAY);
        symptomTable.setForeground(Color.black);
        Font font = new Font("",1,10);
        symptomTable.setFont(font);
        symptomTable.setRowHeight(30);
        JScrollPane symptomTableScroll = new JScrollPane(symptomTable);
        symptomTableScroll.setBounds(50, 110, 300, 400);
        add(symptomTableScroll);

        medicationTable = new JTable();
        Object[] columnsMedication = {"name", "from", "to"};
        medicationModel = new DefaultTableModel();
        medicationModel.setColumnIdentifiers(columnsMedication);
        medicationTable.setModel(medicationModel);
        medicationTable.setBackground(Color.LIGHT_GRAY);
        medicationTable.setForeground(Color.black);
        medicationTable.setFont(font);
        medicationTable.setRowHeight(30);
        JScrollPane medicationTableScroll = new JScrollPane(medicationTable);
        medicationTableScroll.setBounds(380, 110, 400, 400);
        add(medicationTableScroll);

        medicTable = new JTable();
        Object[] columnsMedic = {"id", "medic"};
        medicModel = new DefaultTableModel();
        medicModel.setColumnIdentifiers(columnsMedic);
        medicTable.setModel(medicModel);
        medicTable.setBackground(Color.LIGHT_GRAY);
        medicTable.setForeground(Color.black);
        medicTable.setFont(font);
        medicTable.setRowHeight(30);
        JScrollPane medicTableScroll = new JScrollPane(medicTable);
        medicTableScroll.setBounds(800, 110, 350, 400);
        medicTable.getColumnModel().getColumn(0).setMaxWidth(30);
        add(medicTableScroll);

        JLabel symptomTblLabel = new JLabel("Symptoms");
        symptomTblLabel.setBounds(150, 510, 100, 30); add(symptomTblLabel);
        JLabel medicationTblLabel = new JLabel("Prescription");
        medicationTblLabel.setBounds(530, 510, 100, 30); add(medicationTblLabel);
        JLabel medicTblLabel = new JLabel("Other medics");
        medicTblLabel.setBounds(925, 510, 100, 30); add(medicTblLabel);

        SwingWorker<Case, Void> caseWorker = new SwingWorker<Case, Void>() {
            @Override
            protected Case doInBackground() throws Exception {
                return CaseController.getInstance().getCaseById(token, caseId);
            }

            @Override
            protected void done() {
                try {
                    Case c = get();
                    Patient patient = PatientController.getInstance().getPatientById(token, c.getPatientId());
                    Medic owner = MedicController.getInstance().getMedicById(c.getOwnerMedicId());
                    title.setText("Case #" + caseId + (c.isCompleted() ? " (completed)" : ""));
                    patientLabel.setText("Patient: " + patient.getUserDetails().getFirstName() + " " + patient.getUserDetails().getLastName());
                    medicLabel.setText("Owner: " + owner.getUserDetails().getFirstName() + " " + owner.getUserDetails().getLastName());

                    for (Symptom symptom : c.getSymptomList().getSymptoms())
                        symptomModel.addRow(new Object[]{symptom.getDescription(), symptom.getFirstAppearance().toString()});
                    for (Medication medication : c.getPrescription().getMedication())
                        medicationModel.addRow(new Object[]{medication.getName(), medication.getFromDate(), medication.getToDate()});
                    for (int medicId : c.getOtherMedicsIds()) {
                        Medic other = MedicController.getInstance().getMedicById(medicId);
                        medicModel.addRow(new Object[]{medicId, other.getUserDetails().getFirstName()
                            + " " + other.getUserDetails().getLastName() + other.getSpecializations().toString()});
                    }
                    frame.repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
            }
        };
        caseWorker.execute();

        setVisible(true);
    }
}
