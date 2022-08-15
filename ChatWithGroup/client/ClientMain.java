import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9000)){
            //reading the input from server
            BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            
            //returning the output to the server : true statement is to flush the buffer otherwise
            //we have to do it manuallyy
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);

            //taking the user input
            Scanner scanner = new Scanner(System.in);
            String userInput;
            String response;
            String clientName = "empty";

            ClientRunnable clientRun = new ClientRunnable(socket);

            new Thread(clientRun).start();
           //loop closes when user enters exit command
           
           do {

               //reading the input from server
               if (clientName.equals("empty")) {
                    System.out.println("Enter your name ");
                    userInput = scanner.nextLine();
                    clientName = userInput;
                    output.println(userInput);
               }
               else {
                    String message = ( "(" + clientName + ")" + " message : " );
                    System.out.println(message);
                    userInput = scanner.nextLine();
                    output.println(message + " " + userInput);
               }
               if (userInput.equals("exit")) {
                   break;
               }

           } while (!userInput.equals("exit"));
        } catch (Exception e) {
            System.out.println("Exception occured in client main: " + e.getStackTrace());
        }
    }
}