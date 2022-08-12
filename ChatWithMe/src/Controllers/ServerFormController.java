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
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerFormController {
    public TextField txtSendMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public Button btnSend;
    private final Desktop desktop = Desktop.getDesktop();
    public Button openButton;
    public ImageView myImageView;

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

                        ReceiveImage();
                        String finalRecord = record;

                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                //vbox_message.getChildren().add( new Text( "  Client : "+finalRecord));
                                File file = new File("C:\\Users\\JANITH\\Desktop\\Desktop All Here\\Java All\\" +
                                        "Chat App\\Java Socket Chat-App\\ChatWithMe\\src\\assets\\uploadedImages\\test2.jpg");
                                BufferedImage read = null;
                                try {
                                    read = ImageIO.read(file);
                                } catch (IOException e) {
                                    System.out.println("oh nooo!");
                                }
                                assert read != null;
                                WritableImage image = SwingFXUtils.toFXImage(read, null);
                                ImageView imageView = new ImageView();
                                imageView.setImage(image);
                                imageView.setFitHeight(90);
                                imageView.setFitWidth(140);
                                vbox_message.getChildren().add(imageView);
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

    public void openFileOnAction(ActionEvent actionEvent) {

    }

    public BufferedImage ReceiveImage() throws IOException {
        InputStream inputStream = accept.getInputStream();

        byte[] sizeAr = new byte[4];
        inputStream.read(sizeAr);

        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

        byte[] imageAr = new byte[size];
        inputStream.read(imageAr);


        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
        ImageIO.write(image, "jpg", new File("C:\\Users\\JANITH\\Desktop\\Desktop All Here\\" +
                "Java All\\Chat App\\Java Socket Chat-App\\ChatWithMe\\src\\assets\\uploadedImages\\test2.jpg"));

        return image;
    }
}
