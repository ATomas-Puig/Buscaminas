package model;

import java.io.*;
import java.net.*;

public class BuscaminasServer {
    boolean end;
    DatagramSocket socket;
    int port, players_ended, multiPort = 5557;
    Board board;
    MulticastSocket multiSocket;
    InetAddress multicastIP;

    NetworkInterface nInterface;

    public BuscaminasServer(int port) {
        try {
            socket = new DatagramSocket(port);
            multiSocket = new MulticastSocket(multiPort);
            multicastIP = InetAddress.getByName("224.0.0.10");
            nInterface = NetworkInterface.getByName("wlan1");

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.port = port;
        board = new Board();
        board.initBoard();
        end = false;
        players_ended = 0;

    }

    public void runServer() throws IOException {
        byte[] receivingData = new byte[1024];
        byte[] sendingData;
        InetAddress clientIP;
        int clientPort;

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
            //clientIP = packet.getAddress();
            //clientPort = packet.getPort();
            //packet = new DatagramPacket(sendingData, sendingData.length, clientIP, clientPort);
            //socket.send(packet);
            multiPacket = new DatagramPacket(sendingData, sendingData.length, multicastIP, multiPort);
            multiSocket.send(multiPacket);
        } while (!end);
    }

    private byte[] processData(byte[] data, int length) {
        Move move = null;
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(in);
            move = (Move) ois.readObject();


            if (move.getPlayer() == board.getTurn()) {
                if (board.getBoard()[move.getX()][move.getY()] == 0) {
                    board.getBoard()[move.getY()][move.getY()] = move.getPlayer();
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
