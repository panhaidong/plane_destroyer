package org.zhutou.planedestroyer;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AirportView extends View {
    private static final int WIDTH = 10;
    private static final int COLOR_ACTIVE_EDIT = 0xCC00FF00;
    private static final int COLOR_EMPTY = 0x00FFFFFF;

    private static final int COLOR_PLANE_HEAD = 0x99FF0000;
    private static final int COLOR_PLANE_BODY = 0x99FF00FF;

    private static final int COLOR_UNKNOWN = 0x88000000;
    private static final int COLOR_KNOWN = 0x00FFFFFF;

    private int unit = 0;
    private int offsetX = 0;
    private int offsetY = 0;
    private final Paint mPaint = new Paint();


    private Plane[] planes = new Plane[3];
    private int activePlaneIdx = 0;

    private boolean fighting;
    private Set<Point> clearPoints = new HashSet<Point>();

    public AirportView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        initView();

    }


    public AirportView(Context context, AttributeSet attrs) throws InterruptedException {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        initView();

    }

    private void initView() {
        initPlanes();
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
        for (int i = 0; i < planes.length; i++) {
            Plane plane = planes[i];
            draw(canvas, plane, i == activePlaneIdx);
        }

        if (fighting) {
            drawBombSituation(canvas);
        }
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
                int x = (int) event.getX();
                int y = (int) event.getY();
                System.out.println(x + "," + y);
                x = (x - offsetX) / unit;
                y = (y - offsetY) / unit;
                System.out.println(x + "," + y);

                if (x >= 0 && x < WIDTH && y >= 0 && y < WIDTH) {

                    if (fighting) {
                        Point p = new Point(x, y);
                        if (clearPoints.contains(p)) {
                            clearPoints.remove(p);
                        } else {
                            clearPoints.add(p);
                        }
                    } else {
                        activePlane(x, y);
                    }
                    this.postInvalidate();
                    return true;
                }
                break;

        }
        return true;
    }

    private boolean activePlane(int x0, int y0) {
        for (int i = 0; i < planes.length; i++) {
            Plane plane = planes[i];
            int[][] area = plane.getArea();
            Point p = plane.originPoint;
            for (int y = 0; y < area.length; y++) {
                int[] row = area[y];
                for (int x = 0; x < row.length; x++) {
                    if (row[x] > 0) {
                        if (p.x + x == x0 && p.y + y == y0) {
                            activePlaneIdx = i;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void drawBombSituation(Canvas canvas) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                Point point = new Point(x, y);
                if (clearPoints.contains(point))
                    mPaint.setColor(COLOR_KNOWN);
                else
                    mPaint.setColor(COLOR_UNKNOWN);
                canvas.drawRect(offsetX + (x) * unit, offsetY + (y) * unit, offsetX + (x + 1) * unit, offsetY + (y + 1)
                        * unit, mPaint);

            }
        }
    }

    private void initPlanes() {
        Plane plane1 = new Plane();
        planes[0] = plane1;

        Plane plane2 = new Plane();
        plane2.moveRight();
        plane2.moveRight();
        plane2.moveRight();
        plane2.moveRight();
        plane2.moveRight();
        plane2.moveDown();
        plane2.moveDown();
        planes[1] = plane2;

        Plane plane3 = new Plane();
        plane3.moveRight();
        plane3.moveRight();
        plane3.moveDown();
        plane3.moveDown();
        plane3.moveDown();
        plane3.moveDown();
        plane3.moveDown();
        plane3.moveDown();
        planes[2] = plane3;

    }

    private void draw(Canvas canvas, Plane plane, boolean active) {
        int[][] area = plane.getArea();
        Point p = plane.originPoint;
        for (int y = 0; y < area.length; y++) {
            int[] row = area[y];
            for (int x = 0; x < row.length; x++) {
                mPaint.setColor(COLOR_EMPTY);
                if (row[x] == 1) {
                    mPaint.setColor(active && !fighting ? COLOR_ACTIVE_EDIT : COLOR_PLANE_BODY);
                } else if (row[x] == 2) {
                    mPaint.setColor(active && !fighting ? COLOR_ACTIVE_EDIT : COLOR_PLANE_HEAD);
                }
                canvas.drawRect(offsetX + (p.x + x) * unit, offsetY + (p.y + y) * unit, offsetX + (p.x + x + 1) * unit,
                        offsetY + (p.y + y + 1) * unit, mPaint);

            }
        }

    }

    public void moveUp() {
        planes[activePlaneIdx].moveUp();
        this.postInvalidate();
    }

    public void moveDown() {
        planes[activePlaneIdx].moveDown();
        this.postInvalidate();
    }

    public void moveLeft() {
        planes[activePlaneIdx].moveLeft();
        this.postInvalidate();
    }

    public void moveRight() {
        planes[activePlaneIdx].moveRight();
        this.postInvalidate();
    }

    public void rotateLeft() {
        planes[activePlaneIdx].rotateLeft();
        this.postInvalidate();
    }

    public void fight() {
        fighting = true;
        this.postInvalidate();
    }

    static class Plane {
        private static int[][] area0 = { { 0, 0, 2, 0, 0 }, { 1, 1, 1, 1, 1 }, { 0, 0, 1, 0, 0 }, { 0, 1, 1, 1, 0 } };
        private static int[][] area1 = { { 0, 0, 1, 0 }, { 1, 0, 1, 0 }, { 1, 1, 1, 2 }, { 1, 0, 1, 0 }, { 0, 0, 1, 0 } };
        private static int[][] area2 = { { 0, 1, 1, 1, 0 }, { 0, 0, 1, 0, 0 }, { 1, 1, 1, 1, 1 }, { 0, 0, 2, 0, 0 } };
        private static int[][] area3 = { { 0, 1, 0, 0 }, { 0, 1, 0, 1 }, { 2, 1, 1, 1 }, { 0, 1, 0, 1 }, { 0, 1, 0, 0 } };

        private static enum Orientation {
            UP, DOWN, LEFT, RIGHT
        };

        private Point originPoint = new Point(0, 0);
        private Orientation orientation = Orientation.UP;

        public int[][] getArea() {
            switch (orientation) {
                case UP:
                    return area0;
                case RIGHT:
                    return area1;
                case DOWN:
                    return area2;
                case LEFT:
                    return area3;
                default:
                    return area0;
            }
        }

        public void moveUp() {
            originPoint.y = Math.max(0, originPoint.y - 1);
        }

        public void moveDown() {
            originPoint.y = Math.min(WIDTH - getArea().length, originPoint.y + 1);
        }

        public void moveLeft() {
            originPoint.x = Math.max(0, originPoint.x - 1);
        }

        public void moveRight() {
            originPoint.x = Math.min(WIDTH - getArea()[0].length, originPoint.x + 1);
        }

        public void rotateLeft() {
            switch (orientation) {
                case UP:
                    orientation = Orientation.LEFT;
                    break;
                case RIGHT:
                    orientation = Orientation.UP;
                    break;
                case DOWN:
                    orientation = Orientation.RIGHT;
                    break;
                case LEFT:
                    orientation = Orientation.DOWN;
                    break;
            }
        }
    }
}
