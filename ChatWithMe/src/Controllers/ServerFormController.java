package Controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {
    public TextField txtSendMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public Button btnSend;

    Socket accept=null;

    public void initialize(){

        HBox hBox = new HBox();
        hBox.setSpacing(20);

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                System.out.println("Server Started!");
                accept = serverSocket.accept();
                System.out.println("Client Connected!");

                String record="";

                while(true){
                    if(!record.equals("esc")){

                        InputStreamReader inputStreamReader = new InputStreamReader(accept.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        record = bufferedReader.readLine();

                        String finalRecord = record;
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                vbox_message.getChildren().add( new Text( "  Client : "+finalRecord));
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
        String mg =  txtSendMessage.getText();
        PrintWriter printWriter = new PrintWriter(accept.getOutputStream());
        printWriter.println(mg);

        Label lbl = new Label(mg + " : You  ");
        HBox hBox=new HBox();
        hBox.getChildren().add(lbl);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        vbox_message.getChildren().add(hBox);
        vbox_message.setSpacing(10);

        printWriter.flush();
    }
}
