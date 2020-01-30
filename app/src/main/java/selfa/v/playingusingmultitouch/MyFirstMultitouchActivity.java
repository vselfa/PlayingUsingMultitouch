package selfa.v.playingusingmultitouch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MyFirstMultitouchActivity extends MainMenu {

    public static MyFirstMultitouchView myFirstMultitouchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFirstMultitouchView = new MyFirstMultitouchView(this);
        setContentView(myFirstMultitouchView);
    }

}
