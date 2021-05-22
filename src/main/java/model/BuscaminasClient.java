package model;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BuscaminasClient {

    private int destinationPort;
    private String srvIp;
    private InetAddress destinationAddress;
    private Board board;
    private Move move;

    NetworkInterface networkInterface;

    public BuscaminasClient(String ip, int port, int player) {
        this.srvIp = ip;
        this.destinationPort = port;
        this.move = new Move();
        this.move.setPlayer(player);

        try {
            networkInterface = NetworkInterface.getByName("wlan1");
        } catch (SocketException e) {
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
        byte[] sendingData = new byte[1024];

        DatagramPacket packet;
        DatagramSocket socket = new DatagramSocket();

        sendingData = firstMove();

        do {
            try {
                packet = new DatagramPacket(sendingData, sendingData.length, destinationAddress, destinationPort);
                socket.send(packet);
                packet = new DatagramPacket(receivedData, 1024);
                socket.receive(packet);
                sendingData = processData(packet.getData(), packet.getLength());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!board.isEnded());
    }

    private byte[] firstMove() {
        printFirstBoard();
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce tu jugada:");
        System.out.print("Introduce la coordenada X: ");
        int x = sc.nextInt();
        System.out.print("Introduce la coordenada Y: ");
        int y = sc.nextInt();
        move.setX(x);
        move.setY(y);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(move);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] answer = os.toByteArray();
        return answer;
    }


    private byte[] processData(byte[] data, int length) {
        Scanner sc = new Scanner(System.in);
        boolean validPlay = false;

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            ois = new ObjectInputStream(in);
            board = (Board) ois.readObject();

            if (board.isEnded()) {
                if (board.getLoser() == move.getPlayer()) {
                    System.out.println("Qué pena pringado, te ha reventado una puta bomba en la cara y has perdido.");
                } else {
                    System.out.println("¡Enhorabuena pichón, has ganado!");
                }
                System.out.println(printEndedBoard(board));
            } else {
                System.out.println(printBoard(board));
            }

            while (!validPlay && !board.isEnded()) {
                if (board.getTurn() != move.getPlayer()) {
                    move.setX(-1);
                    move.setY(-1);

                    try {
                        oos = new ObjectOutputStream(os);
                        oos.writeObject(move);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] answer = os.toByteArray();
                    return answer;

                } else {
                    System.out.println("Introduce tu jugada:");
                    System.out.print("Introduce la coordenada X: ");
                    int x = sc.nextInt();
                    System.out.print("Introduce la coordenada Y: ");
                    int y = sc.nextInt();

                    if (board.getBoard()[x][y] == 1 || board.getBoard()[x][y] == 2) {
                        System.out.println("La jugada no es válida, por favor, introduce una jugada válida:");
                    } else {
                        move.setX(x);
                        move.setY(y);
                        validPlay = true;
                    }

                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(move);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] answer = os.toByteArray();
        return answer;


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

    public void printFirstBoard() {
        String s = "";
        for (int i = 0; i < 8; i++) {
            s = s + (i);
            for (int j = 0; j < 8; j++) {
                s = s + "[ ]";
                if (i == 8) {
                    System.out.println(i);
                }
            }
            s = s + "\n";
        }
        System.out.println(s);
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
