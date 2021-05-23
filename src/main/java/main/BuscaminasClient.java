package main;

import model.Board;
import model.Move;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BuscaminasClient {

    private Board board;
    private Move move;
    private int destinationPort;
    private String srvIp;
    private InetAddress destinationAddress;

    private MulticastSocket multiSocket;
    private InetAddress multicastIP;

    InetSocketAddress groupMulticast;
    NetworkInterface networkInterface;

    public BuscaminasClient(String ip, int port, int player) {
        this.srvIp = ip;
        this.destinationPort = port;
        this.move = new Move();
        this.move.setPlayer(player);

        try {
            multiSocket = new MulticastSocket(5557);
            multicastIP = InetAddress.getByName("224.0.0.10");
            groupMulticast = new InetSocketAddress(multicastIP, 5557);
            networkInterface = NetworkInterface.getByName("wlan2");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            destinationAddress = InetAddress.getByName(srvIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void runClient() throws IOException {
        byte[] receivedData = new byte[1024];
        byte[] sendingData;

        DatagramPacket packet;
        DatagramSocket socket = new DatagramSocket();

        multiSocket.joinGroup(groupMulticast, networkInterface);
        System.out.println("Esperando respuesta del servidor.");

        do {
            try {
                DatagramPacket multiPacket = new DatagramPacket(receivedData, receivedData.length);
                multiSocket.receive(multiPacket);
                sendingData = processData(multiPacket.getData(), multiPacket.getLength());

                if (board.getTurn() == this.move.getPlayer() && sendingData != null) {
                    packet = new DatagramPacket(sendingData, sendingData.length, destinationAddress, destinationPort);
                    socket.send(packet);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!board.isEnded());
        multiSocket.leaveGroup(groupMulticast, networkInterface);
        socket.close();
    }

    private byte[] processData(byte[] data, int length) {
        Scanner sc = new Scanner(System.in);
        boolean validPlay = false;
        int x = -1;
        int y = -1;

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            ois = new ObjectInputStream(in);
            board = (Board) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(printBoard(board));

        if (!board.isEnded()) {
            if (board.getTurn() == this.move.getPlayer()) {

                System.out.println("Es tu turno. Introduce las coordenadas:");

                while (!validPlay) {
                    System.out.println("X: ");
                    x = sc.nextInt();
                    System.out.println("Y: ");
                    y = sc.nextInt();

                    if (board.getBoard()[x][y] == 1 || board.getBoard()[x][y] == 2)
                        System.out.println("La jugada no es válida. Introduce de nuevo las coordenadas.");
                    else validPlay = true;
                }

                move.setX(x);
                move.setY(y);

                try {
                    oos = new ObjectOutputStream(os);
                    oos.writeObject(move);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return os.toByteArray();

            } else {
                System.out.println("Es el turno de tu oponente. Por favor, espera.");
            }
        } else if (board.getLoser() == move.getPlayer()) {
            System.out.println("Has perdido.");
            System.out.println(printEndedBoard(board));
        } else {
            System.out.println("Has ganado.");
            System.out.println(printEndedBoard(board));
        }

        return null;
    }

    public String printBoard(Board board) {
        String s = "";
        for (int i = 0; i < board.getBoard().length; i++) {
            s = s + (i);
            for (int j = 0; j < board.getBoard()[i].length; j++) {
                if (board.getBoard()[i][j] == 0) {
                    s = s + "[ ]";
                } else if (board.getBoard()[i][j] == 3) {
                    s = s + "[ ]";
                } else if (board.getBoard()[i][j] == 1) {
                    s = s + "[1]";
                } else if (board.getBoard()[i][j] == 2) {
                    s = s + "[2]";
                }

                if (i == 8) {
                    System.out.println(j);
                }
            }
            s = s + "\n";
        }
        return s;
    }

    public String printEndedBoard(Board board) {
        String s = "";
        for (int i = 0; i < board.getBoard().length; i++) {
            s = s + (i);
            for (int j = 0; j < board.getBoard()[i].length; j++) {
                if (board.getBoard()[i][j] == 0) {
                    s = s + "[ ]";
                } else if (board.getBoard()[i][j] == 3) {
                    s = s + "[*]";
                } else if (board.getBoard()[i][j] == 1) {
                    s = s + "[1]";
                } else if (board.getBoard()[i][j] == 2) {
                    s = s + "[2]";
                }

                if (i == 8) {
                    System.out.println(j);
                }
            }
            s = s + "\n";
        }
        return s;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("¿Qué número de jugador eres? 1/2");
        int player = scanner.nextInt();
        BuscaminasClient buscaminasClient = new BuscaminasClient("localhost", 5555, player);
        try {
            buscaminasClient.runClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
