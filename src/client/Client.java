package client;

import server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    private final Server server;
    private boolean connected;
    private String name;

    JTextArea log;
    JTextField tfIPAddress;
    JTextField tfPort;
    JTextField tfLogin;
    JTextField tfMessage;
    JPasswordField password;
    JButton btnLogin;
    JButton btnSend;
    JPanel headerPanel;

    public Client(Server server) {
        this.server = server;
        drawClient();
    }

    private void drawClient() {
        setSize(WIDTH, HEIGHT);
        setTitle("Client");
        setLocation(server.getX() - 500, server.getY());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private Component createHeaderPanel() {
        headerPanel = new JPanel(new GridLayout(2, 3));
        tfIPAddress = new JTextField("127.0.0.1");
        tfPort = new JTextField("8080");
        tfLogin = new JTextField("Ivan");
        password = new JPasswordField("12345");
        btnLogin = new JButton("login");
        btnLogin.addActionListener(e -> connectToServer());

        headerPanel.add(tfIPAddress);
        headerPanel.add(tfPort);
        headerPanel.add(new JPanel());
        headerPanel.add(tfLogin);
        headerPanel.add(password);
        headerPanel.add(btnLogin);

        return headerPanel;
    }

    private Component createLog() {
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    private Component createFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    sendMessage();
                }
            }
        });
        btnSend = new JButton("send");
        btnSend.addActionListener(e -> sendMessage());
        panel.add(tfMessage);
        panel.add(btnSend, BorderLayout.EAST);
        return panel;
    }

    public void answer(String text) {
        appendLog(text);
    }

    private void connectToServer() {
        if (server.connectUser(this)) {
            appendLog("You have successfully connected!\n");
            headerPanel.setVisible(false);
            connected = true;
            name = tfLogin.getText();
            String log = server.getTextArea();
            if (log != null) {
                appendLog(log);
            }
        } else {
            appendLog("Connection failed");
        }
    }

    public void disconnectFromServer() {
        if (connected) {
            headerPanel.setVisible(true);
            connected = false;
            server.disconnectUser(this);
            appendLog("You have been disconnected from the server!");
        }
    }

    public void sendMessage() {
        if (connected) {
            String text = tfMessage.getText();
            if (!text.equals("")) {
                server.sendMessage(name + ": " + text);
                tfMessage.setText("");
            }
        } else {
            appendLog("No connection to server");
        }

    }

    private void appendLog(String text) {
        log.append(text + "\n");
    }


    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            disconnectFromServer();
        }
        super.processWindowEvent(e);
    }
}
