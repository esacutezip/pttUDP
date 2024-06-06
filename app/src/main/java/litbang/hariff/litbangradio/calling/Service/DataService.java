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

import com.score.rahasak.utils.OpusDecoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import litbang.hariff.litbangradio.calling.MainActivity;
import litbang.hariff.litbangradio.calling.Service.Model.ConfigWifiModel;
import litbang.hariff.litbangradio.calling.Service.Model.DataParserVoice;
import litbang.hariff.litbangradio.calling.Service.Model.ModelReceivePacket;
import litbang.hariff.litbangradio.calling.Service.Model.NetStatusEnum;
import litbang.hariff.litbangradio.calling.Service.Model.SetWifiConnection;

import static litbang.hariff.litbangradio.calling.MainActivity.SAMPLE_RATE;


public class DataService extends Service {

    public String LOG_TAG = "BcastDataService";
    public static boolean isIpOk;
    private String ipAddress;
    private int SERVERPORT = 9001;
    Context context;
    public static int cntString;
    public static boolean forFlag = true;
    public static boolean finishFlag = false;
    public static DatagramSocket socket;
    SetWifiConnection setWifiConnection;
    ConfigWifiModel configWifiModel;
    NetStatusEnum statusEnum = NetStatusEnum.DISCONNECTED;
    InetAddress serverAddr;
    ReadThread readThread;


    public static ArrayList<byte[]> jitter;
    public static ArrayList<short[]> jitter2;
    public static boolean play = true;
    public static int plays = 0 , NUM_CHANNELS = 1;
    public static final int SAMPLE_RATE = 16000;//48000//8000//16000
    public static final int FRAME_SIZE = 320;//960;//160;320;
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
        isIpOk = false;
        ipAddress = String.valueOf(MainActivity.myip);
        SERVERPORT = MainActivity.port2;
        context = this;

        Log.d(LOG_TAG, "mIntent:" + intent);
        configWifiModel = (ConfigWifiModel) intent.getParcelableExtra("configWifiModel");


        if (finishFlag) {
            forFlag = false;

            if (socket != null) {
                Log.d(LOG_TAG, "closing socket");
                if(socket.isClosed()){
                    socket.close();
                }else{
                    socket.disconnect();
                }
                Log.d(LOG_TAG, "socket closed.");
            }

        }else{
            new Thread(new InitThread()).start();
            //new ReadThread().execute("");
        }
        return Service.START_REDELIVER_INTENT;

    }

    public class InitThread implements Runnable {

        public void run() {
            try {
                //Log.d(LOG_TAG, "InitThread begins.");
                // connect to socket
                Log.i(LOG_TAG, "checking ip..." + "isIpOk:" + isIpOk + "-" + ipAddress + ":" + SERVERPORT);
                while (!isIpOk) {
                    Log.i(LOG_TAG, "IP is ok");
                    try {

                        if (socket == null || !socket.isBound())  {
                            serverAddr = InetAddress.getByName(ipAddress);
                            socket = new DatagramSocket(SERVERPORT);
                            socket.setReuseAddress(true);
                        }

                        Log.d("LOG_TAG", "socket opened");
                        isIpOk = true;

                        socket.setSoTimeout(10000);
                        statusEnum = NetStatusEnum.CONNECTED;
                        //BlockingQueue queue = new ArrayBlockingQueue(1);
                        ReadDataThread read = new ReadDataThread();
                        Thread readDataThread = new Thread(read);
                        readDataThread.start();
                        //Thread readDataThread = new Thread(read);
                        //readDataThread.start();
                        //ReadPacketVoice voice = new ReadPacketVoice(queue);
                        //Thread threadvoice = new Thread(voice);
                        //threadvoice.start();
                        //new ReadThread().execute("");
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error during connection first:" +  e.getMessage());
                        statusEnum = NetStatusEnum.DISCONNECTED;
                        socket.close();
                        socket = null;
                    }


                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "InitThread--" + e.getMessage());
            }
        }
    }

    public class ReadThread extends AsyncTask<String, Void, String> {
        private StringBuilder sb = new StringBuilder();
        private boolean isnttimeout = true;
        private String byteInput;
        DataParserVoice dataParser = new DataParserVoice();
        private byte[] buffer = new byte[5000];
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {
            new Thread(new InitThread()).start();
            return "";
        }


        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);

        }
    }


    public class ReadDataThread implements Runnable {

        private StringBuilder sb = new StringBuilder();
        private boolean isnttimeout = true;
        private String byteInput;
        DataParserVoice dataParser = new DataParserVoice();
        private byte[] buffer = new byte[5000];
        protected BlockingQueue queue = null;


        @Override
        public void run() {
            // read data
            Looper.prepare();
            //Log.i(LOG_TAG, "Read thread begins.");
            while (forFlag) {
                if(socket!=null){
                    if (!socket.isClosed()) {
                        java.util.Arrays.fill(buffer,(byte) 0);
                        try {
                            DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
                            try {
                                if(socket.isClosed()){
                                    Log.d(LOG_TAG, "if socket.isClosed");
                                }else{
                                    socket.receive(packet); // This method blocks until
                                    Log.d(LOG_TAG, "socket receive");
                                }
                            } catch (SocketTimeoutException e) {
                                isnttimeout = false;
                            }


                            if (isnttimeout) {
                                int countBytesRead = packet.getLength();
                                Log.d(LOG_TAG,"countBytesRead: "+countBytesRead);
                                byte[] buffervoice = new byte[countBytesRead];
                                for (int i = 0; i < countBytesRead; i++) {
                                    buffervoice[i] = buffer[i];
                                    byteInput = String.format(" %02x", buffer[i]);
                                    sb.append(byteInput.trim()); sb.append(" ");
                                }
                                Log.d(LOG_TAG, "DATA VOICE:" + sb.toString());
                                sb.delete(0,sb.length());
                                ModelReceivePacket packets = new ModelReceivePacket();
                                packets.setPacket(buffervoice);
                                packets.setPacketlenght(countBytesRead);
                                queue.put("esa");
                                Log.d("MASUK", "plays send");
                                MainActivity.QueueReceiveVoice.add(Pair.create("ReceiveQueue", (Object) packets));
                            }
                            isnttimeout = true;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "error while getting data", e);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "Socket is closed.");
                        forFlag = false;
                    }
                }
            }
            Looper.loop();

        } //end of void run

    } //end of ReadThread


    public class ReadPacketVoice implements Runnable {
        protected BlockingQueue queue = null;

        public ReadPacketVoice(){
            int minBufSize = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
            byte[] buffer = new byte[minBufSize];
            speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
            speaker2 = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
            speaker.play();
            speaker2.play();
        }

        public ReadPacketVoice(BlockingQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            // read data

            Looper.prepare();
            try {
            while(play==true) {
                if(jitter!=null) {
                    if(jitter.size()!=0) {
                        if (plays <= jitter.size() - 1) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            String currentDateandTime = sdf.format(new Date());

                            if(queue!=null) {
                                if(queue.take().toString().equalsIgnoreCase("esa")){
                                    if(plays<=jitter.size() - 1) {
                                        Log.d(LOG_TAG, "Plays " + String.valueOf(plays)+" , "+jitter.size());
                                        if (MainActivity.phone == false) {
                                            if(speaker!=null) {
                                                speaker.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                                            }
                                        } else {
                                            if(speaker2!=null) {
                                                speaker2.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                                            }
                                        }
                                    }
                                    plays += 1;
                                }
                            }

                            /*
                            if(plays<=jitter.size() - 1) {
                                if (MainActivity.phone == false) {
                                    if(speaker!=null) {
                                        speaker.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                                    }
                                } else {
                                    if(speaker2!=null) {
                                        speaker2.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                                    }
                                }
                            }
                            plays += 1;
                            */
                        }
                    }
                }

            }
            } catch (InterruptedException e) {





                //e.printStackTrace();
            }
            Looper.loop();

        } //end of void run

    } //end of ReadThread

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
