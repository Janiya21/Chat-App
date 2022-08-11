import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientFormController {

    public TextField txtClientMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;

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

                        String finalRecord = record;
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                vbox_message.getChildren().add( new Text( "  Server : "+finalRecord));
                            }
                        });

                        System.out.println(record);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        String msg = txtClientMessage.getText();
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(msg);

        Label lbl = new Label(msg + " : You  ");
        HBox hBox=new HBox();
        hBox.getChildren().add(lbl);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        vbox_message.getChildren().add(hBox);
        vbox_message.setSpacing(10);

        printWriter.flush();
    }
}
