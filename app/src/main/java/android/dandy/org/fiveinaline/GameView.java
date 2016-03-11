package android.dandy.org.fiveinaline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dandy
 * This is a custom view which can draw the chess board of
 * the "Five In a Line" game.
 */
public class GameView extends View {
    private GameController mGameController;
    private Paint paint = new Paint();
    private static int[] colorBank =
            new int[]{Color.BLUE,Color.RED,Color.GREEN,Color.YELLOW,Color.CYAN,Color.BLACK};
    private int[] selected = new int[]{-1,-1};

    protected int mHighestScore;
    protected int mCurrentScore;
    protected TextView highestScoreText;
    protected TextView currentScoreText;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrSet){
        super(context, attrSet);
        init();
    }

    private void init(){
        paint.setAntiAlias(true);
        this.mGameController = new GameController();
        mGameController.startGame();
        mCurrentScore = mGameController.getScore();
    }

    public GameController getGameController() {
        return this.mGameController;
    }

    public void setGameController(GameController gameController){
        this.mGameController = gameController;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("GameView", "onDraw");
        int BOARD_WIDTH = getWidth()-20;
        int GRID_WIDTH = BOARD_WIDTH/GameController.ROW_NUM;
        canvas.drawColor(0xeaebec);
        paint.setColor(Color.BLACK);
        //Draw the board
        for(int i=0;i<=GameController.ROW_NUM;i++)
        {
            canvas.drawLine(10, i*GRID_WIDTH+10,getWidth()-10 ,i*GRID_WIDTH+10, paint);
            canvas.drawLine(i*GRID_WIDTH+10, 10,i*GRID_WIDTH+10, getWidth()-10, paint);
        }
        //使用maskFilter实现棋子的滤镜效果，使之看起来更有立体感。
        float[] dire = new float[]{1,1,1};  //光线方向
        float light = 0.5f;   //光线强度
        float spe = 6;
        float blur = 3.5f;
        EmbossMaskFilter emboss=new EmbossMaskFilter(dire,light,spe,blur);
        paint.setMaskFilter(emboss);
        //Draw the chess
        for(int i=0;i<GameController.ROW_NUM;i++){
            for(int j=0;j<GameController.ROW_NUM;j++){
                if(mGameController.chessBoard[i][j] > 0){
                    if(selected[0]==i && selected[1]==j){
                        canvas.drawLine(j*GRID_WIDTH+10,i*GRID_WIDTH+10+GRID_WIDTH/2,
                                j*GRID_WIDTH+15,i*GRID_WIDTH+10+GRID_WIDTH/2,paint);
                        canvas.drawLine((j+1)*GRID_WIDTH+10,i*GRID_WIDTH+10+GRID_WIDTH/2,
                                (j+1)*GRID_WIDTH+5,i*GRID_WIDTH+10+GRID_WIDTH/2,paint);
                        canvas.drawLine(j*GRID_WIDTH+10+GRID_WIDTH/2,i*GRID_WIDTH+10,
                                j*GRID_WIDTH+10+GRID_WIDTH/2,i*GRID_WIDTH+15,paint);
                        canvas.drawLine(j*GRID_WIDTH+10+GRID_WIDTH/2,(i+1)*GRID_WIDTH+10,
                                j*GRID_WIDTH+10+GRID_WIDTH/2,(i+1)*GRID_WIDTH+5,paint);
                    }
                    paint.setColor(colorBank[mGameController.chessBoard[i][j]-1]);
                    canvas.drawCircle(j*GRID_WIDTH+10+GRID_WIDTH/2,i*GRID_WIDTH+10+GRID_WIDTH/2,
                            GRID_WIDTH/2 - 5, paint);
                }
                paint.setColor(Color.BLACK);
            }
        }
        mCurrentScore = mGameController.getScore();
        if(mCurrentScore > mHighestScore)mHighestScore = mCurrentScore;
        currentScoreText.setText(""+mCurrentScore);
        highestScoreText.setText(""+mHighestScore);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int BOARD_WIDTH = getWidth()-20;
        int GRID_WIDTH = BOARD_WIDTH/GameController.ROW_NUM;
        if(event.getX() < 10 || event.getY() < 10 || event.getX() > getWidth()-10
                || event.getY() > getWidth()-10){}
        else{
            int r = (int)(event.getY()-10)/GRID_WIDTH;
            int c = (int)(event.getX()-10)/GRID_WIDTH;
            r&=7;
            c&=7;
            //There is a chess, mark it as selected, redraw.
            if(mGameController.chessBoard[r][c]>0){
                selected[0] = r;
                selected[1] = c;
                invalidate();
            }
            else{
                //If there is one chess selected
                if(selected[0]>-1){
                    int[][] temp = new int[GameController.ROW_NUM][GameController.ROW_NUM];
                    for (int i = 0; i < GameController.ROW_NUM; i++) {
                        for (int j = 0; j < GameController.ROW_NUM; j++) {
                            temp[i][j] = mGameController.chessBoard[i][j];
                        }
                    }
                    if(mGameController.isConnected(selected[0], selected[1],r,c,temp)){
                        mGameController.chessBoard[r][c]
                                = mGameController.chessBoard[selected[0]][selected[1]];
                        mGameController.chessBoard[selected[0]][selected[1]] = 0;
                        selected[0] = selected[1] = -1;
                        int score = mGameController.getScore();
                        mGameController.checkBoard();
                        if(mGameController.getScore()==score)
                            mGameController.process();
                        invalidate();
                        if(mGameController.isTerminated()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Game Over!");
                            builder.setMessage("您获得了" + mCurrentScore + "分,是否重新开始游戏？");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mGameController.restartGame();
                                    invalidate();
                                }
                            });
                            builder.setNegativeButton("No", null);
                            builder.create().show();
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "此路不通！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return  super.onTouchEvent(event);
    }
}
