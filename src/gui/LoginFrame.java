package gui;

import controller.AuthenticationController;
import dao.exceptions.NotFoundException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {
    static final int width = 320;
    static final int height = 240;

    public LoginFrame() {
        JFrame frame = this;
        setSize(width, height);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("User");
        JTextField usernameText = new JTextField();
        usernameLabel.setBounds(32, 48, 100, 35);
        usernameText.setBounds(140, 48, 100, 35);
        add(usernameLabel);
        add(usernameText);

        JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordField = new JPasswordField();
        passwordLabel.setBounds(32, 2* 48, 100, 35);
        passwordField.setBounds(140, 2* 48, 100, 35);
        add(passwordLabel);
        add(passwordField);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setBounds(50, 3 * 48, 100, 20);
        add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() {
                        String username = usernameText.getText();
                        String password = String.valueOf(passwordField.getPassword());

                        String token;
                        try {
                            token = AuthenticationController.getInstance().authenticateUser(username, password);
                        } catch (Exception e1) {
//                            e1.printStackTrace();
                            return null;
                        }

                        return token;
                    }

                    @Override
                    protected void done() {
                        String token;
                        try {
                            token = get();
                            if (token == null)
                                throw new NullPointerException();
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(frame, "Invalid password");
                            return;
                        }

                        AppFrame appFrame = new AppFrame(token);
                        frame.dispose();
                    }
                };
                worker.execute();
            }
        });

        JButton registerButton = new JButton("REGISTER");
        registerButton.setBounds(170, 3 * 48, 100, 20);
        add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame registerFrame = new RegisterFrame();
            }
        });

        setVisible(true);
    }
}
