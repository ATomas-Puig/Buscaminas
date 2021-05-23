package main;

import model.Board;
import model.Move;

import java.io.*;
import java.net.*;

public class BuscaminasServer {

    private Board board;
    private int port, multiPort = 5557;
    private boolean end;
    private DatagramSocket socket;

    MulticastSocket multiSocket;
    InetAddress multicastIP;

    public BuscaminasServer(int port) {
        try {
            socket = new DatagramSocket(port);
            multiSocket = new MulticastSocket(multiPort);
            multicastIP = InetAddress.getByName("224.0.0.10");

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.port = port;
        board = new Board();
        board.initBoard();
        end = false;
    }

    public void runServer() throws IOException {
        byte[] receivingData = new byte[1024];
        byte[] sendingData;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(board);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendingData = os.toByteArray();
        DatagramPacket multiPacket = new DatagramPacket(sendingData, sendingData.length, multicastIP, multiPort);
        multiSocket.send(multiPacket);

        do {
            DatagramPacket packet = new DatagramPacket(receivingData, receivingData.length);
            socket.receive(packet);
            sendingData = processData(packet.getData(), packet.getLength());
            multiPacket = new DatagramPacket(sendingData, sendingData.length, multicastIP, multiPort);
            multiSocket.send(multiPacket);
        } while (!end);
        multiSocket.close();
        socket.close();
    }

    private byte[] processData(byte[] data, int length) {
        Move move = null;
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(in);
            move = (Move) ois.readObject();
            System.out.println("Jugador " + move.getPlayer() + ". Coordenadas: " + move.getX() + " " + move.getY());

            if (move.getPlayer() == board.getTurn()) {
                if (board.getBoard()[move.getX()][move.getY()] == 0) {
                    board.getBoard()[move.getX()][move.getY()] = move.getPlayer();
                    board.changeTurn();
                } else if (board.getBoard()[move.getX()][move.getY()] == 3) {
                    board.setLoser(move.getPlayer());
                    board.setEnded(true);
                    end = true;
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(board);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] answer = os.toByteArray();
        return answer;

    }

    public static void main(String[] args) {
        BuscaminasServer buscaminasServer = new BuscaminasServer(5555);
        try {
            buscaminasServer.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
