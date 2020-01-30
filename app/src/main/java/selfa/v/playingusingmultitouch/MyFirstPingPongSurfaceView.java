package selfa.v.playingusingmultitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyFirstPingPongSurfaceView extends SurfaceView implements SurfaceHolder.Callback  {

    // The thread
    private PingPongThread pingPongTread = null;
    // The ball
    private int x;
    private int y;
    // The speed and other attributes
    private int xDirection = 10;     private int yDirection = 10;
    private static int radius = 20;  private static int ballColor = Color.BLUE;
    // To two palettes
    public float meitatX;   boolean clicPartEsquerre;
    private float distanciaX = 100, distanciaY = 10;
    private float ample = 10, alt = 150;
    private float paleta1X , paleta1Y, paleta2X, paleta2Y;
    // The score
    private int puntsPaleta1, puntsPaleta2;
    private String marcadorPaleta1, marcadorPaleta2, marcador;
    private float mLastTouchX, mLastTouchY;
    private int comptador = 0;
    private boolean haGuanyat = false;// Per mantindre el text de guanyador en pantalla
    private String text = "";
    Paint table = new Paint();
    Paint ball = new Paint();
    Paint paleta1 = new Paint();
    Paint paleta2 = new Paint();
    Paint texto = new Paint();

    public MyFirstPingPongSurfaceView(Context context) {
        super(context); getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        puntsPaleta1 = 0;     puntsPaleta2 = 0;
        x = getWidth()/2;         y = getHeight()/2;
        paleta1X = distanciaX;  paleta1Y = distanciaY;
        paleta2X =  getWidth() - distanciaX;  paleta2Y = distanciaY;
        meitatX = getWidth()/2;
        if (pingPongTread != null) return;
        pingPongTread = new PingPongThread(getHolder());
        pingPongTread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread();
    }

    public void stopThread () {
        if (pingPongTread != null) pingPongTread.stop = true;
    }


    public void newDraw(Canvas canvas) {
        // The table
        table.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), table);
        // The ball
        ball.setColor(ballColor);
        canvas.drawCircle(x, y, radius, ball);
        // The palettes
        paleta1.setColor(Color.RED);         paleta2.setColor(Color.BLUE);
        canvas.drawRect(paleta1X, paleta1Y, paleta1X + ample, paleta1Y + alt, paleta1);
        canvas.drawRect(paleta2X, paleta2Y, paleta2X + ample, paleta2Y + alt, paleta2);
        if (puntsPaleta1 > 9) {
            text = "  --- > La paleta 1 ha GUANYAT!! <----";
            haGuanyat = true;
            puntsPaleta1 = 0;
            puntsPaleta2 = 0;
        } else {
            if (puntsPaleta2 > 9) {
                text = "  --- > La paleta 2 ha GUANYAT!! <----";
                puntsPaleta1 = 0;
                puntsPaleta2 = 0;
                haGuanyat = true;
            } else {
                if (haGuanyat) {
                    comptador++;
                    if (comptador > 1000) {
                        haGuanyat = false;
                        text = "";
                        comptador = 0;
                    }
                }
                texto.setTextSize(30);
                marcadorPaleta1 = "Paleta 1 = " + String.valueOf(puntsPaleta1) + " --- ";
                marcadorPaleta2 = "Paleta 2 = " + String.valueOf(puntsPaleta2);
                marcador = "Marcador: " + marcadorPaleta1 + marcadorPaleta2 + text;
                canvas.drawText(marcador, 10, getHeight() - 30, texto);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX  = ev.getX();
                mLastTouchY  = ev.getY();
                if (mLastTouchX < meitatX) { // Part esquerre => Paleta1
                    clicPartEsquerre = true;
                }
                else clicPartEsquerre = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Calculate the distance moved
                final float x = ev.getX();  final float y = ev.getY();
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                if (clicPartEsquerre) {
                    // Only vertical movement palette 1
                    paleta1Y += dy;
                    if (paleta1Y < 0) paleta1Y =0;
                    if (paleta1Y + alt > getHeight()) paleta1Y = getHeight() - alt ;
                }
                else {
                    // Only vertical movement palette 2
                    paleta2Y += dy;
                    if (paleta2Y < 0) paleta2Y =0;
                    if (paleta2Y + alt > getHeight() ) paleta2Y = getHeight() - alt ;
                }
                // Remember this touch position for the next move event
                mLastTouchX = x;            mLastTouchY = y;
                invalidate();  // Call to newDraw();
                break;
            }
        }
        return true;// Fi  onTouchEvent
    }

    // The thread -----------------------------------------------------------
    private class PingPongThread extends Thread {

        public boolean stop = false;
        private SurfaceHolder surfaceHolder;

        public PingPongThread(SurfaceHolder surfaceHolder) {
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
                    x = (int) (2 * distanciaX);
                    puntsPaleta2 += 1;
                }
                // Rebassa la paleta 2 (dreta)
                if (x > getWidth() - radius) {
                    xDirection = -xDirection;
                    // Per evitar que rebote per darrere
                    x = (int) (getWidth() - 2 * distanciaX);
                    puntsPaleta1 += 1;
                }
                // Rebot dalt
                if (y < 0) {
                    yDirection = -yDirection;
                    y = radius;
                }
                // Rebota baix
                if (y > getHeight() - radius) {
                    yDirection = -yDirection;
                    y = getHeight() - radius - 1;
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
                    synchronized (surfaceHolder) {
                        newDraw(c);
                    }
                } finally {
                    if (c != null) surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}
