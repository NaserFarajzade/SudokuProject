import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    Socket client;
    private BufferedReader in;
    private PrintWriter out;

    private final int clientNumber;
    private final int opponentNumber ;

    private final int[][] table;
    private final int[][] keyTable;

    private final int[] locks;
    private final int[] turn;
    private final int[] scores;


    public ClientHandler(Socket client,
                         int clientNumber,
                         int[] locks,
                         int[] turn,
                         int[][] keyTable,
                         int[][] table,
                         int[] scores
    ){
        this.client = client;
        this.clientNumber = clientNumber;
        this.table = table;
        this.locks = locks;
        this.turn = turn;
        this.opponentNumber = clientNumber == 0 ? 1 : 0 ;
        this.keyTable = keyTable;
        this.scores = scores;
        initIO();
    }

    private void initIO() {
        try{
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Read/write failed");
            System.exit(-1);
        }
    }

    @Override
    public synchronized void run() {

        out.println("youare client :" + clientNumber);
        out.println("turn cl0");
        turn[clientNumber] = 0;

        while(true){
            try{
                if (in.ready()) {

                    String line = in.readLine();
                    if (line != null) {
                        System.out.println("(passed from client :" + clientNumber + ")");
                        int i = Integer.parseInt(line.split(" ")[0]);
                        int j = Integer.parseInt(line.split(" ")[1]);
                        int value = Integer.parseInt(line.split(" ")[2]);

                        System.out.println("received data : i="+i+" j="+j+" value="+value);
                        gameLogic(i , j , value , out);

                        synchronized (locks) {
                            locks[opponentNumber] = 1;
                            returnDataToClients();
                        }
                    }
                }
                //call another thread to update its client
                if (locks[clientNumber] == 1){
                    returnDataToClients();
                    locks[clientNumber] = 0;
                    System.out.println("data backed to client : " + clientNumber);
                }

            } catch (IOException e) {
                System.out.println("Read failed");
                System.exit(-1);
            }
        }
    }

    private void gameLogic(int i, int j, int value, PrintWriter out) {

        if (keyTable[i][j] == value){
            table[i][j] = value;
            scores[clientNumber]++;
        }else {
            out.println("wrong answer");
        }

    }

    private void returnDataToClients() {
        int sum = 0;

        //table
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("nums ");

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                stringBuilder.append(table[i][j] + " ");
                sum += table[i][j];
            }
        }
        out.println(stringBuilder.toString());

        //turn changed
        out.println("turn changed");

        //scores
        out.println("score :" + scores[0] + ":" + scores[1]);

        //who won?
        if(sum == 405){
            if (scores[0] == scores[1]){
                out.println("winner : no body");
            }else if (scores[0] > scores[1]){
                out.println("winner : cl" + 0 );
            }else{
                out.println("winner : cl" + 1 );
            }
        }
    }
}
