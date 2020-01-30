package selfa.v.playingusingmultitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class MyFirstMultitouchView extends View {
    private static final int SIZE = 60;
    // SparseArray: maps integers to Objects. PointF holds two float coordinates
    private SparseArray<PointF> mActivePointers;
    private Paint mPaint;
    private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
            Color.LTGRAY, Color.YELLOW };
    private Paint textPaint;

    public MyFirstMultitouchView(Context context) {
        super(context);         initView();
    }
    private void initView() {
        mActivePointers = new SparseArray<PointF>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE); mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
    }
    // Here the newDraw method: To repaint canvas
    @Override
    protected void onDraw (Canvas canvas) {
        // 3.- Drawing all the pointers
        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
            PointF point = mActivePointers.valueAt(i);
            if (point != null)
                mPaint.setColor(colors[i % 9]);
            canvas.drawCircle(point.x, point.y, SIZE, mPaint);
        }
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLUE);
        canvas.drawText("Toca la pantalla i mou els ditets ...!!", 10, 40, textPaint);
        canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 100, textPaint);
    } //  End onDraw

    // Here onTouchEvent method: to control events on the screen
    @Override
    public boolean onTouchEvent (MotionEvent event) {
        int pointerIndex = event.getActionIndex(); // get pointer index from the event object
        int pointerId = event.getPointerId(pointerIndex); // get pointer ID
        // Getting the event with multitouch
        int maskedAction = event.getActionMasked();
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:{ // 1.- Getting the initial values of the pointers
                // We have a new pointer. Lets add it to the list of pointers
                PointF f = new PointF();  f.x = event.getX(pointerIndex);       f.y = event.getY(pointerIndex);
                // Adds a mapping from the specified key (pointerId) to the specified value (PointF f )
                mActivePointers.put(pointerId, f);   break;
            }
            case MotionEvent.ACTION_MOVE: { // 2.- Updating the position of the pointers
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {  point.x = event.getX(i);     point.y = event.getY(i);  }
                }
                break;
            }
            case MotionEvent.ACTION_UP:         case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: { mActivePointers.remove(pointerId);   break;       }
        }
        // When the view's appearance may need to be changed, the view will call invalidate().
        invalidate();  // To call the onDraw() method
        return true;
    }
} // End MultiTouchView
