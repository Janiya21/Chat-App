import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    public TextArea txtMsgBox;
    public TextField txtSendMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public Button btnSend;

    Socket accept=null;

    public void initialize(){
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
                        txtMsgBox.setText(record);
                        System.out.println(record);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

       /* // new
        vbox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double)newValue);
            }
        });*/
    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        String mg =  txtSendMessage.getText();
        PrintWriter printWriter = new PrintWriter(accept.getOutputStream());
        printWriter.println(mg);
        printWriter.flush();

       /* //new
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(mg);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-color: rgb(239,242,255); -fx-background-color: rgb(15,125,242); -fx-border-radius: 20px");
        text.setFill(Color.color(0.934, 9.945, 0.996));

        hBox.getChildren().add(textFlow);
        vbox_message.getChildren().add(hBox);*/
    }
}
