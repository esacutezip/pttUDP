package litbang.hariff.litbangradio.calling.Service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ActivityGround extends Activity {
    public static String TAG = "sdsa";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if((intent!=null)&&(intent.getStringExtra("link")!=null)) {
            String data = intent.getStringExtra("link");
            Log.d(TAG , "SERVICE STARTING : "+data);
            if (data.equalsIgnoreCase("true")) {
                FloathingWidgetService.playservice = true;
                FloathingWidgetService.plaayy = 0;
            }else if(data.equalsIgnoreCase("false")){
                FloathingWidgetService.playservice = false;
                FloathingWidgetService.plaayy = 0;
            }
        }


        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
