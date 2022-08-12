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

public class ClientFormController {

    public TextField txtClientMessage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public Button btnSend;
    public Button openButton;
    public ImageView myImageView;

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

    private void openFile(File file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
            myImageView.setImage(image);
        } catch (IOException ex) {
            System.out.println("Oops Not Loaded..");
        }
    }

    public void openFileOnAction(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(sp_main.getScene().getWindow());
        if (file != null) {
            openFile(file);
        }
    }
}
