package com.example.medivh.mario_cover;

import android.content.Context;
import android.media.MediaPlayer;
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
    final int STATUS_OFF = 9;

    Button ControlButton;
    private Context context;
    Handler handlerRules;


    public eventsHandler(Context contextMain,Button button) {
        context = contextMain;
        ControlButton = button;
        final MediaPlayer clicksound = MediaPlayer.create(context,R.raw.smb_jump_small);
        final MediaPlayer endsound = MediaPlayer.create(context,R.raw.smb_gameover);
        final MediaPlayer bumpsound = MediaPlayer.create(context,R.raw.smb_bump);
        final MediaPlayer maintheme = MediaPlayer.create(context, R.raw.maintheme_nes);

        if ( clicksound.isPlaying() == true) endsound.stop();
        if ( endsound.isPlaying() == true) endsound.stop();
        if ( maintheme.isPlaying() == true) endsound.stop();
        if ( bumpsound.isPlaying() == true) endsound.stop();

        handlerRules = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATUS_NONE:
                        break;
                    case STATUS_START:
                        if ( endsound.isPlaying() == true) endsound.stop();
                        if ( maintheme.isPlaying() == false) {
                            maintheme.start();
                            maintheme.setLooping(true);
                        }
                        break;
                    case STATUS_END:
                        if(maintheme.isPlaying() == true) maintheme.pause();
                        ControlButton.setBackgroundResource(R.drawable.finalbutton);
                        ControlButton.setVisibility(View.VISIBLE);
                        ControlButton.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        ControlButton.setText("Congrac!\n Start new game?");
                        break;
                    case STATUS_MOVE:
                        break;
                    case STATUS_BUMP:
                        if (bumpsound.isPlaying() == false) {
                            bumpsound.start();
                        }
                        break;
                    case STATUS_JUMP:
                            if (clicksound.isPlaying() == true) clicksound.pause();
                            clicksound.seekTo(0);
                            clicksound.start();
                        break;
                    case STATUS_FALL:
                        if ( endsound.isPlaying() == false) endsound.start();
                    case STATUS_RESTART:
                        if(maintheme.isPlaying() == true)  maintheme.pause();
                            ControlButton.setBackgroundResource(R.drawable.restart);
                            ControlButton.setVisibility(View.VISIBLE);
                            ControlButton.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                            ControlButton.setText("You lose!\n Restart Game?");
                        break;
                    case STATUS_OFF:
                        if ( clicksound.isPlaying() == true) endsound.stop();
                        if ( endsound.isPlaying() == true) endsound.stop();
                        if ( maintheme.isPlaying() == true) endsound.stop();
                        if ( bumpsound.isPlaying() == true) endsound.stop();
                        break;
                }
            }
        };
    }

}





