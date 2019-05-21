package gui;

import controller.AuthenticationController;
import controller.CaseController;
import controller.MedicController;
import controller.PatientController;
import dao.exceptions.NotFoundException;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AppFrame extends JFrame {
    static final int width = 800;
    static final int height = 600;

    static private class MedicWorker extends SwingWorker<Object[], Void> {
        JTextField specializationText;
        DefaultTableModel medicModel;
        JFrame frame;

        MedicWorker(JTextField specializationText, DefaultTableModel medicModel, JFrame frame) {
            this.specializationText = specializationText;
            this.medicModel = medicModel;
            this.frame = frame;
        }

        @Override
        protected Object[] doInBackground() throws Exception {
            if (specializationText.getText() == null || specializationText.getText().equals("")) {
                return MedicController.getInstance().getAllMedics().toArray();
            } else {
                return MedicController.getInstance()
                        .getAllMedicsBySpecialization(new Specialization(specializationText.getText())).toArray();
            }
        }

        @Override
        protected void done() {
            try {
                Object[] medics = get();
                for (int i = medicModel.getRowCount() - 1; i >= 0; --i)
                    medicModel.removeRow(i);
                for (Object medicObj : medics) {
                    Medic medic = (Medic)medicObj;
                    Object[] row = new Object[]{Integer.toString(medic.getId()),
                            medic.getUserDetails().getFirstName() + " " + medic.getUserDetails().getLastName() + " " +
                                    medic.getSpecializations().toString()};
                    medicModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, e.getMessage());
            }
        }
    }

    public AppFrame(String token) {
        JFrame frame = this;
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelContainer = new JPanel();
        JPanel panelUser = new JPanel();
        JPanel panelCases = new JPanel();
        CardLayout cl = new CardLayout();
        panelContainer.setLayout(cl);

        panelUser.setLayout(null);
        panelCases.setLayout(null);

        JButton goToUser = new JButton("User data");
        JButton goToCases = new JButton("See cases");
        goToUser.setBounds(0, 0, 100, 20); panelCases.add(goToUser);
        goToCases.setBounds(0, 0, 100, 20); panelUser.add(goToCases);

        panelContainer.add(panelUser, "user");
        panelContainer.add(panelCases, "cases");

        add(panelContainer);

        cl.show(panelContainer, "user");

        goToUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(panelContainer, "user");
            }
        });

        goToCases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(panelContainer, "cases");
            }
        });


        UserTypeEnum type;
        try {
            type = AuthenticationController.getInstance().getType(token);
        } catch (Exception e) {
            e.printStackTrace();
            setVisible(true);
            return;
        }

        if (type == UserTypeEnum.PATIENT) {
            SwingWorker<Patient, Void> workerData = new SwingWorker<Patient, Void>() {
                @Override
                protected Patient doInBackground() throws Exception {
                    return PatientController.getInstance().getPatientPersonalData(token);
                }

                @Override
                protected void done() {
                    try {
                        Patient patient = get();
                        JLabel nameText = new JLabel(patient.getUserDetails().getFirstName() + " " + patient.getUserDetails().getLastName());
                        JLabel emailText = new JLabel(patient.getUserDetails().getEmail());
                        JLabel bioText = new JLabel(patient.getUserDetails().getBio());

                        Integer medicId = patient.getPersonalMedicId();
                        JLabel medicText;
                        if (medicId == null || medicId <= 0) {
                             medicText = new JLabel("Medic: N\\A");
                        } else {
                            Medic medic = MedicController.getInstance().getMedicById(medicId);
                            medicText = new JLabel("Medic: " + medic.getUserDetails().getFirstName() + " " + medic.getUserDetails().getLastName());
                        }

                        nameText.setBounds(100, 100, 200, 30); panelUser.add(nameText);
                        emailText.setBounds(100, 150, 200, 30); panelUser.add(emailText);
                        bioText.setBounds(100, 200, 200, 30); panelUser.add(bioText);
                        medicText.setBounds(100, 250, 200, 30); panelUser.add(medicText);
                        frame.repaint();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                    }
                }
            };
            workerData.execute();

            JTable medicTable = new JTable();
            Object[] columnsMedic = {"id", "medic"};
            DefaultTableModel medicModel = new DefaultTableModel();
            medicModel.setColumnIdentifiers(columnsMedic);
            medicTable.setModel(medicModel);
            medicTable.setBackground(Color.LIGHT_GRAY);
            medicTable.setForeground(Color.black);
            Font font = new Font("",1,10);
            medicTable.setFont(font);
            medicTable.setRowHeight(30);
            JScrollPane medicTableScroll = new JScrollPane(medicTable);
            medicTableScroll.setBounds(350, 50, 400, 400);
            medicTable.getColumnModel().getColumn(0).setMaxWidth(30);
            panelUser.add(medicTableScroll);

            JLabel specializationLabel = new JLabel("Specialization");
            JTextField specializationText = new JTextField();
            JButton getMedicsBtn = new JButton("Get medics");
            specializationLabel.setBounds(350, 500, 80, 30); panelUser.add(specializationLabel);
            specializationText.setBounds(450, 500, 100, 30); panelUser.add(specializationText);
            getMedicsBtn.setBounds(600, 500, 100, 30); panelUser.add(getMedicsBtn);


            MedicWorker medicWorker = new MedicWorker(specializationText, medicModel, frame);
            medicWorker.execute();

            getMedicsBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MedicWorker medicWorker = new MedicWorker(specializationText, medicModel, frame);
                    medicWorker.execute();
                }
            });

            JButton sendReqBtn = new JButton("Send request");
            sendReqBtn.setBounds(350, 450, 150, 20);
            panelUser.add(sendReqBtn);

            sendReqBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = medicTable.getSelectedRow();
                    if (i < 0)
                        return;
                    int medicId = Integer.parseInt((String) medicModel.getValueAt(i, 0));
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            PatientController.getInstance().sendPatientRequest(token, medicId);
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                get();
                                JOptionPane.showMessageDialog(frame, "Request sent");
                            } catch (Exception e1) {
                                JOptionPane.showMessageDialog(frame, e1.getMessage());
                            }
                        }
                    };
                    worker.execute();
                }
            });

            JTable caseTable = new JTable();
            Object[] columnsCase = {"id", "case"};
            DefaultTableModel caseModel = new DefaultTableModel();
            caseModel.setColumnIdentifiers(columnsCase);
            caseTable.setModel(caseModel);
            caseTable.setBackground(Color.LIGHT_GRAY);
            caseTable.setForeground(Color.black);
            caseTable.setFont(font);
            caseTable.setRowHeight(30);
            JScrollPane caseTableScroll = new JScrollPane(caseTable);
            caseTableScroll.setBounds(200, 50, 400, 400);
            caseTable.getColumnModel().getColumn(0).setMaxWidth(30);
            panelCases.add(caseTableScroll);

            SwingWorker<Object[], Void> caseWorker = new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    return PatientController.getInstance().getPatientPersonalData(token).getAssignedCasesIds().toArray();
                }

                @Override
                protected void done() {
                    try {
                        Object[] cases = get();
                        for (Object caseObj : cases) {
                            int caseId = (Integer) caseObj;
                            Case c = CaseController.getInstance().getCaseById(token, caseId);
                            Object[] row = new Object[] {c.getId(),
                                "Case #" + c.getId() + (c.isCompleted() ? "(completed)" : "")};
                            caseModel.addRow(row);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                    }
                }
            };
            caseWorker.execute();

            JButton goCaseBtn = new JButton("Go");
            goCaseBtn.setBounds(280, 500, 100, 30);
            panelCases.add(goCaseBtn);
            goCaseBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = caseTable.getSelectedRow();
                    if (i < 0)
                        return;
                    int caseId = (int) caseModel.getValueAt(i, 0);
                    CaseFrameBase caseFrameBase = new CaseFramePatient(token, caseId);
                }
            });

            JButton newCaseBtn = new JButton("New");
            newCaseBtn.setBounds(410, 500, 100, 30);
            panelCases.add(newCaseBtn);
            newCaseBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
                        @Override
                        protected Integer doInBackground() throws Exception {
                            return CaseController.getInstance().createCase(token);
                        }

                        @Override
                        protected void done() {
                            try {
                                int caseId = get();
                                caseModel.addRow(new Object[]{caseId, "Case #" + caseId});
                            } catch (Exception e1) {
                                JOptionPane.showMessageDialog(frame, e1.getMessage());
                            }
                        }
                    };
                    worker.execute();
                }
            });
        } else {
            SwingWorker<Medic, Void> workerData = new SwingWorker<Medic, Void>() {
                @Override
                protected Medic doInBackground() throws Exception {
                    return MedicController.getInstance().getMedicData(token);
                }

                @Override
                protected void done() {
                    try {
                        Medic medic = get();
                        JLabel nameText = new JLabel(medic.getUserDetails().getFirstName() + " " + medic.getUserDetails().getLastName());
                        JLabel emailText = new JLabel(medic.getUserDetails().getEmail());
                        JLabel bioText = new JLabel(medic.getUserDetails().getBio());

                        nameText.setBounds(100, 100, 200, 30); panelUser.add(nameText);
                        emailText.setBounds(100, 150, 200, 30); panelUser.add(emailText);
                        bioText.setBounds(100, 200, 200, 30); panelUser.add(bioText);
                        frame.repaint();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                    }
                }
            };
            workerData.execute();

            JTable specTable = new JTable();
            Object[] columnsSpec = {"specializations"};
            DefaultTableModel specModel = new DefaultTableModel();
            specModel.setColumnIdentifiers(columnsSpec);
            specTable.setModel(specModel);
            specTable.setBackground(Color.LIGHT_GRAY);
            specTable.setForeground(Color.black);
            Font font = new Font("",1,10);
            specTable.setFont(font);
            specTable.setRowHeight(30);
            JScrollPane specTableScroll = new JScrollPane(specTable);
            specTableScroll.setBounds(250, 50, 200, 400);
            panelUser.add(specTableScroll);

            SwingWorker<Object[], Void> specWorker = new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    return MedicController.getInstance().getMedicData(token).getSpecializations().toArray();
                }

                @Override
                protected void done() {
                    try {
                        Object[] specs = get();
                        for (Object specObj : specs) {
                            Specialization spec = (Specialization)specObj;
                            specModel.addRow(new Object[]{spec.getName()});
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                    }
                }
            };
            specWorker.execute();

            JTextField addSpecText = new JTextField();
            JButton addSpecBtn = new JButton("Add");
            addSpecText.setBounds(250, 500, 100, 30); panelUser.add(addSpecText);
            addSpecBtn.setBounds(355, 500, 90, 30); panelUser.add(addSpecBtn);

            addSpecBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (addSpecText.getText() == null || addSpecText.getText().equals(""))
                        return;
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            MedicController.getInstance().addSpecialization(token, new Specialization(addSpecText.getText()));
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                get();
                                specModel.addRow(new Object[]{addSpecText.getText()});
                            } catch (Exception e1) {
                                JOptionPane.showMessageDialog(frame, e1.getMessage());
                            }

                        }
                    };
                    worker.execute();
                }
            });

            JTable reqTable = new JTable();
            Object[] columnsReq = {"id", "patient"};
            DefaultTableModel reqModel = new DefaultTableModel();
            reqModel.setColumnIdentifiers(columnsReq);
            reqTable.setModel(reqModel);
            reqTable.setBackground(Color.LIGHT_GRAY);
            reqTable.setForeground(Color.black);
            reqTable.setFont(font);
            reqTable.setRowHeight(30);
            JScrollPane reqTableScroll = new JScrollPane(reqTable);
            reqTableScroll.setBounds(500, 50, 200, 400);
            panelUser.add(reqTableScroll);

            SwingWorker<Object[], Void> reqWorker = new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    return MedicController.getInstance().getRequestPatientsIds(token).toArray();
                }

                @Override
                protected void done() {
                    try {
                        Object[] ids = get();
                        for (Object id : ids) {
                            Patient patient = PatientController.getInstance().getPatientById(token, (int)id);
                            reqModel.addRow(new Object[]{id, patient.getUserDetails().getFirstName() + " " +
                                patient.getUserDetails().getLastName()});
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                    }
                }
            };
            reqWorker.execute();

            JButton acceptBtn = new JButton("Accept");
            acceptBtn.setBounds(550, 500, 100, 30); panelUser.add(acceptBtn);

            acceptBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = reqTable.getSelectedRow();
                    if (i < 0)
                        return;
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            MedicController.getInstance().acceptRequest(token, (int)reqModel.getValueAt(i, 0));
                            reqModel.removeRow(i);
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                get();
                            } catch (Exception e1) {
                                JOptionPane.showMessageDialog(frame, e1.getMessage());
                            }
                        }
                    };
                    worker.execute();
                }
            });

            JTable caseTable = new JTable();
            Object[] columnsCase = {"id", "case"};
            DefaultTableModel caseModel = new DefaultTableModel();
            caseModel.setColumnIdentifiers(columnsCase);
            caseTable.setModel(caseModel);
            caseTable.setBackground(Color.LIGHT_GRAY);
            caseTable.setForeground(Color.black);
            caseTable.setFont(font);
            caseTable.setRowHeight(30);
            JScrollPane caseTableScroll = new JScrollPane(caseTable);
            caseTableScroll.setBounds(200, 50, 400, 400);
            caseTable.getColumnModel().getColumn(0).setMaxWidth(30);
            panelCases.add(caseTableScroll);

            SwingWorker<Object[], Void> caseWorker = new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    return MedicController.getInstance().getMedicData(token).getAssignedCasesIds().toArray();
                }

                @Override
                protected void done() {
                    try {
                        Object[] cases = get();
                        for (Object caseObj : cases) {
                            int caseId = (Integer) caseObj;
                            Case c = CaseController.getInstance().getCaseById(token, caseId);
                            Object[] row = new Object[] {c.getId(),
                                    "Case #" + c.getId() + (c.isCompleted() ? "(completed)" : "")};
                            caseModel.addRow(row);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                    }
                }
            };
            caseWorker.execute();

            JButton goCaseBtn = new JButton("Go");
            goCaseBtn.setBounds(350, 500, 100, 30);
            panelCases.add(goCaseBtn);
            goCaseBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = caseTable.getSelectedRow();
                    if (i < 0)
                        return;
                    int caseId = (int) caseModel.getValueAt(i, 0);
                    CaseFrameBase caseFrameBase = new CaseFrameMedic(token, caseId);
                }
            });

        }


        setVisible(true);
    }
}
