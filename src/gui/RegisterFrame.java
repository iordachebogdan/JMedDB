package gui;

import controller.AuthenticationController;
import model.UserTypeEnum;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class RegisterFrame extends JFrame {
    static final int width = 320;
    static final int height = 480;

    RegisterFrame() {
        JFrame frame = this;
        setSize(width, height);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("User");
        JTextField usernameText = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        JTextField passwordText = new JTextField();
        JLabel firstNameLabel = new JLabel("First name");
        JTextField firstNameText = new JTextField();
        JLabel lastNameLabel = new JLabel("Last name");
        JTextField lastNameText = new JTextField();
        JLabel emailLabel = new JLabel("Email");
        JTextField emailText = new JTextField();
        JLabel bioLabel = new JLabel("Bio");
        JTextField bioText = new JTextField();

        JRadioButtonMenuItem medicRadio = new JRadioButtonMenuItem("medic");
        JRadioButtonMenuItem patientRadio = new JRadioButtonMenuItem("patient");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(medicRadio);
        buttonGroup.add(patientRadio);

        usernameLabel.setBounds(32, 48, 100, 35); add(usernameLabel);
        usernameText.setBounds(140, 48, 100, 35); add(usernameText);
        passwordLabel.setBounds(32, 2 * 48, 100, 35); add(passwordLabel);
        passwordText.setBounds(140, 2 * 48, 100, 35); add(passwordText);
        firstNameLabel.setBounds(32, 3 * 48, 100, 35); add(firstNameLabel);
        firstNameText.setBounds(140, 3 * 48, 100, 35); add(firstNameText);
        lastNameLabel.setBounds(32, 4 * 48, 100, 35); add(lastNameLabel);
        lastNameText.setBounds(140, 4 * 48, 100, 35); add(lastNameText);
        emailLabel.setBounds(32, 5 * 48, 100, 35); add(emailLabel);
        emailText.setBounds(140, 5 * 48, 100, 35); add(emailText);
        bioLabel.setBounds(32, 6 * 48, 100, 35); add(bioLabel);
        bioText.setBounds(140, 6 * 48, 100, 35); add(bioText);

        medicRadio.setBounds(50, 7 * 48, 100, 35); add(medicRadio);
        patientRadio.setBounds(170, 7 * 48, 100, 35); add(patientRadio);
        patientRadio.setSelected(true);

        JButton button = new JButton("REGISTER");
        button.setBounds(100, 8 * 48, 100, 35);
        add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameText.getText();
                String password = passwordText.getText();
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String email = emailText.getText();
                String bio = bioText.getText();
                UserTypeEnum type = patientRadio.isSelected() ? UserTypeEnum.PATIENT : UserTypeEnum.MEDIC;

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        AuthenticationController.getInstance().registerUser(username, password, firstName, lastName,
                                email, bio, type);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(frame, e1.getMessage());
                            return;
                        }
                        frame.dispose();
                    }
                };
                worker.execute();
            }
        });

        setVisible(true);
    }
}
