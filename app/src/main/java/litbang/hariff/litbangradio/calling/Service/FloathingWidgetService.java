package litbang.hariff.litbangradio.calling.Service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.score.rahasak.utils.OpusDecoder;
import com.score.rahasak.utils.OpusEncoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import litbang.hariff.litbangradio.calling.App;
import litbang.hariff.litbangradio.calling.R;
import litbang.hariff.litbangradio.calling.Service.Model.DataParserVoice;

public class FloathingWidgetService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingWidget;
    public static String TAG = "SERVICE";
    LinearLayout xx;
    DatagramSocket socket3 ,socket ,socket4 ,socket2;
    SharedPreferences pref;
    public static int port=50005;
    public static int port2=50004;
    public static int portdata=50006;
    public static int portdata2=50007;
    public static String Ipsender = "192.168.66.39";
    public static String myip = "127.0.0.1";
    Context ctx;
    AudioThread mAudioThread;
    SendThread sendthread;
    ReadThread read;
    public static boolean mIsStarted = false , status = false ,closebutton=false ,status4=true ,status2 = false ,
                            play =false ,phone=false;
    public static int plays = 0;
    Thread receiveThread2 ,receiveThread;
    public static ArrayList<byte[]> jitter;
    public static ArrayList<short[]> jitter2;
    static final int SAMPLE_RATE = 8000;
    static final int FRAME_SIZE = 160;
    static final int NUM_CHANNELS = 1;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public static AudioTrack speaker;
    public static AudioTrack speaker2;
    public static ImageView statusview = null;
    public static boolean updateui =true ;
    public static boolean update ;
    public static boolean playservice = false;
    public static int plaayy = 1;
    TimerTask mTt1;
    Timer mTimer1;
    Handler mTimerHandler = new Handler();
    ImageView Calling;
    public FloathingWidgetService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        LoadPreference();
        jitter = new ArrayList<>();
        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floathing_widget, null);
        final WindowManager.LayoutParams params ;
        myip = pref.getString("myip", "127.0.0.1");
        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, channelConfig, audioFormat);
        byte[] buffer = new byte[minBufSize];
        speaker = new AudioTrack(AudioManager.STREAM_MUSIC,SAMPLE_RATE,channelConfig,audioFormat,minBufSize,AudioTrack.MODE_STREAM);
        speaker2 = new AudioTrack(AudioManager.STREAM_VOICE_CALL,SAMPLE_RATE,channelConfig,audioFormat,minBufSize,AudioTrack.MODE_STREAM);
        speaker.play();
        speaker2.play();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);

        final View collapsedView = mFloatingWidget.findViewById(R.id.collapse_view);
        final View expandedView = mFloatingWidget.findViewById(R.id.expanded_container);
        final ImageView closeButton = mFloatingWidget.findViewById(R.id.close_btn);
        Calling = mFloatingWidget.findViewById(R.id.collapsed_iv);
         statusview = mFloatingWidget.findViewById(R.id.collapsed_status);
        Calling.setBackgroundResource(R.drawable.mic);
        statusview.setBackgroundResource(R.drawable.ic_swap);
        //*
        //ImageView closeButtonCollapsed = mFloatingWidget.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(receiveThread2!=null) {
                    receiveThread2.interrupt();
                    receiveThread2 = null;
                }
                */
                stopSelf();
            }
        });


        //*/
        /*
        Calling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //collapsedView.setVisibility(View.VISIBLE);
                //expandedView.setVisibility(View.GONE);
            }
        });
        */

        /*
        closeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG , "presdown");
                        byte[] data = new byte[2];
                        data[0] = 0x01;
                        data[1] = 0x01;
                        StartService(data);
                        status = true;
                        //startStreaming();
                        start();
                        closeButton.setBackgroundResource(R.drawable.mic2);
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG , "presup");
                        status = false;
                        //recorder.release();
                        closeButton.setBackgroundResource(R.drawable.mic);
                        stop();
                        byte[] data2 = new byte[2];
                        data2[0] = 0x01;
                        data2[1] = 0x02;
                        StartService(data2);
                        break;
                }
                // tell the system that we handled the event but a further processing is required
                return false;
            }
        });
        */

        mFloatingWidget.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        if (Xdiff < 10 && Ydiff < 10) {

                            if(closebutton==false){
                                closebutton= true;
                                Log.d(TAG , "presdown");
                                byte[] data = new byte[2];
                                data[0] = 0x01;
                                data[1] = 0x01;
                                StartService(data);
                                status = true;
                                start();
                                Calling.setBackgroundResource(R.drawable.mic2);
                            }else if(closebutton==true){
                                closebutton = false;
                                Log.d(TAG , "presup");
                                status = false;
                                Calling.setBackgroundResource(R.drawable.mic);
                                stop();
                                byte[] data2 = new byte[2];
                                data2[0] = 0x01;
                                data2[1] = 0x02;
                                StartService(data2);
                            }

                            /*
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                            */
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingWidget, params);
                        return true;
                }
                return false;
            }
        });

        //*
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        // TODO
                        Log.d(TAG , "BERUBAH Service: "+update);
                        if(update==false) {
                            statusview.setBackgroundResource(R.drawable.ic_swap);
                        }else if(update==true) {
                            statusview.setBackgroundResource(R.drawable.ic_swap_green);
                        }

                        //*
                        if(plaayy==0) {
                            if (playservice == true) {
                                closebutton = true;
                                Log.d(TAG, "presdown");
                                byte[] datax = new byte[2];
                                datax[0] = 0x01;
                                datax[1] = 0x01;
                                StartService(datax);
                                status = true;
                                start();
                                Calling.setBackgroundResource(R.drawable.mic2);
                                plaayy += 1;
                            }else if(playservice==false) {
                                closebutton = false;
                                Log.d(TAG, "presup");
                                status = false;
                                Calling.setBackgroundResource(R.drawable.mic);
                                stop();
                                byte[] data2 = new byte[2];
                                data2[0] = 0x01;
                                data2[1] = 0x02;
                                StartService(data2);
                                plaayy += 1;
                            }
                        }
                       //*/
                    }
                });
            }
        };
        mTimer1.schedule(mTt1, 0, 200);
        //*/
        //StartReceivingData();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG , "START SERVICE : "+closebutton);
        if((intent!=null)&&(intent.getStringExtra("link")!=null)) {
        String data = intent.getStringExtra("link");
            if(data.equalsIgnoreCase("true")){
                closebutton= true;
                Log.d(TAG , "presdown");
                byte[] datax= new byte[2];
                datax[0] = 0x01;
                datax[1] = 0x01;
                StartService(datax);
                status = true;
                start();
                Calling.setBackgroundResource(R.drawable.mic2);
            }else  if(data.equalsIgnoreCase("false")){
                closebutton = false;
                Log.d(TAG , "presup");
                status = false;
                Calling.setBackgroundResource(R.drawable.mic);
                stop();
                byte[] data2 = new byte[2];
                data2[0] = 0x01;
                data2[1] = 0x02;
                StartService(data2);
            }
        }
        return 1;
    }
    private boolean isViewCollapsed() {
        return mFloatingWidget == null || mFloatingWidget.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidget != null) mWindowManager.removeView(mFloatingWidget);
        //status4 = false;
        //status2 = false;
        if(mTimer1!=null) {
            mTimer1.cancel();
            mTimer1.purge();
            mTt1.cancel();
            mTt1 = null;
            mTimerHandler = null;
            mTimer1 = null;
        }
        /*
        if(socket3!=null) {
            socket3.close();
            socket3.disconnect();
            socket3 = null;
        }
        if(socket!=null) {
            socket.close();
            socket.disconnect();
            socket = null;
        }
        if(socket4!=null) {
            socket4.close();
            socket4.disconnect();
            socket4 = null;
        }
        if(socket2!=null) {
            socket2.close();
            socket2.disconnect();
            socket2 = null;
        }
        */
    }

    private void LoadPreference() {
        pref  = PreferenceManager
                .getDefaultSharedPreferences(this);
        pref = App.pref;
        Ipsender = pref.getString("ipsender", "192.168.66.39");
        port = Integer.valueOf(pref.getString("portsender", "50005"));
        port2 = Integer.valueOf(pref.getString("portreceive", "50004"));
        portdata = Integer.valueOf(pref.getString("portdata", "50006"));
        portdata2 = Integer.valueOf(pref.getString("portdata2", "50007"));
    }


    public void StartService(byte[] data){
        //new SendCommandService().execute(data);
        try {
            socket3 = new DatagramSocket();
            InetAddress destination = InetAddress.getByName(Ipsender);
            socket3.connect(destination, portdata);
            Log.d(TAG , "SOCKETT : "+Ipsender +" , "+portdata);
            DatagramPacket packet;
            packet = new DatagramPacket (data,data.length,destination,portdata);
            Log.d(TAG , "SOCKETT : "+socket3.isConnected() +" , "+packet);
            if(socket3.isConnected()&&packet!=null) {
                socket3.send(packet);
                socket3.close();
            }else{
                Toast.makeText(ctx , "EROR" , Toast.LENGTH_SHORT).show();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void start() {
        mIsStarted = true;
        Log.d(TAG , "playSend");
        mAudioThread = new AudioThread();
        mAudioThread.start();
    }

    private class AudioThread extends Thread {
        // Sample rate must be one supported by Opus.
        //static final int SAMPLE_RATE = 16000;
        // Number of samples per frame is not arbitrary,
        // it must match one of the predefined values, specified in the standard.
        //static final int FRAME_SIZE = 320;
        // 1 or 2
        //static final int NUM_CHANNELS = 1;
        @Override
        public void run() {
            int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                    NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            // initialize audio recorder
            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufSize);
            // init opus encoder
            //OpusEncoder encoder = new OpusEncoder();
            //encoder.init(SAMPLE_RATE, NUM_CHANNELS, OpusEncoder.OPUS_APPLICATION_VOIP);
            // init audio track
            AudioTrack track = new AudioTrack(AudioManager.STREAM_SYSTEM,
                    SAMPLE_RATE,
                    NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufSize,
                    AudioTrack.MODE_STREAM);
            // init opus decoder
            //OpusDecoder decoder = new OpusDecoder();
            //decoder.init(SAMPLE_RATE, NUM_CHANNELS);
            // start
            recorder.startRecording();
            track.play();

            byte[] inBuf = new byte[FRAME_SIZE * NUM_CHANNELS * 2];
            byte[] encBuf = new byte[1024];
            short[] outBuf = new short[FRAME_SIZE * NUM_CHANNELS];

            try {
                socket = new DatagramSocket();
                DatagramPacket packet;
                final InetAddress destination = InetAddress.getByName(Ipsender);
                while (!Thread.interrupted()) {
                    // Encoder must be fed entire frames.
                    int to_read = inBuf.length;
                    int offset = 0;
                    while (to_read > 0) {
                        int read = recorder.read(inBuf, offset, to_read);
                        if (read < 0) {
                            throw new RuntimeException("recorder.read() returned error " + read);
                        }
                        to_read -= read;
                        offset += read;
                    }

                    //int encoded = encoder.encode(inBuf, FRAME_SIZE, encBuf);
                    //Log.v(TAG, "Encoded " + inBuf.length + " bytes of audio into " + encoded + " bytes");
                    packet = new DatagramPacket(inBuf, inBuf.length, destination, port);
                    socket.send(packet);
                    StringBuilder sb = new StringBuilder();
                    String byteInput;
                    for (int i = 0; i <inBuf.length; i++) {
                        byteInput = String.format(" %02x",inBuf[i]);
                        sb.append(byteInput.trim());
                        sb.append(" ");
                    }
                    Log.d("TAG", "SEND :" +inBuf.length + ":" + sb.toString());
                    sb.delete(0, sb.length());
                }
            } catch(UnknownHostException e) {
                Log.e("VS", "UnknownHostException");
            } catch (IOException e) {
                Log.e("VS", "IOException");
            } finally {
                recorder.stop();
                recorder.release();
                track.stop();
                track.release();
            }
        }
    }
    private void stop() {
        if(mAudioThread!=null) {
            mAudioThread.interrupt();
            try {
                mAudioThread.join();
            } catch (InterruptedException e) {
                Log.w(TAG, "Interrupted waiting for audio thread to finish");
            }
        }
        mIsStarted = false;
        if(sendthread!=null) {
            sendthread.cancel(true);
        }
    }

    public class SendThread extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            mIsStarted = true;
            Log.d(TAG , "playSend");
            mAudioThread = new AudioThread();
            mAudioThread.start();
            return "";
        }
        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);
        }
    }

    public void StartReceivingData(){
        new ReceiveDataTHread().execute("");
        new RunOnUI().execute("");

    }

    public class ReceiveDataTHread extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {
            receiveThread2 = new Thread (new Runnable() {
                @Override
                public void run() {
                    try {

                        InetAddress serverAddr = InetAddress.getByName(myip);
                        socket4 = new DatagramSocket(portdata2, serverAddr);
                        //socket4 = new DatagramSocket(portdata2);
                        //DatagramSocket socket4 = new DatagramSocket(null);
                        //socket4.bind(new InetSocketAddress(myip, portdata2));
                        socket4.setReuseAddress(true);
                        byte[] buffer = new byte[2];
                        Log.d(TAG , "SOCKET $ :"+myip+" , "+portdata2+" , "+status4);
                        while (status4 == true) {
                            try {
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                                if (packet != null) {
                                    socket4.receive(packet);

                                    buffer = packet.getData();
                                    int countBytesRead = packet.getLength();
                                    StringBuilder sb = new StringBuilder();
                                    String byteInput;
                                    for (int i = 0; i < countBytesRead; i++) {
                                        byteInput = String.format(" %02x", buffer[i]);
                                        sb.append(byteInput.trim());
                                        sb.append(" ");
                                    }
                                    Log.d("TAG", "DATA DDD:" + sb.toString());
                                    sb.delete(0, sb.length());
                                    Log.d("TAG", "DATA DDD0:" + buffer.length);
                                    if (buffer[0] == 0x01) {
                                        if (buffer[1] == 0x01) {
                                            status2 = true;

                                            jitter = new ArrayList<>();
                                            if(jitter.size()!=0){
                                                jitter.clear();
                                            }
                                            plays = 0;
                                            phone = false;
                                            update = true;
                                            //statusview.setBackgroundResource(R.drawable.ic_swap_green);
                                            Log.d("TAG", "DATA DDD1:" + update);
                                            startReceiving();

                                            //updateui = true;
                                            //statusview.setBackgroundResource(R.drawable.ic_swap_green);

                                        } else if (buffer[1] == 0x02) {
                                            Log.d("TAG", "DATA DDD2:" + update);
                                            phone = false;
                                            if(read!=null) {
                                                read.cancel(true);
                                            }
                                            if(receiveThread!=null) {
                                                receiveThread.interrupt();
                                            }
                                            //statusview.setBackgroundResource(R.drawable.ic_swap);
                                            //statusview.setBackgroundResource(R.drawable.ic_swap);
                                            update = false;
                                            //updateui = true;
                                        }
                                        Log.d("TAG", "DATA DDD3:" + update);
                                    }



                                }
                            } catch(IOException e) {
                                Log.e("VR", "IOException");
                                e.printStackTrace();
                            }
                        }

                    } catch (SocketException e) {
                        Log.e("VR", "IOException2");
                        socket2=null;
                        socket = null;
                        socket3 = null;
                        socket4=null;
                        e.printStackTrace();
                        //Log.e("VR", "SocketException");
                    } catch (UnknownHostException e) {
                        //Log.e("VR", "IOException3");
                        e.printStackTrace();
                    }
                }

            });
            receiveThread2.start();
            return "";
        }


        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);

        }
    }

    public class RunOnUI extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    //OverviewFragment.refresh(getApplicationContext());
                    //while(updateui==true){
                        System.out.println("BERUBAH : "+update+""+updateui);

                        if(update==false) {
                            statusview.setBackgroundResource(R.drawable.ic_swap);
                        }else if(update==true) {
                            statusview.setBackgroundResource(R.drawable.ic_swap_green);
                        }
                        //updateui=false;
                    //}

                }
            });
            return "";
        }


        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);

        }
    }



    public void startReceiving() {

        read = new ReadThread();
        read.execute("");

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
            jitter = new ArrayList<>();

            receiveThread = new Thread (new Runnable() {

                @Override
                public void run() {
                    try {
                        InetAddress serverAddr = InetAddress.getByName(myip);
                        socket2 = new DatagramSocket(port2 , serverAddr);
                        socket2.setReuseAddress(true);
                        if(socket2.isConnected()){
                            status2 = true;
                        }
                        Log.d("VR", "Socket Created");
                        //minimum buffer size. need to be careful. might cause problems. try setting manually if any problems faced
                        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, channelConfig, audioFormat);
                        byte[] buffer = new byte[minBufSize];
                        OpusDecoder decoder = new OpusDecoder();
                        decoder.init(SAMPLE_RATE, NUM_CHANNELS);
                        while(status2 == true) {
                            try {
                                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);

                                socket2.receive(packet);

                                //Log.d("VR", "Packet Received");
                                //reading content from packet

                                buffer=packet.getData();
                                byte[] buffer1 = new byte[packet.getLength()];
                                StringBuilder sb2 = new StringBuilder();
                                String byteInput2;
                                for (int i = 0; i < packet.getLength(); i++) {
                                    buffer1[i] = buffer[i];
                                    byteInput2 = String.format(" %02x", buffer[i]);
                                    sb2.append(byteInput2.trim()); sb2.append(" ");
                                }
                                Log.d("TAG", "ADA ORIGINAL :"+buffer1.length+ ":"+ sb2.toString());
                                sb2.delete(0,sb2.length());
                                /* encoded
                                short[] outBuf = new short[FRAME_SIZE * NUM_CHANNELS];
                                int decoded = 0;
                                try {
                                    decoded = decoder.decode(buffer1, outBuf, FRAME_SIZE);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                StringBuilder sb = new StringBuilder();
                                String byteInput;
                                for (int i = 0; i < decoded; i++) {
                                    byteInput = String.format(" %02x", outBuf[i]);
                                    sb.append(byteInput.trim()); sb.append(" ");
                                }
                                Log.d("TAG", "ADA Lenght :"+decoded+ ":"+ sb.toString());
                                sb.delete(0,sb.length());
                                //sending data to the Audiotrack obj i.e. speaker
                                */
                                jitter.add(buffer1);

                                //Log.d(TAG, "jitter : " + jitter.size());
                                DataServiceSound.jitter = jitter;
                                DataService.jitter = jitter;
                                if(jitter.size()==20){
                                    play = true;
                                    //new PlaySound().cancel(true);
                                    new PlaySound().execute("play");
                                    //playSoundClip();
                                }
                            } catch(IOException e) {
                                Log.e("VR","IOException");
                                //socket2.close();
                                e.printStackTrace();
                            }
                        }
                    } catch (SocketException e) {
                        Log.e("VR", "SocketException");
                        //} catch (UnknownHostException e) {
                        //e.printStackTrace();
                        //socket2.close();
                    } catch (IOException e) {
                        Log.e("VR", "IOException2");
                        //socket2.close();
                        //e.printStackTrace();
                    }
                }



            });
            receiveThread.start();

            return "";
        }


        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);

        }
    }

    public class PlaySound extends AsyncTask<String, Void, String> {
        boolean ddx= false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {

            //*
            if(jitter.size()!=0) {
                //if(play==true) {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

                while(play==true) {
                    if(plays<=jitter.size()-1) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String currentDateandTime = sdf.format(new Date());
                        Log.d(TAG, "plays " + String.valueOf(plays) + " , jitter : " + jitter.size()+" , DATE : "+currentDateandTime);
                        //speaker.play();
                        if(jitter.size()!=0) {
                            if (phone == false) {
                                speaker.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                            } else {
                                speaker2.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                            }
                        }
                        //jitter.remove(plays);
                        if(plays==jitter.size()-1){
                            //speaker.pause();
                        }
                        plays += 1;
                    }

                }
            }
            //*/
            return "";
        }
        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);
            //play=false;
        }
    }



}
