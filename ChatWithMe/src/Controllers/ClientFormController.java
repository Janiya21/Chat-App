package Controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientFormController {

    public TextField txtClientMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public Button btnSend;
    public Button openButton;
    public ImageView myImageView;


    public Pane emojiPane;
    public Label lblEmojiOne1;
    public Label lblEmojiOne2;
    public Label lblEmojiOne3;
    public Label lblEmojiOne4;
    public Label lblEmojiOne5;
    public Label lblEmojiOne6;
    public Label lblEmojiOne7;
    public Label lblEmojiOne8;

    Socket socket = null;
    BufferedImage bufferedImage=null;
    Socket textSocket = null;

    public void initialize() throws IOException {

        emojiPane.setVisible(false);

        lblEmojiOne1.setText(new String(Character.toChars(0x1F606)));
        lblEmojiOne2.setText(new String(Character.toChars(0x1F601)));
        lblEmojiOne3.setText(new String(Character.toChars(0x1F602)));
        lblEmojiOne4.setText(new String(Character.toChars(0x1F609)));
        lblEmojiOne5.setText(new String(Character.toChars(0x1F618)));
        lblEmojiOne6.setText(new String(Character.toChars(0x1F610)));
        lblEmojiOne7.setText(new String(Character.toChars(0x1F914)));
        lblEmojiOne8.setText(new String(Character.toChars(0x1F642)));

        new Thread(()->{
            System.out.println("huk");
            try {
                textSocket = new Socket("localhost",4000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

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
        if(!msg.equals("")){
            OutputStream outputStream = textSocket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.println(msg);

            Label lbl = new Label(msg + " : You  ");
            HBox hBox=new HBox();
            hBox.getChildren().add(lbl);
            hBox.setAlignment(Pos.BASELINE_RIGHT);
            vbox_message.getChildren().add(hBox);
            vbox_message.setSpacing(10);
            txtClientMessage.setText("");
            printWriter.flush();
        }else{
            OutputStream outputStream = socket.getOutputStream();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            outputStream.write(size);
            outputStream.write(byteArrayOutputStream.toByteArray());

            WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setFitHeight(90);
            imageView.setFitWidth(140);
            HBox hBox=new HBox();
            hBox.getChildren().add(imageView);
            hBox.setAlignment(Pos.BASELINE_RIGHT);
            vbox_message.getChildren().add(hBox);

            outputStream.flush();
            System.out.println("Flushed By Client: " + System.currentTimeMillis());
        }

    }



    public void emojiClickOnAction(ActionEvent actionEvent) {
        emojiPane.setVisible(true);
    }

    public void emojiClickOnAction1(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne1.getText());
    }

    public void emojiClickOnAction2(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne2.getText());

    }

    public void emojiClickOnAction3(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne3.getText());
    }

    public void emojiClickOnAction4(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne4.getText());
    }

    public void emojiClickOnAction5(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne5.getText());

    }

    public void emojiClickOnAction6(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne6.getText());

    }

    public void emojiClickOnAction7(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne7.getText());

    }

    public void emojiClickOnAction8(ActionEvent actionEvent) {
        txtClientMessage.setText(txtClientMessage.getText()+lblEmojiOne8.getText());

    }

    public void btnDownEmojiBar(ActionEvent actionEvent) {
        emojiPane.setVisible(false);
    }
}
