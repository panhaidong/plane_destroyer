package org.zhutou.planedestroyer;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class AirportMapView extends View {
    private static final int WIDTH = 10;
    private static final int COLOR_EMPTY = 0x00FFFFFF;

    private static final int COLOR_PLANE_HEAD = 0x99FF0000;
    private static final int COLOR_PLANE_BODY = 0x99FF00FF;

    private static final int COLOR_UNKNOWN = 0x88000000;

    private int unit = 0;
    private int offsetX = 0;
    private int offsetY = 0;
    private final Paint mPaint = new Paint();

    private Map<Point, Integer> clearPoints = new HashMap<Point, Integer>();

    private Activity activity;

    public AirportMapView(Activity activity) {
        super(activity.getApplicationContext());
        this.activity = activity;
        setFocusable(true);
        setFocusableInTouchMode(true);
        initView();

    }

    private void initView() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        unit = Math.min(w, h) / 12;
        offsetX = unit;
        offsetY = unit;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(20);
        for (int x = 0; x <= WIDTH; x++) {
            canvas.drawLine(offsetX + x * unit, offsetY + 0, offsetX + x * unit, offsetY + WIDTH * unit, mPaint);
            canvas.drawLine(offsetX + 0, offsetY + x * unit, offsetX + WIDTH * unit, offsetY + x * unit, mPaint);

            if (x < WIDTH) {
                canvas.drawText(String.valueOf(x), offsetX + x * unit + unit / 4, offsetY - unit / 4, mPaint);
                canvas.drawText(String.valueOf(x), offsetX - unit * 2 / 5, offsetY + x * unit + unit * 3 / 4, mPaint);
                canvas.drawText(String.valueOf(x), offsetX + x * unit + unit / 4, offsetY + unit * WIDTH + unit / 2,
                        mPaint);
                canvas.drawText(String.valueOf(x), offsetX + unit * WIDTH + unit / 4,
                        offsetY + x * unit + unit * 3 / 4, mPaint);

            }

        }
        drawBombSituation(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int tx = (int) event.getX();
                int ty = (int) event.getY();

                final int x = (tx - offsetX) / unit;
                final int y = (ty - offsetY) / unit;

                if (x >= 0 && x < WIDTH && y >= 0 && y < WIDTH) {
                    DialogInterface.OnClickListener lnr = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int c = 0;
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    c = 1;
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    c = 0;
                                    break;
                                case DialogInterface.BUTTON_NEUTRAL:
                                    c = 2;
                                    break;

                            }
                            clearPoints.put(new Point(x, y), c);
                            postInvalidate();
                        }
                    };
                    new AlertDialog.Builder(activity).setCustomTitle(null)
                            .setMessage(String.format("%s, %s ?", x, y))
                            .setNegativeButton("炸空", lnr)
                            .setNeutralButton("炸废", lnr)
                            .setPositiveButton("炸伤", lnr)
                            .create()
                            .show();


                    return true;
                }
                break;

        }
        return true;
    }

    private void drawBombSituation(Canvas canvas) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                Point point = new Point(x, y);
                Integer c = clearPoints.get(point);
                if (c == null)
                    mPaint.setColor(COLOR_UNKNOWN);
                else if (c == 0)
                    mPaint.setColor(COLOR_EMPTY);
                else if (c == 1)
                    mPaint.setColor(COLOR_PLANE_BODY);
                else if (c == 2)
                    mPaint.setColor(COLOR_PLANE_HEAD);
                canvas.drawRect(offsetX + (x) * unit, offsetY + (y) * unit, offsetX + (x + 1) * unit, offsetY + (y + 1)
                        * unit, mPaint);

            }
        }
    }
}
