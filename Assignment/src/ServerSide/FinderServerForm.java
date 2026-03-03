package ServerSide;

import javax.swing.*;
import java.awt.*;
import java.net.*;

public class FinderServerForm extends JFrame {

    static FinderServerForm instance;

    JTextField portField = new JTextField("5000", 6);
    JButton startBtn    = new JButton("Start");
    JTextArea logArea   = new JTextArea(12, 40);

    public FinderServerForm() {
        super("Finder Server");
        instance = this;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        logArea.setEditable(false);

        JPanel top = new JPanel();
        top.add(new JLabel("Port:"));
        top.add(portField);
        top.add(startBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        startBtn.addActionListener(e -> {
            try {
                int port = Integer.parseInt(portField.getText());
                ServerSocket ss = new ServerSocket(port);
                log("Server started on port " + port);
                startBtn.setEnabled(false);
                new Thread(() -> {
                    try {
                        while (true) {
                            new Thread(new FinderServer(ss.accept())).start();
                        }
                    } catch (Exception ex) { }
                }).start();
            } catch (Exception ex) {
                log("Error: " + ex.getMessage());
            }
        });

        pack();
    }

    public static void log(String msg) {
        if (instance != null)
            SwingUtilities.invokeLater(() -> instance.logArea.append(msg + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinderServerForm().setVisible(true));
    }
}
