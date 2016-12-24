package com.example.medivh.mario_cover;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;


/**
 * Created by Medivh on 24.12.2016.
 */

public class eventsHandler {

    final int STATUS_NONE = 0;
    final int STATUS_START = 1;
    final int STATUS_RESTART = 2;
    final int STATUS_END = 3;
    final int STATUS_MOVE = 4;
    final int STATUS_JUMP = 5;
    final int STATUS_FALL = 6;
    final int STATUS_BUMP = 7;

    Button ControlButton;
    private Context context;
    Handler handlerRules;

    public eventsHandler(Context contextMain,Button button) {
        context = contextMain;
        ControlButton = button;
        handlerRules = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATUS_NONE:
                        break;
                    case STATUS_START:
                        break;
                    case STATUS_END:
                        ControlButton.setBackgroundResource(R.drawable.finalbutton);
                        ControlButton.setVisibility(View.VISIBLE);
                        ControlButton.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        ControlButton.setText("Congrac!\n Start new game?");
                        break;
                    case STATUS_MOVE:
                        break;
                    case STATUS_BUMP:

                        break;
                    case STATUS_JUMP:
                        break;
                    case STATUS_FALL:
                    case STATUS_RESTART:
                            ControlButton.setBackgroundResource(R.drawable.restart);
                            ControlButton.setVisibility(View.VISIBLE);
                            ControlButton.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                            ControlButton.setText("You lose!\n Restart Game?");
                        break;
                }
            }
        };
    }
}





