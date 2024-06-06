package litbang.hariff.litbangradio.calling;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
public class App extends Application {
    private  boolean mBound = false;
    Context ctx;
    public static SharedPreferences pref;
    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        MainActivity.Ipsender = pref.getString("ipsender", "192.168.66.39");
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        MainActivity.myip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("myip", MainActivity.myip);
        editor.commit();
        MainActivity.myip = pref.getString("myip", "127.0.0.1");
        MainActivity.port = Integer.valueOf(pref.getString("portsender", "50005"));
        MainActivity.port2 = Integer.valueOf(pref.getString("portreceive", "50004"));
        MainActivity.portdata = Integer.valueOf(pref.getString("portdata", "50006"));
        MainActivity.portdata2 = Integer.valueOf(pref.getString("portdata2", "50007"));
        MainActivity.FramePlay = Integer.valueOf(pref.getString("frameplay", "20"));
        //startDataServiceCCU(ctx);
        //startDataServiceSound(ctx);

    }



    private static void endDataServiceCCU(Context ctx) {
        litbang.hariff.litbangradio.calling.Service.DataService.forFlag = false;
        litbang.hariff.litbangradio.calling.Service.DataService.finishFlag = true;
        Intent ds2Intent = new Intent(ctx,litbang.hariff.litbangradio.calling.Service.DataService.class);
        ctx.stopService(ds2Intent);
    }
    //============================BMS================================

    private static void startDataServiceCCU(Context ctx) {

        litbang.hariff.litbangradio.calling.Service.DataService.forFlag = true;
        litbang.hariff.litbangradio.calling.Service.DataService.finishFlag = false;
        Intent ds2Intent = new Intent(ctx,litbang.hariff.litbangradio.calling.Service.DataService.class);
        ctx.startService(ds2Intent);
    }

    public void closingsocket(){
            Log.d("SERVICE :", "litbang.hariff.litbangradio.calling.Service.DataService = " + litbang.hariff.litbangradio.calling.Service.DataService.socket);
            if (litbang.hariff.litbangradio.calling.Service.DataService.socket == null) { } else {
                if (!litbang.hariff.litbangradio.calling.Service.DataService.socket.isClosed())
                    litbang.hariff.litbangradio.calling.Service.DataService.socket.close();
            }
    }

    private static void endDataServiceSound(Context ctx) {
        Intent ds2Intent = new Intent(ctx,litbang.hariff.litbangradio.calling.Service.DataServiceSound.class);
        ctx.stopService(ds2Intent);
    }
    //============================BMS================================

    private static void startDataServiceSound(Context ctx) {
        Intent ds2Intent = new Intent(ctx,litbang.hariff.litbangradio.calling.Service.DataServiceSound.class);
        ctx.startService(ds2Intent);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        //closingsocket();
        //endDataServiceCCU(ctx);
        //endDataServiceSound(ctx);
    }
}
