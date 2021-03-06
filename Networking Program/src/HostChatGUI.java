/*
 * HostChatGUI
 * program that displays the host gui and starts a server
 * @Author: Andrew
 * Date: December 13,2017
 */

//imports:
//util
import java.util.ArrayList;
//awt
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//swing
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.text.DefaultCaret;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

//io
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileWriter;

//net
import java.net.ServerSocket;
import java.net.Socket;

public class HostChatGUI {
 // global declarations:
 // frame
 private JFrame frame;

 // panels
 private JPanel panel;

 // buttons
 private JButton sendButton;
 private JButton leaveButton;
 private JButton startServerButton;
 private JButton kickButton;

 // combobox
 private JComboBox statusComboBox;
 private DefaultComboBoxModel statusComboBoxModel;

 // scroll panes
 private JScrollPane clientScrollPane;
 private JScrollPane messageScrollPane;

 // JTextArea
 private JTextArea messageTextArea;
 private JTextArea chatTextArea;

 // DefaultCaret
 private DefaultCaret caret;

 // labels
 private JLabel serverInfoLabel;
 private JLabel hostNameLabel;
 private JLabel userLabel;

 // Layout
 private GroupLayout gl_panel;

 // JList
 private JList<String> clientList;
 private DefaultListModel<String> clientListModel;

 // ArrayList
 private ArrayList<User> clientArrList;
 private ArrayList clientStream;// stores printwriters for each client
 private ArrayList<String> previousMessages;// stores the last 15 messages in the chat

 // boolean
 private Boolean serverStarted = false;

 // host account info
 private String hostName;
 private String hostIP;
 private int hostPort;
 private String serverPassword;

 /**
  * Create the application.
  */
 public HostChatGUI(String password, int port, String ip, String hostName) {
   //initialize ArrayList connected clients 
   clientArrList = new ArrayList<User>();
   clientArrList.add(new User(hostName));
   // set server variables
   this.serverPassword = password;
   this.hostPort = port;
   this.hostName = hostName;
   this.hostIP = ip;
   // build GUI
   initialize();
 }// end of HostChatGUI constructor
 
 /*
  * startServer
  */
 public class StartServer implements Runnable {
   @Override
   public void run() {
     // initialize ArrayLists
     clientStream = new ArrayList();
     previousMessages = new ArrayList<String>();
     //initialize ServerSocket
     ServerSocket serverSock = null;
     
     try {
       // display on host GUI
       chatTextArea.append("Server Started! \n");
       // declare ServerSocket
       serverSock = new ServerSocket(hostPort);
       // will keep on accepting new clients until server shuts down
       while (serverStarted == true) {
         Socket clientSock = serverSock.accept();
         // create PrintWriter for that client
         PrintWriter pw = new PrintWriter(clientSock.getOutputStream());
         // add to ArrayList of client PrintWriters
         clientStream.add(pw);
         //create a new thread for the client
         Thread t = new Thread(new ConnectionHandler(clientSock, pw));
         t.start();
       }
     } 
     catch (Exception e) {
       e.printStackTrace();
       chatTextArea.append("Connect to a client failed. \n");
     } 
     finally {
       //close ServerSocket
       if (serverSock != null) {
         try {
         serverSock.close();
       } catch (Exception e) {
         System.out.println("Failed to close server");
         e.printStackTrace();
       }
     }
   }
  }
 }// end of startServer
 
 /*
  * ConnectionHandler inner class for connection threads. Manages chat activities
  */
 public class ConnectionHandler implements Runnable {
  // declarations
  private BufferedReader input;
  private PrintWriter output;
  private Socket client;

  /*
   * ConnectionHandler constructor
   */
  public ConnectionHandler(Socket clientSocket, PrintWriter output) {
   // set PrintWriter
   this.output = output;
   try {
    // set Socket
    this.client = clientSocket;
    // set BufferedReader
    InputStreamReader stream = new InputStreamReader(client.getInputStream());
    this.input = new BufferedReader(stream);
   } 
   catch (Exception e) {
    e.printStackTrace();
   }
  }// end of ConnectionHandler constructor

  @Override
  public void run() {
   // TODO Auto-generated method stub
    // declarations
    String message;// the entire String received from client PrintWriter
    String command,data;
    
    try {
      // Keep reading from clients until server shuts down
      while (serverStarted == true) {
        if (input.ready()) {
          // read from client
          message = input.readLine();
          System.out.println("(SERVER)Message received from client" + message);
          
          //prevents from index out of bounds
          if (message.length() >= 4) {
            // seperate message String into a command code and data
            command = message.substring(0, 4);
            message = message.substring(4);
            data = message;
            
            // connect command
            if (command.equals("@001")) {
              // send message to other clients
              globalDisplay(data + " has connected to the chat!");
              // their jlist
              chatTextArea.append(data + " has connected to the chat! \n");
              // add to ArrayList of "User"s
              addClient(data);
              // send new client list info to all clients
              sendClientList();
              sendPreviousMessages(output);
            }
            // disconnect command
            else if (command.equals("@002")) {
              // send message to all clients
              globalDisplay(data + " has disconnected!");
              chatTextArea.append(data + " has disconnected! \n");
              // remove client from ArrayList of "User"s
              removeClient(data);
              // send new client list info to all clients
              sendClientList();
            }
            // whisper to host command
            else if (command.equals("@003")) {
              // divide info
              String fromUser = message.substring(0, message.indexOf(" "));
              message = message.substring(message.indexOf(" ") + 1);
              String toUser = message.substring(0, message.indexOf(" "));
              message = message.substring(message.indexOf(" ") + 1);
              
              // relay private message
              relayPrivateMessage(message, fromUser, toUser);
            }
            // if a client has updated their status
            else if (command.equals("@006")) {
              //divide info
              String name = data.substring(0,data.indexOf(" "));
              data = data.substring(data.indexOf(" ") + 1);
              String status = data;
              
              //find the client in ArrayList and update their status
              for(int i = 0;i < clientArrList.size();i++){
                if(clientArrList.get(i).getName().equals(name)){
                  clientArrList.get(i).setStatus(status);
                }
              }
              //update chat area 
              chatTextArea.append(name + " is now " + status.toLowerCase() + ". \n");
              //send info to other clients
              globalDisplay(name + " is now " + status.toLowerCase() + ".");
              sendClientList();
              //refresh host JList
              updateJList();
            }
            // normal chat command
            else if (command.equals("@000")) {
              // separate client name and message
              String name = data.substring(0, data.indexOf(" "));
              data = data.substring(data.indexOf(" ") + 1);
              // display on host gui
              chatTextArea.append(name + ": " + data + " \n");
              // send message to all clients
              globalDisplay(name + ": " + data);
            }
            // failure to get client info
            else {
              System.out.println("Failed to received data from client");
            }
          }
        }
      }
    } 
    catch (Exception e) {
      e.printStackTrace();
      clientStream.remove(client);
    } 
    finally {
      // close BufferedReader,PrintWriter, and Socket when server shuts down
      try {
        if (input != null) {
          input.close();
        }
        if (output != null) {
          output.close();
        }
        if (client != null) {
          client.close();
        }
      } catch (Exception e) {
        
      }
    }
  }// end of run
 }// end of ConnectionHandler inner class

 /*
  * globalDisplay 
  * method that displays user messages on the global chat JTextArea
  * @params String
  * returns nothing
  */
 public void globalDisplay(String message) {
   // prevents adding connection messages to ArrayList
   // doing so will cause the same connection message to appear twice on client's
   // chat
   if (message.indexOf("has connected to the chat!") == -1) {
     // store 15 messages in ArrayList
     if (previousMessages.size() > 14) {
       previousMessages.remove(0);
       previousMessages.add(message);
     } 
     else {
       previousMessages.add(message);
     }
   }
   
   // loops through ArrayList of client PrintWriters and outputs message to each
   for (int i = 0; i < clientStream.size(); i++) {
     try {
       PrintWriter pw = (PrintWriter) (clientStream.get(i));
       pw.println(message);
       pw.flush();
     } 
     catch (Exception e) {
       e.printStackTrace();
     }
   }
 }// end of globalDisplay
 
 /*
  * addClient 
  * method that takes in the client name as a String and creates a new
  * User object, and then adds it to ArrayList of clients 
  * @param String 
  * returns nothing
  */
 public void addClient(String name) {
   // add new User object to client ArrayList
   clientArrList.add(new User(name));
   
   //refresh host JList
   updateJList();
 }// end of addClient
 
 /*
  * removeClient 
  * method that takes in client name as a String, finds the name in
  * client ArrayList and remove the object
  * @param String
  * returns nothing
  */
 public void removeClient(String name){
   if (!name.equals(hostName)) {
     // find specific User object and remove from client ArrayList
     for (int i = 0; i < clientArrList.size(); i++) {
       if (clientArrList.get(i).getName().equals(name)) {
         // remove from ArrayList of User objects
         clientArrList.remove(i);
         // remove client's stream
         PrintWriter tempPW = (PrintWriter) (clientStream.get(i - 1));
         clientStream.remove(i - 1);
         tempPW.close();
       }
     }
   }
   //remove the host from ArrayList, only happen when host leaves server
   else{
     clientArrList.remove(0);
   }
   
   //refresh host JList
   updateJList();
 }// end of removeClient
 
 /*
  * sendKick 
  * method that sends kick command to a specified client
  * @param String
  * returns nothing
  */
 public void sendKick(String name) {
   // declare PrintWriter
   PrintWriter pw = null;
   
   try {
     // find the client's PrintWriter
     for (int i = 0; i < clientArrList.size(); i++) {
       if (name.equals(clientArrList.get(i).getName())) {
         pw = (PrintWriter) (clientStream.get(i - 1));
       }
     }
     
     // send kick command
     pw.println("@004");
     pw.flush();
   } catch (Exception e) {
     System.out.println("Failed to send kick info.");
   }
 }//end of sendKick
 
 /*
  * updateJList
  * method that refresh the JList of User objects
  * @params nothing
  * returns nothing
  */ 
 public void updateJList(){
   // refresh JList:
   clientListModel.removeAllElements();
   
   // add every other user to JList
   for (int i = 0; i < clientArrList.size(); i++) {
     clientListModel.addElement(clientArrList.get(i).getName());
   } 
 }//end of updateJList
 
 /*
  * sendPrivateMessage 
  * method that sends a private message from the host to
  * another client
  * @param the message as a String and the selected index of the client JList
  * returns nothing
  */
 public void sendPrivateMessage(String message, int index) {
   // declare PrintWriter
   PrintWriter pw = null;
   
   try {
     // get the PrintWriter of the client
     pw = (PrintWriter) (clientStream.get(index - 1));
     // send message
     pw.println("@003" + hostName + " whispered: " + message);
     pw.flush();
   } catch (Exception e) {
     System.out.println("Failed sending private message (host)");
   }
 }//end of sendPrivateMessage

 /*
  * relayPrivateMessage 
  * method that relays private message between other clients 
  * @param String message and String of the name of the client who is receiving
  * the message 
  * returns nothing
  */
 public void relayPrivateMessage(String message, String fromUser, String toUser) {
   //index variables
   int to = 0, from = 0;
   //add new line function to message
   message = (message + " \n");
   
   // find the index of receiver and sender in the client ArrayList
   for (int i = 1; i < clientArrList.size(); i++) {
     if (clientArrList.get(i).getName().equals(toUser)) {
       to = i;
     } else if (clientArrList.get(i).getName().equals(fromUser)) {
       from = i;
     }
   }
   
   //client sending to another client
   if (!toUser.equals(hostName)) {
     //initialize PrintWriters
     PrintWriter pwSender = null;
     PrintWriter pwReceiver = null;
     
     try {
       // get the PrintWriter of clients
       pwSender = (PrintWriter) (clientStream.get(from - 1));
       pwReceiver = (PrintWriter) (clientStream.get(to - 1));
       // send message to both clients
       pwSender.println("(WHISPER)" + fromUser + " whispered: " + message);
       pwSender.flush();
       pwReceiver.println("(WHISPER)" + fromUser + " whispered: " + message);
       pwReceiver.flush();
     } 
     catch (Exception e) {
       System.out.println("Failed to relay private message.");
     }
   }
   //client sending to host
   else {
     //update chat area
     chatTextArea.append("(WHISPER)" + fromUser + " whispered: " + message);
     
     //initialize PrintWriter
     PrintWriter pwSender = null;
     
     try {
       pwSender = (PrintWriter) (clientStream.get(from - 1));
       // send message
       pwSender.println("(WHISPER)" + fromUser + " whispered: " + message);
       pwSender.flush();
     } 
     catch (Exception e) {
       System.out.println("Failed to receive private message");
     }
   }
 }//end of relayPrivateMessage

 /*
  * sendClientList 
  * method that sends the host's ArrayList of clients to all other
  * clients
  * @param nothing
  * @returns nothing
  */
 public void sendClientList() {
  // String of all client names
  String names = "";
  
  // get all names of online clients
  for (int i = 0; i < clientArrList.size(); i++) {
    names += (clientArrList.get(i).getName() + "," + clientArrList.get(i).getStatus() + " ");
  }

  // send information to all clients
  for (int i = 0; i < clientStream.size(); i++) {
   try {
    PrintWriter pw = (PrintWriter) (clientStream.get(i));
    pw.println("@007" + names);
    pw.flush();
   } catch (Exception e) {
    e.printStackTrace();
   }
  }
 }// end of sendClientList

 /*
  * sendPreviousMessages 
  * method that sends the last 15 messages in chat to a
  * client
  * @param PrintWriter of client 
  * returns nothing
  */
 public void sendPreviousMessages(PrintWriter clientPW) {
  // declare new PrintWriter
  PrintWriter pw = null;

  try {
   // set PrintWriter to client's
   pw = clientPW;

   // send all messages to client
   for (int i = 0; i < previousMessages.size(); i++) {
    System.out.println(previousMessages.get(i));
    pw.println(previousMessages.get(i));
    pw.flush();
   }
  } catch (Exception e) {

  }
 }// end of sendPreviousMessages

 /**
  * Initialize the contents of the frame.
  */
 private void initialize() {
  // initialize frame
  frame = new JFrame(hostName + "'s Chat");
  frame.setBounds(100, 100, 1280, 800);
  frame.setResizable(false);
  //custom exit method
  frame.addWindowListener(new WindowAdapter(){
    @Override
    public void windowClosing(WindowEvent e){
      if(serverStarted == true) {
	      //remove host from ArrayList
	      removeClient(hostName);
	      //send info to other clients
	      sendClientList();
	      //command the other clients to shut down
	      globalDisplay("@005");
	      //set variable to false inorder to close Sockets and IO
	      serverStarted = false;
	      frame.dispose();
      }
      else {
    	  frame.dispose();
      }
    }
  });

  // initialize JPanel
  panel = new JPanel();
  frame.getContentPane().add(panel, BorderLayout.CENTER);

  // initialize JScrollPane
  clientScrollPane = new JScrollPane();
  messageScrollPane = new JScrollPane();

  // initialize JLabels:
  // hostNameLabel
  hostNameLabel = new JLabel("Username:" + hostName);
  hostNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
  // serverInfoLabel
  serverInfoLabel = new JLabel("IP:" + hostIP + " Port:" + hostPort);
  serverInfoLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
  // users connected label
  userLabel = new JLabel("Users Connected:");
  userLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));

  // initialize buttons:
  // send button
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
       String name = clientList.getSelectedValue();
       // prevents the user from whispering themselves
       if (!name.equals(hostName)) {
        chatTextArea
          .append("(WHISPER)" + hostName + ": " + message.substring(9) + " \n");
        sendPrivateMessage(message.substring(9), selectedIndex);
       } else {
        chatTextArea.append("Can't whisper to yourself! \n");
       }
       // no client is selected
      } else {
       chatTextArea.append("Please select a client to whisper to \n");
      }
      // normal chat
     } else {
      globalDisplay(hostName + ": " + message);
      chatTextArea.append(hostName + ": " + message + " \n");
     }
     // normal chat
    } else {
     globalDisplay(hostName + ": " + message);
     chatTextArea.append(hostName + ": " + message + " \n");
    }
    // set the message JTextArea blank
    messageTextArea.setText("");
   }
  });
  
  // leave server button
  leaveButton = new JButton("Leave Chat");
  leaveButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
  leaveButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if(serverStarted == true) {
	      //remove host from ArrayList
	      removeClient(hostName);
	      //send info to other clients
	      sendClientList();
	      //command other clients to shut down
	      globalDisplay("@005");
	      //close Sockets and IO
	      serverStarted = false;
	      frame.dispose();
      }
      else {
    	  frame.dispose();
      }
    }
  });
  
  // kick button
  kickButton = new JButton("Kick Client");
  kickButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
  kickButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      //get the name of client to kick
      String selectedName = clientList.getSelectedValue();
      
      //prevent host from kicking themselves
      if (!selectedName.equals(hostName)) {
        globalDisplay(selectedName + " has been kicked by the host! \n");
        removeClient(selectedName);
        sendClientList();
      } else {
        chatTextArea.append("You can't kick yourself! \n");
      }
    }
  });

  // start server button
  startServerButton = new JButton("Start Server");
  startServerButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
  startServerButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent arg0) {
     //only start the server if no server has been before
     if (serverStarted == false) {
       serverStarted = true;
       //create new server
       Thread newServer = new Thread(new StartServer());
       newServer.start();
     } else {
       chatTextArea.append("Server already started! \n");
     }
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
    
    //update status
    if(status.equals("Online") && !clientArrList.get(0).getStatus().equals("Online")){
      clientArrList.get(0).setStatus("Online");
      globalDisplay(hostName + " is now online.");
      chatTextArea.append("You went online. \n");
    }
    else if(status.equals("Away") && !clientArrList.get(0).getStatus().equals("Away")){
      clientArrList.get(0).setStatus("Away");
      globalDisplay(hostName + " is now away.");
      chatTextArea.append("You are now away. \n");
    }
    //send info to other clients
    sendClientList();
    updateJList();
   }
  });

  // initialize JTextArea:
  // chatTextArea
  chatTextArea = new JTextArea();
  chatTextArea.setFont(new Font("Tahoma", Font.PLAIN, 18));
  chatTextArea.setWrapStyleWord(true);
  chatTextArea.setLineWrap(true);
  chatTextArea.setEditable(false);
  // set auto-scroll
  caret = (DefaultCaret) chatTextArea.getCaret();
  caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
  
  // messageTextArea
  messageTextArea = new JTextArea();
  // add key listener for messageTextArea
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
        String name = clientList.getSelectedValue();
        // prevents the user from whispering themselves
        if (!name.equals(hostName)) {
         chatTextArea.append(
           "(WHISPER)" + hostName + ": " + message.substring(9) + " \n");
         sendPrivateMessage(message.substring(9), selectedIndex);
        } else {
         chatTextArea.append("Can't whisper to yourself! \n");
        }
        // else if no client is selected
       } else {
        chatTextArea.append("Please select a client to whisper to \n");
       }
       // normal chat
      } else {
       globalDisplay(hostName + ": " + message);
       chatTextArea.append(hostName + ": " + message + " \n");
      }
      // normal chat
     } else {
      globalDisplay(hostName + ": " + message);
      chatTextArea.append(hostName + ": " + message + " \n");
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
  // add the user to JList
  clientListModel.addElement(hostName);
  // manually set selected index on jlist to 0
  clientList.setSelectedIndex(0);

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
  							.addComponent(statusComboBox, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE)
  							.addGap(14))
  						.addGroup(gl_panel.createSequentialGroup()
  							.addComponent(clientScrollPane, GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
  							.addPreferredGap(ComponentPlacement.UNRELATED)))
  					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
  						.addGroup(gl_panel.createSequentialGroup()
  							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  								.addGroup(gl_panel.createSequentialGroup()
  									.addComponent(kickButton)
  									.addGap(18)
  									.addComponent(startServerButton)
  									.addGap(119)
  									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  										.addComponent(serverInfoLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
  										.addComponent(hostNameLabel, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
  									.addGap(62))
  								.addGroup(gl_panel.createSequentialGroup()
  									.addComponent(messageTextArea, GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
  									.addGap(18)))
  							.addComponent(sendButton))
  						.addComponent(messageScrollPane, GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE)))
  				.addComponent(leaveButton)
  				.addComponent(userLabel))
  			.addContainerGap())
  );
  gl_panel.setVerticalGroup(
  	gl_panel.createParallelGroup(Alignment.LEADING)
  		.addGroup(gl_panel.createSequentialGroup()
  			.addContainerGap()
  			.addComponent(userLabel)
  			.addPreferredGap(ComponentPlacement.RELATED)
  			.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
  				.addComponent(messageScrollPane)
  				.addComponent(clientScrollPane, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE))
  			.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  				.addGroup(gl_panel.createSequentialGroup()
  					.addGap(24)
  					.addComponent(statusComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
  					.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
  					.addComponent(sendButton))
  				.addGroup(gl_panel.createSequentialGroup()
  					.addGap(6)
  					.addComponent(messageTextArea, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)))
  			.addGap(18)
  			.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
  				.addComponent(kickButton)
  				.addComponent(startServerButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
  				.addComponent(hostNameLabel))
  			.addPreferredGap(ComponentPlacement.RELATED)
  			.addComponent(serverInfoLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
  			.addGap(31)
  			.addComponent(leaveButton)
  			.addContainerGap())
  );
  
  //add textArea to scrollpane
  messageScrollPane.setViewportView(chatTextArea);
  //add JList to scrollpane
  clientScrollPane.setViewportView(clientList);
  // set panel layout to borderlayout
  panel.setLayout(gl_panel);
  // set frame visible
  this.frame.setVisible(true);
 }//end of initialize
}//end of HostGUI
