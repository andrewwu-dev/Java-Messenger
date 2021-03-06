/*
 * HostMenu
 * GUI program for Host window
 * @Authors: Andrew, Connor
 * Date: December 13, 2017
 */

//imports:
//awt
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//swing
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

//io
import java.io.PrintWriter;
import java.io.FileWriter;

public class HostMenu {
 // global declarations:
 // frame
 private JFrame frame;

 // panel
 JPanel panel;

 // text fields
 private JTextField portField;
 private JTextField nameField;
 private JTextField passwordField;
 private JTextField ipField;
 
 //JLabel
 private JLabel portLabel;
 private JLabel nameLabel;
 private JLabel ipLabel;
 private JLabel passwordLabel;

 //JButton
 private JButton createServerButton;
 private JButton backButton;
 private JLabel hostMenuButton;
 
 //GroupLayout
 private GroupLayout gl_panel;
 
 /**
  * Create the application.
  */
 public HostMenu() {
  initialize();
  frame.setVisible(true);
 }// end of HostMenu constructor
 
 /*
  * writeFile
  * @Author Andrew
  * method that writes password to a text file
  * @param String
  * returns nothing
  */
 public void writeFile(String password) {
   PrintWriter pw = null;
   
   try {
     //write password into a textfile
     pw = new PrintWriter(new FileWriter("password.txt"));
     pw.write(password);
     pw.close();
   }catch(Exception e) {
     System.out.println("Writing to file failed");
   }
 }//end of writeFile

 /**
  * Initialize the contents of the frame.
  */
 private void initialize() {
  // initialize JFrame
  frame = new JFrame();
  frame.setBounds(100, 100, 580, 530);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  // initialize panel
  panel = new JPanel();
  
  //initialize JLabels:
  //host menu label
  hostMenuButton = new JLabel("Host Menu");
  hostMenuButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
  passwordLabel = new JLabel("Password:");
  passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
  
  //initialize JTextFields:
  //portField
  portField = new JTextField();
  portField.setFont(new Font("Tahoma", Font.PLAIN, 18));
  portField.setColumns(10);
  //nameField
  nameField = new JTextField();
  nameField.setFont(new Font("Tahoma", Font.PLAIN, 18));
  nameField.setColumns(10);
  //passwordField
  passwordField = new JTextField();
  passwordField.setFont(new Font("Tahoma", Font.PLAIN, 18));
  passwordField.setColumns(10);
  //ipField
  ipField = new JTextField();
  ipField.setFont(new Font("Tahoma", Font.PLAIN, 18));
  ipField.setColumns(10);
  
  //initialize JLabels:
  //portLabel
  portLabel = new JLabel("Server Port:");
  portLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
  //nameLabel
  nameLabel = new JLabel("Screen Name:");
  nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
  //ipLabel
  ipLabel = new JLabel("IP:");
  ipLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
  
  //initialize buttons:
  //createServerButton
  createServerButton = new JButton("Create Server");
  createServerButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
  createServerButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    //get information from fields
    String password = passwordField.getText();
    String name = nameField.getText();
    String port = portField.getText();
    String ip = ipField.getText();
    
    //write server password to file
    writeFile(password);
    //create new main window
    HostChatGUI hostWindow = new HostChatGUI(password,Integer.parseInt(port),ip,name);
    //close HostMenu
    frame.dispose();
   }
  });
  
  //backButton
  backButton = new JButton("Back");
  backButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
  backButton.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    //create new main menu window
    StartGUI startWindow = new StartGUI();
    //close HostMenu
    frame.dispose();
   }
  });
  
  //GroupLayout setup
  gl_panel = new GroupLayout(panel);
  gl_panel.setHorizontalGroup(
  	gl_panel.createParallelGroup(Alignment.TRAILING)
  		.addGroup(gl_panel.createSequentialGroup()
  			.addGap(225)
  			.addComponent(createServerButton)
  			.addContainerGap(196, Short.MAX_VALUE))
  		.addGroup(gl_panel.createSequentialGroup()
  			.addGap(234)
  			.addComponent(hostMenuButton)
  			.addContainerGap(249, Short.MAX_VALUE))
  		.addGroup(gl_panel.createSequentialGroup()
  			.addContainerGap(103, Short.MAX_VALUE)
  			.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
  				.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
  					.addComponent(ipLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
  					.addComponent(portLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
  					.addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
  				.addComponent(passwordLabel))
  			.addPreferredGap(ComponentPlacement.RELATED)
  			.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
  				.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
  					.addComponent(nameField, Alignment.LEADING)
  					.addComponent(ipField, Alignment.LEADING)
  					.addComponent(portField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
  				.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
  			.addGap(120))
  		.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
  			.addContainerGap()
  			.addComponent(backButton)
  			.addContainerGap(474, Short.MAX_VALUE))
  );
  gl_panel.setVerticalGroup(
  	gl_panel.createParallelGroup(Alignment.TRAILING)
  		.addGroup(gl_panel.createSequentialGroup()
  			.addContainerGap()
  			.addComponent(hostMenuButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
  			.addGap(18)
  			.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
  				.addComponent(portField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
  				.addComponent(portLabel))
  			.addGap(18)
  			.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
  				.addComponent(ipField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
  				.addComponent(ipLabel))
  			.addGap(18)
  			.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
  				.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
  				.addComponent(nameLabel))
  			.addGap(18)
  			.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
  				.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
  				.addComponent(passwordLabel))
  			.addGap(126)
  			.addComponent(createServerButton)
  			.addGap(55)
  			.addComponent(backButton)
  			.addGap(30))
  );
  
  //set panel/frame layout to GroupLayout
  panel.setLayout(gl_panel);
  GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
  groupLayout.setHorizontalGroup(
   groupLayout.createParallelGroup(Alignment.LEADING)
    .addComponent(panel, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
  );
  groupLayout.setVerticalGroup(
   groupLayout.createParallelGroup(Alignment.LEADING)
    .addGroup(groupLayout.createSequentialGroup()
     .addComponent(panel, GroupLayout.PREFERRED_SIZE, 489, Short.MAX_VALUE)
     .addContainerGap())
  );
  
  //add GroupLayout to frame
  frame.getContentPane().setLayout(groupLayout);
 }
}
