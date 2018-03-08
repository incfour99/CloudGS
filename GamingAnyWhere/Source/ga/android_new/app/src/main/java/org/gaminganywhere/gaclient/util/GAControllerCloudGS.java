package org.gaminganywhere.gaclient.util;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Created by kim0375 on 2018-02-28.
 */

public class GAControllerCloudGS extends GAController implements
        OnClickListener, Pad.PartitionEventListener
{

    public GAControllerCloudGS(Context context) {
        super(context);
    }

    public static String getName() {
        return "CloudGS";
    }

    public static String getDescription() {
        return "CloudGS";
    }

    // 방향 버튼
    private Button buttonUp = null;
    private Button buttonDown = null;
    private Button buttonLeft = null;
    private Button buttonRight = null;

    // 숫자 버튼
    private Button button6 = null;
    private Button button9 = null;

    private Pad padRight = null;

    @Override
    public void onDimensionChange(int width, int height) {

        // must be called firs
        super.setMouseVisibility(false);
        super.onDimensionChange(width, height);

        // check width/height
        if (width < 0 || height < 0)
            return;

        final int nButtonW = 8;
        final int nButtonH = 10;
        int keyBtnWidth = width / (nButtonW + 1);
        int keyBtnHeight = height / (nButtonH + 1);
        int marginW = (width / (nButtonW + 1)) / (nButtonW + 1);
        int marginH = (height / (nButtonH + 1)) / (nButtonH + 1);
        int padSize = height * 7 / 30;
        int cBtnSize = (int) (padSize * 0.7);
        int mBtnSize = (int) (padSize);

        int cx, cy, gap;
        int margin = keyBtnWidth/6;
        cx = margin+padSize;
        cy = margin*2+padSize*3;

        gap = (int) (padSize*0.3);
        int bsize = (int) (padSize*0.7);

        buttonRight = newButton("→", cx+gap, cy+gap, bsize, bsize);
        buttonRight.setOnTouchListener(this);
        //
        buttonDown = newButton("↓", cx-bsize/2, cy+gap, bsize, bsize);
        buttonDown.setOnTouchListener(this);
        //
        buttonUp = newButton("↑", cx-bsize/2, cy-bsize/2, bsize, bsize);
        buttonUp.setOnTouchListener(this);
        //
        buttonLeft = newButton("←", cx-gap-bsize, cy+gap, bsize, bsize);
        buttonLeft.setOnTouchListener(this);

        button6 = newButton("SHOT1", width-marginW-(cBtnSize*2), height-cBtnSize, cBtnSize, cBtnSize);
        button6.setOnTouchListener(this);

        button9 = newButton("SHOT2", width-marginW-cBtnSize, height-cBtnSize, cBtnSize, cBtnSize);
        button9.setOnTouchListener(this);

        padRight = null;
        padRight = new Pad(getContext());
        padRight.setAlpha((float) 0.5);
        padRight.setOnTouchListener(this);
        padRight.setPartition(2);
        padRight.setPartitionEventListener(this);
        placeView(padRight, width-width/30-padSize, height-padSize-height/30-cBtnSize, padSize, padSize);
    }

    @Override
    public boolean onTouch(View v, MotionEvent evt) {
        int count = evt.getPointerCount();
        int action = evt.getActionMasked();

        if(count==1 && (v == padRight)) {
            if(((Pad) v).onTouch(evt));
            return true;
        }

        if (v == buttonUp)
            return handleButtonTouch(action, SDL2.Scancode.UP, SDL2.Keycode.UP, 0, 0);
        else if(v == buttonDown)
            return handleButtonTouch(action, SDL2.Scancode.DOWN, SDL2.Keycode.DOWN, 0, 0);
        else if(v == buttonLeft)
            return handleButtonTouch(action, SDL2.Scancode.LEFT, SDL2.Keycode.LEFT, 0, 0);
        else if(v == buttonRight)
            return handleButtonTouch(action, SDL2.Scancode.RIGHT, SDL2.Keycode.RIGHT, 0, 0);
        else if(v == button6)
            return handleButtonTouch(action, SDL2.Scancode._6, SDL2.Keycode._6, 0, 0);
        else if(v == button9)
            return handleButtonTouch(action, SDL2.Scancode._9, SDL2.Keycode._9, 0, 0);

        return super.onTouch(v, evt);
    }

    private int mouseButton = -1;
    private void emulateMouseButtons(int action, int part) {
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                //case MotionEvent.ACTION_POINTER_DOWN:
                if(part == 0 || part == 2)
                    mouseButton = SDL2.Button.LEFT;
                else
                    mouseButton = SDL2.Button.RIGHT;
                this.sendMouseKey(true, mouseButton, getMouseX(), getMouseY());
                break;
            case MotionEvent.ACTION_UP:
                //case MotionEvent.ACTION_POINTER_UP:
                if(mouseButton != -1) {
                    sendMouseKey(false, mouseButton, getMouseX(), getMouseY());
                    mouseButton = -1;
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPartitionEvent(View v, int action, int part) {
        // Right: emulated mouse buttons
        if(v == padRight) {
            emulateMouseButtons(action, part);
            return;
        }
    }
}
