import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientFormController {

    public TextField txtClientMessage;
    public TextArea txtMgBox;

    Socket socket = null;

    public void initialize() throws IOException {
        new Thread(()->{
            try {
                socket = new Socket("localhost",5000);

                String record="";

                while(true){
                    if(!record.equals("esc")){
                        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        record = bufferedReader.readLine();
                        txtMgBox.appendText(record);
                        System.out.println(record);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(txtClientMessage.getText());
        printWriter.flush();
    }
}
