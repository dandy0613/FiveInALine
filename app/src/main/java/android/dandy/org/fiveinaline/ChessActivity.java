package android.dandy.org.fiveinaline;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.dandy.org.fiveinaline.R;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChessActivity extends AppCompatActivity {

    private static final String GAME = "game";
    private static final String TAG = "ChessActivity";
    private SharedPreferences preferences;
    private GameView mGameView;
    private GameController mGameController;
    private Button restartButton;
    private Button choseHardnessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG, "Create");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
            }
        });
        setContentView(R.layout.activity_chess);
        mGameView = (GameView)findViewById(R.id.game_view);

        mGameView.highestScoreText =(TextView)findViewById(R.id.highest_score);
        mGameView.currentScoreText=(TextView)findViewById(R.id.current_score);

        restartButton = (Button)findViewById(R.id.restart_game);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameController.restartGame();
                mGameView.invalidate();
            }
        });
        choseHardnessButton = (Button)findViewById(R.id.chose_hardness);
        choseHardnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChessActivity.this);
                builder.setTitle("选择难度");
                builder.setSingleChoiceItems(new String[]{"入门", "简单", "一般", "困难"},
                        0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        mGameController.setColorNum(4);
                                        mGameController.setStartNum(4);
                                        mGameController.setProcessNum(2);
                                        break;
                                    case 1:
                                        mGameController.setColorNum(5);
                                        mGameController.setStartNum(4);
                                        mGameController.setProcessNum(2);
                                        break;
                                    case 2:
                                        mGameController.setColorNum(5);
                                        mGameController.setStartNum(5);
                                        mGameController.setProcessNum(3);
                                        break;
                                    case 3:
                                        mGameController.setColorNum(6);
                                        mGameController.setStartNum(5);
                                        mGameController.setProcessNum(3);
                                        break;
                                }
                                mGameController.restartGame();
                                mGameView.invalidate();
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        if(savedInstanceState!=null){
            mGameController = savedInstanceState.getParcelable(GAME);
            mGameView.setGameController(mGameController);
        }
        else{
            mGameController = mGameView.getGameController();
        }
        preferences  = getPreferences(MODE_PRIVATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Stop");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("highest", mGameView.mHighestScore);
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Start");
        mGameView.mHighestScore = preferences.getInt("highest", 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GAME, mGameController);
    }
}
