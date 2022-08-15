package handler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("Server " + clientUsername + " has entered the chat!");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        String messageFromClient;

        while(socket.isClosed()){
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                break;
            }
        }
    }

    private void broadcastMessage(String messageFromClient) throws IOException {
        for(ClientHandler clientHandler : clientHandlers){
            if(!clientHandler.clientUsername.equals(clientUsername)){
                try {
                    clientHandler.bufferedWriter.write(messageFromClient);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket,bufferedWriter,bufferedReader);
                }
            }
        }
    }

    public void removeClientHandler() throws IOException {
        clientHandlers.remove(this);
        broadcastMessage("Server:" + clientUsername + " has left the chat !");
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) throws IOException {
        removeClientHandler();

        if(bufferedReader != null){
            bufferedReader.close();
        }
        if(bufferedWriter != null){
            bufferedWriter.close();
        }
        if(socket != null){
            socket.close();
        }
    }
}
