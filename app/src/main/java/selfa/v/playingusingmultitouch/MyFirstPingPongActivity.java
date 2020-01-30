package selfa.v.playingusingmultitouch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MyFirstPingPongActivity extends MainMenu {

    public static MyFirstPingPongSurfaceView myFirstPingPongSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFirstPingPongSurfaceView = new MyFirstPingPongSurfaceView(this);
        setContentView(myFirstPingPongSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stopping the thread
        if (myFirstPingPongSurfaceView != null) myFirstPingPongSurfaceView.stopThread();
    }
}
