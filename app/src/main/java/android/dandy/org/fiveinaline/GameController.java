package android.dandy.org.fiveinaline;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

/**
 * Created by Dandy on 2016/3/9.
 */
public class GameController implements Parcelable{
    public static int ROW_NUM = 8;
    private int score;
    private int startNum;
    private int processNum;
    private int colorNum;
    private boolean terminated;
    protected int[][] chessBoard;

    public GameController(){
        super();
        startNum = 5;
        processNum = 3;
        colorNum = 5;
        chessBoard = new int[ROW_NUM][ROW_NUM];
        for(int i=0;i<ROW_NUM;i++){
            for (int j = 0; j < ROW_NUM; j++) {
                chessBoard[i][j] = 0;
            }
        }
    }

    public GameController(Parcel in){
        score = in.readInt();
        startNum = in.readInt();
        processNum = in.readInt();
        colorNum = in.readInt();
        for(int i=0;i<ROW_NUM;i++){
            in.readIntArray(chessBoard[i]);
        }
    }

    protected void startGame(){
        score = 0;
        terminated = false;
        generateChess(startNum);
        //checkBoard();
    }
    protected void restartGame(){
        chessBoard = new int[ROW_NUM][ROW_NUM];
        startGame();
    }

    protected boolean isConnected(int srcRow, int srcCol, int destRow, int destCol, int[][] board){
        if((Math.abs(srcRow-destRow)==1 && srcCol==destCol)||
                (Math.abs(srcCol-destCol)==1&&srcRow==destRow))
            return true;
        if(board[srcRow][srcCol]==0)board[srcRow][srcCol]=-1;
        if(srcRow>0 && board[srcRow-1][srcCol]==0) {
            if(isConnected(srcRow-1, srcCol, destRow, destCol, board))
                return true;
        }
        if(srcCol>0 && board[srcRow][srcCol-1]==0) {
            if(isConnected(srcRow, srcCol-1, destRow, destCol, board))
                return true;
        }
        if(srcRow<ROW_NUM-1 && board[srcRow+1][srcCol]==0) {
            if(isConnected(srcRow+1, srcCol, destRow, destCol, board))
                return true;
        }
        if(srcCol<ROW_NUM-1 && board[srcRow][srcCol+1]==0) {
            if(isConnected(srcRow, srcCol+1, destRow, destCol, board))
                return true;
        }
        return false;
    }

    protected void process(){
        generateChess(processNum);
        checkBoard();
        int blanks = 0;
        for(int i=0;i<ROW_NUM;i++){
            for(int j=0;j<ROW_NUM;j++){
                if(chessBoard[i][j]==0)
                    blanks++;
            }
        }
        if(blanks < 3){
            terminated = true;
            return;
        }
    }
    private void generateChess(int num){
        Random random = new Random();
        for(int i=0;i<num;i++){
            int t = random.nextInt(ROW_NUM*ROW_NUM);
            if(chessBoard[t/ROW_NUM][t%ROW_NUM]>0){i--;}
            else{
                int r = random.nextInt(colorNum);
                chessBoard[t/ROW_NUM][t%ROW_NUM] = r+1;
            }
        }
    }

    protected void checkBoard(){
        //TODO:完成检查棋盘、加分的逻辑
        int[][] temp = new int[ROW_NUM][ROW_NUM];
        //横向
        for (int i = 0; i < ROW_NUM; i++) {
            int from = 0, to = 1, sFrom = 0, sTo =0;
            for (int j = 0; j < ROW_NUM; j++) {
                if(ROW_NUM-from<5)break;
                if(chessBoard[i][j]==0){
                    from = j+1;
                    to = from+1;
                }
                else if(j+1<ROW_NUM){
                    if(chessBoard[i][j]!=chessBoard[i][j+1]){
                        from = j+1;
                    }
                    to++;
                }
                if(to-from>=5){
                    sFrom = from;
                    sTo = to;
                }
            }
            for(int j=sFrom;j<sTo;j++){
                temp[i][j] = -1;
                score++;
            }
        }
        //纵向
        for (int i = 0; i < ROW_NUM; i++) {
            int from = 0, to = 1, sFrom = 0, sTo =0;
            for (int j = 0; j < ROW_NUM; j++) {
                if(ROW_NUM-from<5)break;
                if(chessBoard[j][i]==0){
                    from = j+1;
                    to = from+1;
                }
                else if(j+1<ROW_NUM){
                    if(chessBoard[j][i]!=chessBoard[j+1][i]){
                        from = j+1;
                    }
                    to++;
                }
                if(to-from>=5){
                    sFrom = from;
                    sTo = to;
                }
            }
            for(int j=sFrom;j<sTo;j++){
                temp[j][i] = -1;
                score++;
            }
        }
        //左上到右下
        for (int i = 0; i <= ROW_NUM-5; i++) {
            for (int j = 0; j <= ROW_NUM-5; j++) {
                if(chessBoard[i][j]!=0 &&chessBoard[i][j]==chessBoard[i+1][j+1]) {
                    int x = i + 1, y = j + 1, length = 2;
                    while (x + 1 < ROW_NUM && y + 1 < ROW_NUM && chessBoard[x][y] == chessBoard[x + 1][y + 1]) {
                        length++;
                        x++;
                        y++;
                    }
                    if (length >= 5) {
                        for (; x >= i && y >= j; x--, y--, score++) {
                            temp[x][y] = -1;
                        }
                    }
                }
            }
        }
        //从右上到左下
        for (int i = 0; i <= ROW_NUM-5; i++) {
            for (int j = ROW_NUM-1; j > 3; j--) {
                if(chessBoard[i][j]!=0 && chessBoard[i][j]==chessBoard[i+1][j-1]) {
                    int x = i + 1, y = j - 1, length = 2;
                    while (x + 1 < ROW_NUM && y > 0 && chessBoard[x][y] == chessBoard[x + 1][y - 1]) {
                        length++;
                        x++;
                        y--;
                    }
                    if (length >= 5) {
                        for (; x >= i && y <= j; x--, y++, score++) {
                            temp[x][y] = -1;
                        }
                    }
                }
            }
        }
        //重设棋盘
        for (int i = 0; i < ROW_NUM; i++) {
            for (int j = 0; j < ROW_NUM; j++) {
                if(temp[i][j]==-1)chessBoard[i][j]=0;
            }
        }
    }

    public int getScore() {
        return score;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public void setColorNum(int colorNum){
        this.colorNum = colorNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(score);
        dest.writeInt(startNum);
        dest.writeInt(processNum);
        dest.writeInt(colorNum);
        for(int i=0;i<ROW_NUM;i++){
            dest.writeIntArray(chessBoard[i]);
        }
    }

    public static final Parcelable.Creator<GameController> CREATOR = new Parcelable.Creator<GameController>(){
        @Override
        public GameController[] newArray(int size)
        {
            return new GameController[size];
        }

        @Override
        public GameController createFromParcel(Parcel in)
        {
            return new GameController(in);
        }
    };
}
