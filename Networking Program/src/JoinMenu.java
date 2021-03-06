/*
 * JoinMenu
 * GUI program for joining a server
 * @Authors: Connor & Andrew
 * date: December 9, 2017
 */

//awt
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
//swing
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
//io
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class JoinMenu {

 private JFrame frame;
 private JTextField textJoinIP;
 private JTextField textJoinPort;
 private JTextField textJoinName;
 private JTextField textJoinPass;

 /**
  * Create the application.
  */
 public JoinMenu() {
  initialize();
  frame.setVisible(true);
 }

 /*
  * correctPassword
  * method that checks if user password matches server password
  * @Author Andrew
  * @param String
  * returns boolean
  */
 public boolean correctPassword(String attempt) {
   //declarations
   BufferedReader br = null;
   boolean accepted = false;
   
   try {
     //read from textfile
     br = new BufferedReader(new FileReader("password.txt"));
     String password = br.readLine();
     
     //if theres nothing in text file, set password String to ""
     if(password == null){
       password = "";
     }
     //check if correct password
     if (attempt.equals(password)) {
       accepted = true;
     } else {
       accepted = false;
     }
     br.close();
   } catch (Exception e) {
     
   }
   return accepted;
 }//end of correctPassword

 /**
  * Initialize the contents of the frame.
  * @Author Connor
  */
 private void initialize() {
   //frame
  frame = new JFrame();
  frame.setBounds(100, 100, 580, 530);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  JPanel panel = new JPanel();
  GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
  groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    .addGroup(groupLayout.createSequentialGroup().addContainerGap()
      .addComponent(panel, GroupLayout.PREFERRED_SIZE, 544, Short.MAX_VALUE).addContainerGap()));
  groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    .addGroup(groupLayout.createSequentialGroup().addContainerGap()
      .addComponent(panel, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE).addContainerGap()));

  JLabel lblConnectToServer = new JLabel("Connect To Server");
  lblConnectToServer.setFont(new Font("Tahoma", Font.PLAIN, 16));

  textJoinIP = new JTextField();
  textJoinIP.setFont(new Font("Tahoma", Font.PLAIN, 18));
  textJoinIP.setColumns(10);

  JLabel lblHostIp = new JLabel("Host IP:");
  lblHostIp.setFont(new Font("Tahoma", Font.PLAIN, 18));

  JLabel lblHostPort = new JLabel("Host Port:");
  lblHostPort.setFont(new Font("Tahoma", Font.PLAIN, 18));

  JLabel lblScreenName = new JLabel("Screen Name:");
  lblScreenName.setFont(new Font("Tahoma", Font.PLAIN, 18));
  
  JLabel wrongPassLabel = new JLabel("");

  JButton btnConnect = new JButton("Connect");
  btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 18));
  btnConnect.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
     // Get Connection Info
     String password = textJoinPass.getText();
     
     if (correctPassword(password) == true) {
       String port = textJoinPort.getText();
       String name = textJoinName.getText();
       String ip = textJoinIP.getText();
       
       // writeFile(password);
       ClientChatGUI chatWindow = new ClientChatGUI(Integer.parseInt(port), ip, name);
       frame.dispose();
     }
     else {
       wrongPassLabel.setText("Wrong Password!");
     }
   }
  });

  JButton btnCBack = new JButton("Back");
  btnCBack.setFont(new Font("Tahoma", Font.PLAIN, 18));
  btnCBack.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    StartGUI startWindow = new StartGUI();
    frame.dispose();
   }
  });

  JLabel lblCPassword = new JLabel("Password:");
  lblCPassword.setFont(new Font("Tahoma", Font.PLAIN, 18));

  textJoinPort = new JTextField();
  textJoinPort.setFont(new Font("Tahoma", Font.PLAIN, 18));
  textJoinPort.setColumns(10);

  textJoinName = new JTextField();
  textJoinName.setFont(new Font("Tahoma", Font.PLAIN, 18));
  textJoinName.setColumns(10);

  textJoinPass = new JTextField();
  textJoinPass.setFont(new Font("Tahoma", Font.PLAIN, 18));
  textJoinPass.setColumns(10);
  
  GroupLayout gl_panel = new GroupLayout(panel);
  gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
    .addGroup(gl_panel.createSequentialGroup().addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
      .addGroup(gl_panel.createSequentialGroup().addGap(206).addComponent(lblConnectToServer))
      .addComponent(btnCBack, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
      .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_panel.createSequentialGroup().addContainerGap().addComponent(btnConnect)
          .addPreferredGap(ComponentPlacement.RELATED).addComponent(wrongPassLabel,
            GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE))
        .addGroup(Alignment.LEADING,
          gl_panel.createSequentialGroup().addGap(84)
            .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
              .addComponent(lblCPassword).addComponent(lblScreenName)
              .addComponent(lblHostPort).addComponent(lblHostIp))
            .addGap(18)
            .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
              .addComponent(textJoinPass, GroupLayout.PREFERRED_SIZE, 235,
                GroupLayout.PREFERRED_SIZE)
              .addComponent(textJoinName, GroupLayout.PREFERRED_SIZE, 235,
                GroupLayout.PREFERRED_SIZE)
              .addComponent(textJoinIP, GroupLayout.PREFERRED_SIZE, 235,
                GroupLayout.PREFERRED_SIZE)
              .addComponent(textJoinPort, GroupLayout.PREFERRED_SIZE, 235,
                GroupLayout.PREFERRED_SIZE)))))
      .addContainerGap(98, Short.MAX_VALUE)));
  gl_panel.setVerticalGroup(
    gl_panel.createParallelGroup(Alignment.LEADING)
      .addGroup(gl_panel.createSequentialGroup().addContainerGap()
        .addComponent(
          lblConnectToServer, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        .addGap(18)
        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
          .addComponent(textJoinIP, GroupLayout.PREFERRED_SIZE, 31,
            GroupLayout.PREFERRED_SIZE)
          .addComponent(lblHostIp))
        .addGap(9)
        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
          .addComponent(textJoinPort, GroupLayout.PREFERRED_SIZE, 31,
            GroupLayout.PREFERRED_SIZE)
          .addComponent(lblHostPort))
        .addGap(9)
        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
          .addComponent(textJoinName, GroupLayout.PREFERRED_SIZE, 31,
            GroupLayout.PREFERRED_SIZE)
          .addComponent(lblScreenName))
        .addGap(9)
        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
          .addComponent(textJoinPass, GroupLayout.PREFERRED_SIZE, 31,
            GroupLayout.PREFERRED_SIZE)
          .addComponent(lblCPassword))
        .addPreferredGap(ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(btnConnect)
          .addComponent(wrongPassLabel))
        .addGap(85)
        .addComponent(btnCBack, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)));
  panel.setLayout(gl_panel);
  frame.getContentPane().setLayout(groupLayout);
 }
}
