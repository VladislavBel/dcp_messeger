package client;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    private Socket clientSocket;
    private Scanner inMesseage;
    private PrintWriter outMesseage;
    private JTextField jtfMesseage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMesseage;
    private String clientName = " ";
    private String getClientName(){
        return this.clientName;
    }

    public ClientWindow(){
        try{
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMesseage = new Scanner(clientSocket.getInputStream());
            outMesseage =new PrintWriter(clientSocket.getOutputStream());
        }
        catch (IOException e){
            e.printStackTrace();
        }

        setBounds(600, 300, 600, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMesseage = new JTextArea();
        jtaTextAreaMesseage.setEditable(false);
        jtaTextAreaMesseage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMesseage);
        add(jsp, BorderLayout.CENTER);
        JLabel jlNumberOfClients = new JLabel("Number of members");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Send");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMesseage = new JTextField("Enter messeage: ");
        bottomPanel.add(jtfMesseage, BorderLayout.CENTER);
        jtfName = new JTextField("Enter your name: ");
        bottomPanel.add(jtfName, BorderLayout.WEST);
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!jbSendMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()){
                    clientName = jtfName.getText();
                    sendMsg();
                    jtfMesseage.grabFocus();
            }
        }
    });
        jtfMesseage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e){
                jtfName.setText("");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        if (inMesseage.hasNext()){
                            String inMes = inMesseage.nextLine();
                            String clientsInChat = "Clients in chat";
                            if (inMes.indexOf(clientsInChat) == 0){
                                jlNumberOfClients.setText(inMes);
                            } else{
                                jtaTextAreaMesseage.append(inMes);
                                jtaTextAreaMesseage.append("\n");
                            }
                        }
                    }
                } catch (Exception e){
                }
            }
        }).start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try{
                    if (!clientName.isEmpty() && clientName != "Enter your name: "){
                        outMesseage.println(clientName + "exit from chat");
                    } else {
                        outMesseage.println("Member exit from chat without name");
                    }
                    outMesseage.println("##session##end##");
                    outMesseage.flush();
                    outMesseage.close();
                    inMesseage.close();
                    clientSocket.close();
                } catch (IOException exc){

                }
            }
        });
        setVisible(true);
}
        public void sendMsg(){
        String messeageStr = jtfName.getText() + ": " + jtfMesseage.getText();
        outMesseage.println(messeageStr);
        outMesseage.flush();
        jtfMesseage.setText("");
    }
}
