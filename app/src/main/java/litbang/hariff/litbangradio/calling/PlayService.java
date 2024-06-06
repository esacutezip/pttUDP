package litbang.hariff.litbangradio.calling;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import litbang.hariff.litbangradio.calling.Service.FloathingWidgetService;

public class PlayService extends Service {
    public static boolean buttontt = false;
    public static String TAG = "PlayService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        super.onCreate();
        Log.d(TAG , "SERVICE STARTING : "+buttontt);
    }


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG , "SERVICE STARTING : "+buttontt);
        String vvv = intent.getStringExtra("link");
        Log.d(TAG , "SERVICE STARTING : "+vvv);
        if((intent!=null)&&(intent.getStringExtra("link")!=null)) {
            String data = intent.getStringExtra("link");
            if (data.equalsIgnoreCase("true")) {
            FloathingWidgetService.playservice = true;
            FloathingWidgetService.plaayy = 0;
            }
        }
        return 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloathingWidgetService.playservice = false;
        FloathingWidgetService.plaayy = 0;
        Log.d(TAG , "SERVICE DESTROY : "+buttontt);
        stopSelf();
    }
}
