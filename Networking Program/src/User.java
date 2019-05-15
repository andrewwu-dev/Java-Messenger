/*
 * User
 * data structures for User object
 * @Author: Andrew
 * Date December 4
 */

class User{
  //declarations
  String name;
  String status;
  String ip;
  
  //empty constructor
  public User() {
    
  }
  
  //constructor 1
  public User(String name) {
    this.name = name;
    this.status = "Online";
  }
  
  //constructor 2
  public User(String name, String status) {
    this.name = name;
    this.ip = ip;
    this.status = status;
  }
  
  //setters
  public void setName(String name) {
    this.name = name;
  }
  
  public void setIP(String ip) {
    this.ip = ip;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
  
  //getters
  public String getName() {
    return this.name;
  }
  
  public String getIP() {
    return this.ip;
  }
  
  public String getStatus() {
    return this.status;
  }
}//end of User