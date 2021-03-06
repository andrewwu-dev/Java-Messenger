/*
 * StartGUI
 * Main menu program
 * @Author Connor
 * Date: December 13
 */

//imports
//awt
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

//swing
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

public class StartGUI {

 private JFrame frame;

 /**
  * Launch the application.
  */
 public static void main(String[] args) {
  EventQueue.invokeLater(new Runnable() {
   public void run() {
    try {
     StartGUI window = new StartGUI();
     window.frame.setVisible(true);
    } catch (Exception e) {
     e.printStackTrace();
    }
   }
  });
 }

 /**
  * Create the application.
  */
 public StartGUI() {
  initialize();
  frame.setVisible(true);
 }

 /**
  * Initialize the contents of the frame.
  */
 private void initialize() {
  frame = new JFrame();
  frame.setBounds(100, 100, 580, 530);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  
  JPanel startPanel = new JPanel();
  GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
  groupLayout.setHorizontalGroup(
   groupLayout.createParallelGroup(Alignment.LEADING)
    .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
     .addContainerGap()
     .addComponent(startPanel, GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
     .addContainerGap())
  );
  groupLayout.setVerticalGroup(
   groupLayout.createParallelGroup(Alignment.LEADING)
    .addGroup(groupLayout.createSequentialGroup()
     .addContainerGap()
     .addComponent(startPanel, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
     .addContainerGap())
  );
  
  JLabel tbTitle = new JLabel("Chat Server Menu");
  tbTitle.setFont(new Font("Tahoma", Font.PLAIN, 16));
  
  //initialize buttons:
  
  //host window button:
  JButton btnHost = new JButton("Host");
  btnHost.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent arg0) {
    HostMenu hostWindow = new HostMenu();
    frame.dispose();
   }
  });
  
  //join window button
  JButton btnJoin = new JButton("Join");
  btnJoin.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    JoinMenu joinWindow = new JoinMenu();
    frame.dispose();
   }
  });
  
  GroupLayout gl_startPanel = new GroupLayout(startPanel);
  gl_startPanel.setHorizontalGroup(
   gl_startPanel.createParallelGroup(Alignment.LEADING)
    .addGroup(Alignment.TRAILING, gl_startPanel.createSequentialGroup()
     .addContainerGap(215, Short.MAX_VALUE)
     .addGroup(gl_startPanel.createParallelGroup(Alignment.TRAILING, false)
      .addComponent(btnJoin, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(btnHost, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(tbTitle))
     .addGap(204))
  );
  gl_startPanel.setVerticalGroup(
   gl_startPanel.createParallelGroup(Alignment.LEADING)
    .addGroup(gl_startPanel.createSequentialGroup()
     .addGap(94)
     .addComponent(tbTitle, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
     .addGap(29)
     .addComponent(btnHost)
     .addGap(31)
     .addComponent(btnJoin)
     .addContainerGap(245, Short.MAX_VALUE))
  );
  startPanel.setLayout(gl_startPanel);
  frame.getContentPane().setLayout(groupLayout);
 }
}
