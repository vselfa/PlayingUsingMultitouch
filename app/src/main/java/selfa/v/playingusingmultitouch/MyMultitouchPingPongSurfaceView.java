package selfa.v.playingusingmultitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyMultitouchPingPongSurfaceView extends SurfaceView implements SurfaceHolder.Callback  {

    // The thread
    private MyMultitouchPingPongThread myMultitouchPingPongThread = null;

    // The ball
    private int x, y;
    // Speed
    private int xDirection = 10, yDirection = 10;
    private static int radius = 20;
    private static int ballColor = Color.BLUE;

    // La situació inicial de les paletes
    private float distanciaX = 100, distanciaY = 10;
    private float paleta1X = distanciaX, paleta1Y = distanciaY;
    private float paleta2X, paleta2Y = distanciaY;
    private float ample = 10, alt = 150;

    // The score
    private int puntsPaleta1, puntsPaleta2;
    private String marcadorPaleta1, marcadorPaleta2, marcador;
    private int comptador = 0;
    private boolean haGuanyat = false;// Per mantindre el text de guanyador en pantalla
    private String text = "";
    Paint paint = new Paint(); Paint paleta1 = new Paint();    Paint paleta2 = new Paint();

    // Multitouch
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    // SparseArray: maps integers to Objects. PointF holds two float coordinates
    // El primer punt gestiona paleta esquerre. El segon paleta dreta
    private SparseArray<PointF> mActivePointers;
    private boolean primeraVegada;

    public MyMultitouchPingPongSurfaceView(Context ctx) {
        super(ctx);        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Inicialitzem valors gràfics
        puntsPaleta1 = 0;         puntsPaleta2 = 0;
        primeraVegada = true;    x = getWidth() / 2;        y = getHeight() / 2;
        paleta2X = getWidth() - distanciaX;
        mActivePointers = new SparseArray();
        if (myMultitouchPingPongThread != null) return;
        myMultitouchPingPongThread = new MyMultitouchPingPongThread(getHolder());
        myMultitouchPingPongThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {       stopThread();    }

    public void stopThread() {
        if (myMultitouchPingPongThread != null) myMultitouchPingPongThread.stop = true;
    }

    public void newDraw(Canvas canvas) {
        pintarTauler(canvas);
        pintarPilota(canvas);
        pintarPunts (canvas);
        // Moviment paletes
        for (int size = mActivePointers.size(), i = 0; i < size; i++) {
            PointF point = mActivePointers.valueAt(i);
            if (point != null) {
                if (i == 0) {
                    // mPaint.setColor(colors[i % 9]);  canvas.drawRect(x1, point.y, x1 + ample, point.y + alt, mPaint);
                    paleta1Y = point.y;
                    pintarPaleta (canvas, paleta1X, point.y, paleta1);
                    if (paleta1Y  < 0 ){ paleta1Y = 0;  }
                    if (paleta1Y + alt > getHeight())  { paleta1Y = getHeight() - alt;  }
                }
                if (i == 1) {
                    // mPaint.setColor(colors[i % 9]);  canvas.drawRect(x2, point.y, x2 + ample, point.y + alt, mPaint);
                    pintarPaleta (canvas, paleta2X, point.y, paleta2);
                    //Log.d("Moviment paleta 2 = ", String.valueOf(increment.y));
                    // paleta2Y = paleta2Y + increment.y;
                    paleta2Y = point.y;
                    if (paleta2Y  < 0 ){ paleta2Y = 0;  }
                    if (paleta2Y + alt > getHeight())  { paleta2Y = getHeight() - alt;  }
                }
            }
            else {
                pintarPaleta (canvas, paleta1X, paleta1Y, paleta1);
                pintarPaleta (canvas, paleta2X, paleta2Y, paleta2);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Get pointer index from the event object
        int pointerIndex = ev.getActionIndex();
        // Get pointer ID
        int pointerId = ev.getPointerId(pointerIndex);
        // Get masked (not specific to a pointer) action
        int action  = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Lets add it to the list of pointers
                PointF f = new PointF();
                f.x = ev.getX(pointerIndex);       f.y = ev.getY(pointerIndex);
                // Adds a mapping from the specified key (pointerId) to the specified value (PointF f )
                mActivePointers.put(pointerId, f);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int size = ev.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(ev.getPointerId(i));
                    if (point != null) {
                        // La paleta acompanya al dit en vertical
                        point.x = ev.getX(i);
                        point.y = ev.getY(i);
                    }
                }
                break;
            }
            /*case MotionEvent.ACTION_UP:     case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: { mActivePointers.remove(pointerId);      break; }*/
        } // End  switch
        invalidate();
        return true;// End onTouchEvent
    }

    public void pintarTauler (Canvas canvas) {
        // El tauler
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setColor(ballColor);
    }
    public void pintarPilota (Canvas canvas) {
        // La pilota
        canvas.drawCircle(x, y, radius, paint);
    }

    public void pintarPaleta(Canvas canvas, float x, float y, Paint paleta) {
        paleta1.setColor(Color.RED);        paleta2.setColor(Color.BLUE);
        canvas.drawRect(x, y, x + ample, y + alt, paleta);
    }

    public void pintarPunts (Canvas canvas) {
        // Els punts
        if (puntsPaleta1 > 9) {
            text = "  --- > La paleta 1 ha GUANYAT!! <----";
            haGuanyat = true;  puntsPaleta1 = 0;   puntsPaleta2 = 0;
        } else {
            if (puntsPaleta2 > 9) {
                text = "  --- > La paleta 2 ha GUANYAT!! <----";
                haGuanyat = true; puntsPaleta1 = 0;  puntsPaleta2 = 0;
            } else {
                if (haGuanyat) {
                    comptador++;
                    if (comptador > 1000) {
                        haGuanyat = false;
                        text = "";
                        comptador = 0;
                    }
                }
                paint.setTextSize(30);
                marcadorPaleta1 = "Paleta 1 = " + String.valueOf(puntsPaleta1) + " --- ";
                marcadorPaleta2 = "Paleta 2 = " + String.valueOf(puntsPaleta2);
                marcador = "Marcador: " + marcadorPaleta1 + marcadorPaleta2 + text;
                canvas.drawText(marcador, 10, getHeight() - 30, paint);
            }
        }
    }

    // The thread -----------------------------------------------------------
    private class MyMultitouchPingPongThread extends Thread {
        public boolean stop = false;
        private SurfaceHolder surfaceHolder;
        public MyMultitouchPingPongThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }
        public void run() {
            while (!stop) {
                // El moviment de la pilota
                x += xDirection;        y += yDirection;
                // Rebassa la paleta 1 (esquerre)
                if (x + radius < 0) {
                    xDirection = -xDirection;
                    // Per evitar que rebote per darrere
                    x = (int) (2 * distanciaX);      puntsPaleta2 += 1;
                }
                // Rebassa la paleta 2 (dreta)
                if (x > getWidth() - radius) {
                    xDirection = -xDirection;
                    // Per evitar que rebote per darrere
                    x = (int) (getWidth() - 2 * distanciaX);    puntsPaleta1 += 1;
                }
                // Rebot dalt
                if (y < 0) {    yDirection = -yDirection; y = radius;      }
                // Rebota baix
                if (y > getHeight() - radius) {
                    yDirection = -yDirection;    y = getHeight() - radius - 1;
                }
                // Programem el xoc
                // Amb la paleta esquerre
                if ((y + radius > paleta1Y) && (y + radius < paleta1Y + alt)) {
                    if ((x + radius > paleta1X) && (x - radius < paleta1X + ample)) {
                        xDirection = -xDirection; puntsPaleta1++;
                        x += 10;                        // Per evitar rebots repetits
                    }
                }
                // Amb la paleta dreta
                if ((y + radius > paleta2Y) && (y + radius < paleta2Y + alt)) {
                    if ((x + radius > paleta2X) && (x - radius < paleta2X + ample)) {
                        xDirection = -xDirection; puntsPaleta2++;
                        x -= 10;                        // Per evitar rebots repetits
                    }
                }
                Canvas c = null;
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {      newDraw(c);        }
                } finally {
                    if (c != null) surfaceHolder.unlockCanvasAndPost(c);
                }
            } // Stop
        } // Run
    } // Thread
} // Classe

