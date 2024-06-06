package litbang.hariff.litbangradio.calling.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import litbang.hariff.litbangradio.calling.MainActivity;
import litbang.hariff.litbangradio.calling.Service.Model.ConfigWifiModel;
import litbang.hariff.litbangradio.calling.Service.Model.DataParserVoice;
import litbang.hariff.litbangradio.calling.Service.Model.ModelReceivePacket;
import litbang.hariff.litbangradio.calling.Service.Model.NetStatusEnum;
import litbang.hariff.litbangradio.calling.Service.Model.SetWifiConnection;


public class DataServiceSound extends Service {

    public String TAG = "ServiceSound";
    public static ArrayList<byte[]> jitter;
    public static ArrayList<short[]> jitter2;
    public static boolean play = true;
    public static int plays = 0 , NUM_CHANNELS = 1;
    public static AudioTrack speaker;
    public static AudioTrack speaker2;
    private int sampleRate2 = 16000;//16000;//11025;//11025;//8000;      //How much will be ideal?
    private int channelConfig2 = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat2 = AudioFormat.ENCODING_PCM_16BIT;
    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //new PlaySound().execute("");
        int minBufSize = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
        byte[] buffer = new byte[minBufSize];
        speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
        speaker2 = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
        speaker.play();
        speaker2.play();
        new PlaySound().execute("");

        return Service.START_REDELIVER_INTENT;
    }


    public class PlaySound extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            //*
            while(play==true) {
                if(jitter!=null) {
                if(jitter.size()!=0) {
                    if (plays <= jitter.size() - 1) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String currentDateandTime = sdf.format(new Date());
                        Log.d(TAG, "plays " + String.valueOf(plays) + " , jitter : " + jitter.size() + " , DATE : " + currentDateandTime);
                        if(plays<=jitter.size() - 1) {
                            if (MainActivity.phone == false) {
                                speaker.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                            } else {
                                speaker2.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                            }
                        }
                        plays += 1;
                    }
                  }
                }

            }
            //*/
            return "";
        }


        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
