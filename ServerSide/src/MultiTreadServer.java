import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

public class MultiTreadServer{

    ServerSocket serverSocket;
    public static String ip = "localhost";
    public static int port = 4321;

    ClientHandler clientHandler;
    ClientHandler clientHandler2;
    int[] locks = {1 , 1 };
    int[] turn = {0 , 0 };
    int[] scores = {0 , 0 };


    SudokuGenerator sudokuGenerator;
    public static int[][] table;
    public static int[][] keyTable;
    public static Scanner scanner = new Scanner(System.in);

    public MultiTreadServer(int hardness){
        setLocalWiFiIP();

        sudokuGenerator = new SudokuGenerator(9 , hardness);
        table = sudokuGenerator.getTableForGame();
        keyTable = sudokuGenerator.getKeyTable();

        initializeServer();
        System.out.println("please set ip = " + ip + " and port = " + port + " in clients");

        manageClients();
    }

    private void setLocalWiFiIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;


                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (iface.getDisplayName().contains("Wi-Fi") && addr.getHostAddress().indexOf(':')<0){
                        ip = addr.getHostAddress();
                        System.out.println(iface.getDisplayName() + " " + ip);
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("ip not found");
            throw new RuntimeException(e);
        }
    }

    private void initializeServer() {
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
            System.out.println("server run at " + InetAddress.getByName(ip) + " at port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageClients() {
        try {
            clientHandler = new ClientHandler(serverSocket.accept() , 0 , locks , turn , keyTable , table , scores);
            Thread thread = new Thread(clientHandler);
            thread.start();

            clientHandler2 = new ClientHandler(serverSocket.accept() , 1 , locks , turn , keyTable , table , scores);
            Thread thread2 = new Thread(clientHandler2);
            thread2.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MultiTreadServer(SudokuGenerator.EASY);
    }

}
