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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerFormController {
    public TextField txtSendMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public Button btnSend;
    public Button openButton;

    public Pane emojiPane;
    public Label lblEmojiOne1;
    public Label lblEmojiOne2;
    public Label lblEmojiOne3;
    public Label lblEmojiOne4;
    public Label lblEmojiOne5;
    public Label lblEmojiOne6;
    public Label lblEmojiOne7;
    public Label lblEmojiOne8;

    Socket accept=null;
    Socket acceptText=null;
    BufferedImage bufferedImage=null;

    public void initialize(){

        emojiPane.setVisible(false);

        lblEmojiOne1.setText(new String(Character.toChars(0x1F606)));
        lblEmojiOne2.setText(new String(Character.toChars(0x1F601)));
        lblEmojiOne3.setText(new String(Character.toChars(0x1F602)));
        lblEmojiOne4.setText(new String(Character.toChars(0x1F609)));
        lblEmojiOne5.setText(new String(Character.toChars(0x1F618)));
        lblEmojiOne6.setText(new String(Character.toChars(0x1F610)));
        lblEmojiOne7.setText(new String(Character.toChars(0x1F914)));
        lblEmojiOne8.setText(new String(Character.toChars(0x1F642)));

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
                            vbox_message.getChildren().add( new Text( "  "+finalRecord));
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
        if(!mg.equals("")){
            PrintWriter printWriter = new PrintWriter(acceptText.getOutputStream());
            printWriter.println(mg);

            Label lbl = new Label(mg + "  ");
            HBox hBox=new HBox();
            hBox.getChildren().add(lbl);
            hBox.setAlignment(Pos.BASELINE_RIGHT);
            vbox_message.getChildren().add(hBox);
            vbox_message.setSpacing(10);
            txtSendMessage.setText("");
            printWriter.flush();
        }else{
            OutputStream outputStream = accept.getOutputStream();
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
            System.out.println("Flushed By Server: " + System.currentTimeMillis());
        }
    }

    public void openFileOnAction(ActionEvent actionEvent) throws IOException {
        final FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(sp_main.getScene().getWindow());
        if (file != null) {
            bufferedImage = ImageIO.read(file);
        }
    }

    public BufferedImage ReceiveImage() throws IOException {
        InputStream inputStream = accept.getInputStream();

        byte[] sizeAr = new byte[4];
        inputStream.read(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
        byte[] imageAr = new byte[size];
        inputStream.read(imageAr);

        return ImageIO.read(new ByteArrayInputStream(imageAr));
    }

    // Emoji Operations

    public void emojiClickOnAction(ActionEvent actionEvent) {
        emojiPane.setVisible(true);
    }

    public void emojiClickOnAction1(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne1.getText());
    }

    public void emojiClickOnAction2(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne2.getText());

    }

    public void emojiClickOnAction3(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne3.getText());
    }

    public void emojiClickOnAction4(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne4.getText());
    }

    public void emojiClickOnAction5(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne5.getText());

    }

    public void emojiClickOnAction6(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne6.getText());

    }

    public void emojiClickOnAction7(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne7.getText());

    }

    public void emojiClickOnAction8(ActionEvent actionEvent) {
        txtSendMessage.setText(txtSendMessage.getText()+lblEmojiOne8.getText());

    }

    public void btnDownEmojiBar(ActionEvent actionEvent) {
        emojiPane.setVisible(false);
    }
}
