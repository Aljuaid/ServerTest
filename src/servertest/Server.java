/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servertest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author pink
 */
public class Server extends JFrame {
    private JTextField enterField; // input from user
    private JTextArea displayArea; //isplay info to user
    private ObjectOutputStream output; //output Stream to client
    private ObjectInputStream input; // input stream from the client
    private ServerSocket server;// server socket
    private Socket connection; //connection to client
    private int counter =1;//counter for number connecton
    
    //set up the GUI
    public Server(){
        super("Server");
        
       //instanstet a lot of stuff
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(
        new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent event) {
                sendData(event.getActionCommand());
                enterField.setText("");
                
                 }
            
        });
     add(enterField, BorderLayout.NORTH);
     
     //INSTISATE displayAra
     displayArea = new JTextArea();
     //add text erea to jscrollpane
     add(new JScrollPane(displayArea), BorderLayout.CENTER);
     setSize(300, 150);
     setVisible(true);
     
    }
    
    public void runServer(){
        try{
            // creat serverSocket
            server = new ServerSocket(12345, 100);
            
            while(true){//all the five step
                try{
                    waitForConnection();
                    getStream();//get input and output Streams
                    processConnection();
                }catch(EOFException eofException){//end of file
                    displayMessage("\nServer terminated connection");
                }finally{
                    closeConnection();
                    counter++;
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }//end method runSrever

private void waitForConnection() throws IOException{
    displayMessage("Waiting for connection\n");
    connection = server.accept(); //allow server to accept connection
    displayMessage("Connection " + counter + " recieved from" +
            connection.getInetAddress().getHostName());   
}//end method waitFromConnection

private void getStream() throws IOException{
    //setup output stream for objects
    output = new ObjectOutputStream( connection.getOutputStream());
    output.flush();// flush output buffer to send header information
    
    //set up input stream for objects
    input = new ObjectInputStream(connection.getInputStream());
    
    displayMessage("\nGot I/O stream\n");
}

//process conection with client
private void processConnection () throws IOException{
    String message = "Connection successful";
    sendData(message);
    
    //enable enterField
    setTextFieldEditable(true);
    
    do{
        try{
            message = (String) input.readObject(); //read message from client
            displayMessage("\n" + message );
        }catch(ClassNotFoundException ClassNotFoundException){
            displayMessage("\nUnknown object type recieved");
        }
    }while(!message.equals("CLIENT>>> TERMINATE"));
    
}//end method processConnection

private void closeConnection(){
    displayMessage("\nTerminating connection\n");
    setTextFieldEditable(false);
    
    try{
        output.close();
        input.close();
        connection.close();
    }catch(IOException ioException){
        ioException.printStackTrace();
        
    }
}//end method closeConnection


private void sendData(String message){
    try{//in their screen
        output.writeObject("SERVER>>> " + message);
        output.flush();
        //in our screen
        displayMessage("\nSEREVER " + message);
    }catch(IOException ioException){
        displayArea.append("\nErroe writing object");
        
        
    }
}//end method senData
 private void displayMessage(final String messageToDisplay){
     SwingUtilities.invokeLater(
            new Runnable() {

               public void run(){
                    displayArea.append(messageToDisplay);
               }  
                 
             });
 }//end method displayMessage
private void setTextFieldEditable(final boolean editable){
     SwingUtilities.invokeLater(
            new Runnable() {

               public void run(){
                    enterField.setEditable(editable);
               }  
                 
             });
 }//end method displayMessage
}
