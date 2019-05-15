/*
 * ClientChatGUI
 * Program that displays the gui for client and connects to a server
 * @Authors: Connor & Andrew
 * date: December 13, 2017
 */

//imports:
//util
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//awt
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//swing
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;

//io
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;

//net
import java.net.Socket;

public class ClientChatGUI {
 // frame
 private JFrame frame;

 // panels
 private JPanel panel;

 // buttons
 private JButton sendButton;
 private JButton leaveButton;

 // combobox
 private JComboBox statusComboBox;
 private DefaultComboBoxModel statusComboBoxModel;

 // JTextArea
 private JTextArea messageTextArea;
 private JTextArea chatTextArea;

 // DefaultCaret
 private DefaultCaret caret;

 // JLabel
 private JLabel usersLabel;
 private JLabel nameLabel;

 // Layout
 private GroupLayout gl_panel;

 // JList
 private JList<String> clientList;
 private DefaultListModel<String> clientListModel;

 // ArrayList
 private ArrayList<User> clientArrList;

 // user info
 private String name;
 private String ip;
 private int port;

 // Connection Variables
 Socket mySocket;
 BufferedReader input;
 PrintWriter output;
 boolean buildGUI = false;

 /**
  * Create the application.
  */
 public ClientChatGUI(int port, String ip, String name) {
   //set variables
   this.port = port;
   this.ip = ip;
   this.name = name;
   //build gui
   initialize();
   //create new thread
   Thread newConnection = new Thread(new Connection());
   newConnection.start();
   //initialize connected clients ArrayList
   clientArrList = new ArrayList<User>();
 }

 public class Connection implements Runnable {
  @Override
  public void run() {
   try {
    mySocket = new Socket(ip, port);

    InputStreamReader stream1 = new InputStreamReader(mySocket.getInputStream());
    input = new BufferedReader(stream1);

    output = new PrintWriter(mySocket.getOutputStream());
    output.println("@001" + name);
    output.flush();

    Thread t = new Thread(new ServerListener(mySocket, output));
    t.start();
    chatTextArea.append("You have connected! \n");
   } catch (IOException e) {
     e.printStackTrace();
     System.out.println("Failure to connect");
     JoinMenu joinWindow = new JoinMenu();
     frame.dispose();
   }
  }
 }

 public class ServerListener implements Runnable {
  private BufferedReader input;
  private PrintWriter output;
  private Socket server;

  /*
   * ServerListener constructor
   */
  public ServerListener(Socket serverSocket, PrintWriter output) {
   this.output = output;
   try {
    this.server = serverSocket;
    InputStreamReader stream = new InputStreamReader(server.getInputStream());
    this.input = new BufferedReader(stream);
   } catch (Exception e) {
    e.printStackTrace();
   }
  }// end of ServerListener constructor

  @Override
  /*
   * @Author Andrew
   */ 
  public void run() {
   // TODO Auto-generated method stub
   String message;

   // keep looping to read server commands
   while (true) {
    try {
     if (this.input.ready()) {
      // read message from server
      message = this.input.readLine();
      System.out.println("(CLIENT)Message from sever: " + message);
      
      //prevent index out of bounds
      if (message.length() >= 4) {
       // receive all names of clients
       // gets list of all connected client from server
       if (message.substring(0, 4).equals("@007")) {
        // cut off the command String
        message = message.substring(4);
        String name,status;
        
        //empty ArrayList
        clientArrList.clear();
        while (message.indexOf(" ") != -1) {
          //divide info
          name = message.substring(0,message.indexOf(","));
          status = message.substring(message.indexOf(",")+1, message.indexOf(" "));
          //add new updated User object
          clientArrList.add(new User(name,status));
          //shorten message
          message = message.substring(message.indexOf(" ") + 1);
        }

        // update JList
        updateJList();
       }
       // if host leaves
       else if (message.equals("@005")) {
        // output that host has left
        chatTextArea.append("Host has left server! \n");

        // auto close program in 5 seconds
        for (int i = 5; i >= 0; i--) {
         TimeUnit.SECONDS.sleep(1);
         chatTextArea.append("Program will be closing in " + i + " \n");
        }
        frame.dispose();
       }
       // if host kicks user
       else if (message.equals("@004")) {
        // remove everything from JList
        clientListModel.removeAllElements();
        chatTextArea.append("You have been kicked by the host. \n");
        server.close();// end connect to server
       }
       // whisper command
       else if (message.substring(0, 4).equals("@003")) {
        chatTextArea.append("(WHISPER)" + message.substring(4) + " \n");
       }
       // normal chat
       else {
        chatTextArea.append(message + " \n");
       }
      }
     }
    } catch (Exception e) {
     e.printStackTrace();
    }
   }
  }

 }//end of ServerListener
 
 /*
  * updateJList
  * method that refresh JList of connected clients
  * @Author: Andrew
  * @params nothing
  * returns nothing
  */
 public void updateJList() {
   // refresh JList:
   clientListModel.removeAllElements();
   
   // add every other user to JList
   for (int i = 0; i < clientArrList.size(); i++) {
     clientListModel.addElement(clientArrList.get(i).getName());
   }
 }//end of updateJList

 /**
  * Initialize the contents of the frame.
  */
 private void initialize() {
  // initialize frame
  frame = new JFrame(name + "'s chat");
  frame.setBounds(100, 100, 1280, 800);
  frame.setResizable(false);
  //custom exit method
  frame.addWindowListener(new WindowAdapter(){
    @Override
    public void windowClosing(WindowEvent e){
      //send info to server that client disconnected
      output.println("@002" + name);
      output.flush();
      output.close();
      //close frame
      frame.dispose();
    }
  });

  // initialize JPanel
  panel = new JPanel();
  frame.getContentPane().add(panel, BorderLayout.CENTER);

  // initialize JScrollPane
  JScrollPane clientScrollPane = new JScrollPane();
  JScrollPane messageScrollPane = new JScrollPane();

  // initialize labels
  usersLabel = new JLabel("Users Connected:");
  usersLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
  nameLabel = new JLabel("Username:" + name);
  nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));

  // initialize buttons:
  // enter button
  sendButton = new JButton("SEND");
  sendButton.setFont(new Font("Tahoma", Font.PLAIN, 24));
  sendButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent arg0) {
    // get String from JTextArea
    String message = messageTextArea.getText();
    // get selected client that they may want to whisper to
    int selectedIndex = clientList.getSelectedIndex();

    // prevents going index out of bounds
    if (message.length() >= 9) {
     // if user wants to whisper to someone
     if (message.substring(0, 9).equals("/whisper ")) {
      // if user has selected a client
      if (selectedIndex != -1) {
       String selectedName = clientList.getSelectedValue();
       // prevents the user from whispering themselves
       if (!selectedName.equals(name)) {
        output.println("@003" + name + " " + selectedName + " " + message.substring(9) + " \n");
        output.flush();
       } else {
        chatTextArea.append("Can't whisper to yourself! \n");
       }
       // no client is selected
      } else {
       chatTextArea.append("Please select a client to whisper to \n");
      }
      // normal chat
     } else {
      output.println("@000" + name + " " + message);
      output.flush();
     }
     // normal chat
    } else {
     output.println("@000" + name + " " + message);
     output.flush();
    }
    // set the message JTextArea blank
    messageTextArea.setText("");
   }
  });

  // leave server button
  leaveButton = new JButton("Leave Chat");
    leaveButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
  leaveButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent arg0) {
     //send disconnect info to server
     output.println("@002" + name);
     output.flush();
     frame.dispose();
   }
  });

  // initialize JComboBox:
  // setup combo box model
  statusComboBoxModel = new DefaultComboBoxModel();
  // add options to combo box model
  statusComboBoxModel.addElement("Online");
  statusComboBoxModel.addElement("Away");
  // add options to combo box
  statusComboBox = new JComboBox(statusComboBoxModel);
  // set initial index to "Online";
  statusComboBox.setSelectedIndex(0);
  // add listener to combo box
  statusComboBox.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    // get status selected from combo box
     String status = (String) statusComboBox.getSelectedItem();
     User temp = new User();
     String tempStatus = "";
     
     //find this client in ArrayList
     for(int i = 0;i < clientArrList.size();i++){
       if(clientArrList.get(i).getName().equals(name)){
         temp = clientArrList.get(i);
         tempStatus = temp.getStatus();
       }
     }
     
     //update this client's status
     if(status.equals("Online") && !temp.getStatus().equals("Online")){
       temp.setStatus("Online");
       chatTextArea.append("You went online. \n");
       tempStatus = "Online";
     }
     else if(status.equals("Away") && !temp.getStatus().equals("Away")){
       temp.setStatus("Away");
       chatTextArea.append("You are now away. \n");
       tempStatus = "Away";
     }
     
     //send info to server
     try{
       output.println("@006" + name + " " + tempStatus);
       output.flush();
     }
     catch(Exception f){
       System.out.println("Failed to send status update to server");
     }
     //refresh JList
     updateJList();
   }
  });

  // initialize JTextArea
  // chatTextArea
  chatTextArea = new JTextArea();
  chatTextArea.setFont(new Font("Tahoma", Font.PLAIN, 18));
  chatTextArea.setWrapStyleWord(true);
  chatTextArea.setLineWrap(true);
  chatTextArea.setEditable(false);
  // set auto-scroll
  caret = (DefaultCaret) chatTextArea.getCaret();
  caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
  
  // messsageTextArea
  messageTextArea = new JTextArea();
  messageTextArea.addKeyListener(new KeyAdapter() {
   @Override
   public void keyPressed(KeyEvent e) {
    // if user presses enter key
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
     // get String from JTextArea
     String message = messageTextArea.getText();
     // get selected client that they may want to whisper to
     int selectedIndex = clientList.getSelectedIndex();
     e.consume();

     // prevents going index out of bounds
     if (message.length() >= 9) {
      // if user wants to whisper to someone
      if (message.substring(0, 9).equals("/whisper ")) {
       // if user has selected a client
       if (selectedIndex != -1) {
        String selectedName = clientList.getSelectedValue();
        // prevents the user from whispering themselves
        if (!selectedName.equals(name)) {
         output.println("@003" + name + " " + selectedName + " " + message.substring(9) + " \n");
         output.flush();
        } else {
         chatTextArea.append("Can't whisper to yourself! \n");
        }
        // no client is selected
       } else {
        chatTextArea.append("Please select a client to whisper to \n");
       }
       // normal chat
      } else {
       output.println("@000" + name + " " + message);
       output.flush();
      }
      // normal chat
     } else {
      output.println("@000" + name + " " + message);
      output.flush();
     }
     // set the message JTextArea blank
     messageTextArea.setText("");
    }
   }
  });
  messageTextArea.setFont(new Font("Tahoma", Font.PLAIN, 18));
  messageTextArea.setWrapStyleWord(true);
  messageTextArea.setLineWrap(true);

  // initialize JList
  clientListModel = new DefaultListModel<String>();
  clientList = new JList<String>(clientListModel);
  clientList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
  clientList.setVisibleRowCount(-1);
  clientList.setFont(new Font("Tahoma", Font.PLAIN, 18));

  // setup borderlayout
  gl_panel = new GroupLayout(panel);
  gl_panel.setHorizontalGroup(
  	gl_panel.createParallelGroup(Alignment.LEADING)
  		.addGroup(gl_panel.createSequentialGroup()
  			.addContainerGap()
  			.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  				.addGroup(gl_panel.createSequentialGroup()
  					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  						.addGroup(gl_panel.createSequentialGroup()
  							.addComponent(clientScrollPane, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
  							.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
  							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
  								.addComponent(messageScrollPane, GroupLayout.PREFERRED_SIZE, 939, GroupLayout.PREFERRED_SIZE)
  								.addGroup(gl_panel.createSequentialGroup()
  									.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
  										.addComponent(nameLabel, GroupLayout.PREFERRED_SIZE, 338, GroupLayout.PREFERRED_SIZE)
  										.addComponent(messageTextArea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
  									.addGap(9)
  									.addComponent(sendButton))))
  						.addComponent(usersLabel))
  					.addContainerGap())
  				.addGroup(gl_panel.createSequentialGroup()
  					.addComponent(statusComboBox, 0, 285, Short.MAX_VALUE)
  					.addGap(974))
  				.addGroup(gl_panel.createSequentialGroup()
  					.addComponent(leaveButton)
  					.addContainerGap(1140, Short.MAX_VALUE))))
  );
  gl_panel.setVerticalGroup(
  	gl_panel.createParallelGroup(Alignment.LEADING)
  		.addGroup(gl_panel.createSequentialGroup()
  			.addContainerGap()
  			.addComponent(usersLabel)
  			.addPreferredGap(ComponentPlacement.RELATED)
  			.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  				.addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
  				.addComponent(clientScrollPane, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE))
  			.addGap(9)
  			.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  				.addGroup(gl_panel.createSequentialGroup()
  					.addGap(73)
  					.addComponent(sendButton))
  				.addGroup(gl_panel.createSequentialGroup()
  					.addPreferredGap(ComponentPlacement.RELATED)
  					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
  						.addComponent(statusComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
  						.addComponent(messageTextArea, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))))
  			.addGap(47)
  			.addComponent(nameLabel)
  			.addGap(29)
  			.addComponent(leaveButton)
  			.addContainerGap())
  );

  // add JList to ScrollPane
  messageScrollPane.setViewportView(chatTextArea);
  clientScrollPane.setViewportView(clientList);
  // set panel layout to borderlayout
  panel.setLayout(gl_panel);
  frame.setVisible(true);
  buildGUI = true;
 }
}
