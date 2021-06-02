import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    int port = 4321;
    String ip = "192.168.1.9";
    Socket server;

    private BufferedReader input;
    private PrintWriter out;
    //private GetDataForClient getDataForClient;
    Scanner scanner = new Scanner(System.in);

    private String line;
    private String[] nums;



    private int[][] table = new int[9][9];

    public Client(){
        connectToServer();
        //getDataForClient = new GetDataForClient(input, out , table);
        //Thread thread = new Thread(getDataForClient);
        Thread thread = new Thread(this);
        thread.start();
        communicateToServer();
    }

    private void communicateToServer() {

        while (true){
            String line = scanner.nextLine();
            out.println(line);
        }

    }

    private void connectToServer() {

        boolean scanning=true;
        while(scanning) {
            try {
                server = new Socket(ip , port);
                System.out.println("Client connected to server successfully");
                scanning=false;
            } catch(IOException e) {
                System.out.println("Connect failed, waiting and trying again");
                try {
                    Thread.sleep(2000);//2 seconds
                } catch(InterruptedException ie){
                    ie.printStackTrace();
                }
            }
        }

        try {
            input = new BufferedReader(new InputStreamReader(
                    server.getInputStream()));

            out = new PrintWriter(server.getOutputStream(),
                true);
        } catch (IOException e) {
            System.out.println("Read/write failed");
            e.printStackTrace();
        }

    }

    public void closeConnection(){
        try {
            server.close();
            input.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
    }

    @Override
    public void run() {

        while (true){
            try {
                line = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line.contains("turn")){
                System.out.println(line);
            }else if (line.contains("nums")){
                printNums();
            }else if(line.contains("youare")){
                System.out.println(line);
            }else if (line.contains("wrong")){
                System.out.println(line);
            }else if (line.contains("score")){
                System.out.println(line);
            }else if (line.contains("winner")){
                System.out.println(line);
            }
        }
    }

    private void printNums() {
        nums = line.split(" ");

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                table[i][j] = Integer.parseInt(nums[i* table.length + j +1]);
                System.out.print(table[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("------------------");
    }
}
