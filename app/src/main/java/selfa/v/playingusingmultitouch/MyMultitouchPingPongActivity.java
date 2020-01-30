package selfa.v.playingusingmultitouch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MyMultitouchPingPongActivity extends MainMenu {

    public static MyMultitouchPingPongSurfaceView myMultitouchPingPongSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myMultitouchPingPongSurfaceView = new MyMultitouchPingPongSurfaceView(this);
        setContentView(myMultitouchPingPongSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stopping the thread
        if (myMultitouchPingPongSurfaceView != null) myMultitouchPingPongSurfaceView.stopThread();
    }

}
