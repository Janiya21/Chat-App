package Controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
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

    Socket socket = null;
    BufferedImage bufferedImage=null;
    Socket textSocket = null;

    public void initialize() throws IOException {

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
        OutputStream outputStream = textSocket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(msg);

        Label lbl = new Label(msg + " : You  ");
        HBox hBox=new HBox();
        hBox.getChildren().add(lbl);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        vbox_message.getChildren().add(hBox);
        vbox_message.setSpacing(10);

        printWriter.flush();
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

    public void sendImageOnAction(ActionEvent actionEvent) throws IOException, InterruptedException {
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
