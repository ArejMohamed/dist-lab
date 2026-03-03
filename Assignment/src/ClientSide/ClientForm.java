package ClientSide;

import javax.swing.*;
import java.awt.*;
import java.net.*;

public class ClientForm extends JFrame {

    static ClientForm instance;

    JTextField nameField    = new JTextField("Client1", 8);
    JTextField myPortField  = new JTextField("6001", 5);
    JTextField srvHostField = new JTextField("localhost", 9);
    JTextField srvPortField = new JTextField("5000", 5);
    JTextField findField    = new JTextField(10);
    JTextArea  logArea      = new JTextArea(10, 40);

    String  myCode    = "";
    boolean listening = false;

    public ClientForm() {
        super("Client Application");
        instance = this;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        logArea.setEditable(false);

        // Row 1: Name + My Port
        JPanel row1 = new JPanel();
        row1.add(new JLabel("Name:")); row1.add(nameField);
        row1.add(new JLabel("My Port:")); row1.add(myPortField);

        // Row 2: Server Host + Server Port
        JPanel row2 = new JPanel();
        row2.add(new JLabel("Server Host:")); row2.add(srvHostField);
        row2.add(new JLabel("Server Port:")); row2.add(srvPortField);

        // Row 3: Register / Unregister
        JButton regBtn   = new JButton("Register");
        JButton unregBtn = new JButton("Unregister");
        JPanel row3 = new JPanel();
        row3.add(regBtn); row3.add(unregBtn);

        // Row 4: Find Name + Find + Connect
        JButton findBtn    = new JButton("Find");
        JButton connectBtn = new JButton("Connect");
        JPanel row4 = new JPanel();
        row4.add(new JLabel("Find Name:")); row4.add(findField);
        row4.add(findBtn); row4.add(connectBtn);

        JPanel top = new JPanel(new GridLayout(4, 1));
        top.add(row1); top.add(row2); top.add(row3); top.add(row4);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Register
        regBtn.addActionListener(e -> {
            try {
                String result = Client.register(srvHostField.getText(),
                        Integer.parseInt(srvPortField.getText()),
                        nameField.getText(), myPortField.getText());
                log(result);
                if (result.contains("Code: ")) {
                    myCode = result.split("Code: ")[1].trim();
                }
                if (!listening && !myCode.isEmpty()) {
                    listening = true;
                    int p = Integer.parseInt(myPortField.getText());
                    new Thread(() -> {
                        try {
                            ServerSocket ss = new ServerSocket(p);
                            log("Listening for peers on port " + p);
                            while (true) {
                                new Thread(new PeerListener(ss.accept(), myCode)).start();
                            }
                        } catch (Exception ex) { log("Listener error: " + ex.getMessage()); }
                    }).start();
                }
            } catch (Exception ex) { log("Error: " + ex.getMessage()); }
        });

        // Unregister
        unregBtn.addActionListener(e -> {
            try {
                log(Client.unregister(srvHostField.getText(),
                        Integer.parseInt(srvPortField.getText()), nameField.getText()));
            } catch (Exception ex) { log("Error: " + ex.getMessage()); }
        });

        // Find
        findBtn.addActionListener(e -> {
            try {
                log(Client.find(srvHostField.getText(),
                        Integer.parseInt(srvPortField.getText()), findField.getText()));
            } catch (Exception ex) { log("Error: " + ex.getMessage()); }
        });

        // Connect (find then connect to peer using code)
        connectBtn.addActionListener(e -> {
            try {
                String found = Client.find(srvHostField.getText(),
                        Integer.parseInt(srvPortField.getText()), findField.getText());
                log("Found: " + found);
                String[] parts = found.split(":");
                if (parts.length == 3) {
                    log("Peer: " + Client.connectToPeer(parts[0],
                            Integer.parseInt(parts[1]), parts[2]));
                } else {
                    log("Cannot connect: " + found);
                }
            } catch (Exception ex) { log("Error: " + ex.getMessage()); }
        });

        pack();
    }

    public static void log(String msg) {
        if (instance != null)
            SwingUtilities.invokeLater(() -> instance.logArea.append(msg + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientForm().setVisible(true));
    }
}
