package Controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerFormController {
    public TextField txtSendMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public Button btnSend;
    public Button openButton;
    public ImageView myImageView;
    public Label lblEmojiOne;

    Socket accept=null;
    Socket acceptText=null;

    public void initialize(){

        String context = new String(Character.toChars(0x1F349));
        lblEmojiOne.setText(context);

        HBox hBox = new HBox();
        hBox.setSpacing(20);

        new Thread(() -> {
            try {
                ServerSocket serverSocket1 = new ServerSocket(4000);
                System.out.println("Text Server port Started!");
                acceptText = serverSocket1.accept();
                System.out.println("Client Connected!");

                String record="";

                while(true){
                    InputStreamReader inputStreamReader = new InputStreamReader(acceptText.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    record = bufferedReader.readLine();

                    System.out.println(record + " rec");
                    String finalRecord = record;

                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            vbox_message.getChildren().add( new Text( "  Client : "+finalRecord));
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                System.out.println("Image Server port Started!");
                accept = serverSocket.accept();
                System.out.println("Client Connected!");

                while(true){

                        BufferedImage imageX = ReceiveImage();

                        Platform.runLater(new Runnable() {
                            @Override public void run() {

                                System.out.println("In new thread");
                                WritableImage image = SwingFXUtils.toFXImage(imageX, null);
                                ImageView imageView = new ImageView();
                                imageView.setImage(image);
                                imageView.setFitHeight(90);
                                imageView.setFitWidth(140);
                                vbox_message.getChildren().add(imageView);

                               /* File file = new File("C:\\Users\\JANITH\\Desktop\\Desktop All Here\\Java All\\" +
                                        "Chat App\\Java Socket Chat-App\\ChatWithMe\\src\\assets\\uploadedImages\\test2.jpg");
                                BufferedImage read = null;
                                try {
                                    read = ImageIO.read(file);
                                } catch (IOException e) {
                                    System.out.println("oh nooo!");
                                }*/
                            }
                        });
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

    public void openFileOnAction(ActionEvent actionEvent) {

    }

    public BufferedImage ReceiveImage() throws IOException {
        InputStream inputStream = accept.getInputStream();

        byte[] sizeAr = new byte[4];
        inputStream.read(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
        byte[] imageAr = new byte[size];
        inputStream.read(imageAr);

        /*ImageIO.write(image, "jpg", new File("C:\\Users\\JANITH\\Desktop\\Desktop All Here\\" +
                "Java All\\Chat App\\Java Socket Chat-App\\ChatWithMe\\src\\assets\\uploadedImages\\test2.jpg"));*/

        return ImageIO.read(new ByteArrayInputStream(imageAr));
    }

    public void emojiClickOnAction(ActionEvent actionEvent) {
        txtSendMessage.setText(lblEmojiOne.getText());
    }
}
