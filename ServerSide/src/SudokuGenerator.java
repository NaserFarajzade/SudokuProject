public class SudokuGenerator {

    private int[][] tableForGame;
    private int[][] keyTable;
    private int N; // number of columns/rows.
    private int SRN; // square root of N
    private int K; // No. Of missing digits
    public static int EASY = 1;
    public static int MEDIUM = 2;
    public static int HARD = 3;

    SudokuGenerator(int N, int hardness) {
        this.N = N;

        if (hardness == 1){
            this.K = 40;
        }else if (hardness == 2){
            this.K = 48;
        } else {
            this.K = 54;
        }

        // Compute square root of N
        Double SRNd = Math.sqrt(N);
        SRN = SRNd.intValue();

        tableForGame = new int[N][N];
        keyTable = new int[N][N];

        this.fillValues();

    }

    // Sudoku Generator
    public void fillValues() {
        // Fill the diagonal of SRN x SRN matrices
        fillDiagonal();

        // Fill remaining blocks
        fillRemaining(0, SRN);

        // Remove Randomly K digits to make game
        removeKDigits();
    }

    // Fill the diagonal SRN number of SRN x SRN matrices
    void fillDiagonal() {

        for (int i = 0; i<N; i=i+SRN)

            // for diagonal box, start coordinates->i==j
            fillBox(i, i);
    }

    // Returns false if given 3 x 3 block contains num.
    boolean unUsedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i<SRN; i++)
            for (int j = 0; j<SRN; j++)
                if (tableForGame[rowStart+i][colStart+j]==num)
                    return false;

        return true;
    }

    // Fill a 3 x 3 matrix.
    void fillBox(int row,int col) {
        int num;
        for (int i=0; i<SRN; i++)
        {
            for (int j=0; j<SRN; j++)
            {
                do
                {
                    num = randomGenerator(N);
                }
                while (!unUsedInBox(row, col, num));

                tableForGame[row+i][col+j] = num;
            }
        }
    }

    // Random generator
    int randomGenerator(int num) {
        return (int) Math.floor((Math.random()*num+1));
    }

    // Check if safe to put in cell
    boolean CheckIfSafe(int i,int j,int num) {
        return (unUsedInRow(i, num) &&
                unUsedInCol(j, num) &&
                unUsedInBox(i-i%SRN, j-j%SRN, num));
    }

    // check in the row for existence
    boolean unUsedInRow(int i,int num) {
        for (int j = 0; j<N; j++)
            if (tableForGame[i][j] == num)
                return false;
        return true;
    }

    // check in the row for existence
    boolean unUsedInCol(int j,int num) {
        for (int i = 0; i<N; i++)
            if (tableForGame[i][j] == num)
                return false;
        return true;
    }

    // A recursive function to fill remaining
    // matrix
    boolean fillRemaining(int i, int j) {
        // System.out.println(i+" "+j);
        if (j>=N && i<N-1)
        {
            i = i + 1;
            j = 0;
        }
        if (i>=N && j>=N)
            return true;

        if (i < SRN)
        {
            if (j < SRN)
                j = SRN;
        }
        else if (i < N-SRN)
        {
            if (j==(int)(i/SRN)*SRN)
                j = j + SRN;
        }
        else
        {
            if (j == N-SRN)
            {
                i = i + 1;
                j = 0;
                if (i>=N)
                    return true;
            }
        }

        for (int num = 1; num<=N; num++)
        {
            if (CheckIfSafe(i, j, num))
            {
                tableForGame[i][j] = num;
                if (fillRemaining(i, j+1))
                    return true;

                tableForGame[i][j] = 0;
            }
        }
        return false;
    }

    // Remove the K no. of digits to complete game
    public void removeKDigits() {
        for (int i = 0; i<N; i++)
            for (int j = 0; j<N; j++)
                keyTable[i][j] = tableForGame[i][j];


        int count = K;
        while (count != 0)
        {
            int cellId = randomGenerator(N*N);

            int i = (cellId/N);
            int j = cellId%9;
            if (j != 0)
                j = j - 1;
            if (i==9 || j==9) continue;
            // System.out.println(i+" "+j);

            if (tableForGame[i][j] != 0)
            {

                count--;
                tableForGame[i][j] = 0;
            }
        }
    }

    public int[][] getKeyTable() {
        return keyTable;
    }

    public int[][] getTableForGame() {
        return tableForGame;
    }

}
