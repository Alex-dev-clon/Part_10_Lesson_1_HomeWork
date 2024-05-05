package server;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {
    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final String LOG_PATH = "src/log.txt";

    List<Client> clientList;

    JButton btnStart;
    JButton btnStop;
    JTextArea textArea;
    boolean isWorking;

    public Server() {
        clientList = new ArrayList<>();
        drawServer();
    }

    private void drawServer() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setTitle("Server");
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        add(textArea);
        add(addButtons(), BorderLayout.SOUTH);
        setVisible(true);
    }
    private Component addButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");

        btnStart.addActionListener(e -> {
            if (isWorking) {
                appendLog("The server is already running");
            } else {
                isWorking = true;
                appendLog("Server is running!");
            }
        });

        btnStop.addActionListener(e -> {
            if (!isWorking) {
                appendLog("The server has already stopped");
            } else {
                isWorking = false;
                while (!clientList.isEmpty()) {
                    disconnectUser(clientList.get(clientList.size() - 1));
                }
                appendLog("Server stopped!");
            }
        });

        panel.add(btnStart);
        panel.add(btnStop);
        return panel;
    }

    public boolean connectUser(Client client) {
        if (!isWorking) {
            return false;
        }
        if (!clientList.contains(client)) {
            clientList.add(client);
            return true;
        }
        return false;
    }

    public String getTextArea() {
        return readLog();
    }

    public void disconnectUser(Client client) {
        clientList.remove(client);
        if (client != null) {
            client.disconnectFromServer();
        }
    }

    public void sendMessage(String text) {
        if (!isWorking) {
            return;
        }
        text += "";
        appendLog(text);
        answerAll(text);
        saveInLog(text);
    }

    private void answerAll(String text) {
        for (Client client : clientList) {
            client.answer(text);
        }
    }

    private void saveInLog(String text) {
        try (FileWriter writer = new FileWriter(LOG_PATH, true)) {
            writer.write(text);
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(LOG_PATH)) {
            int c;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void appendLog(String text) {
        textArea.append(text + "\n");
    }

}
