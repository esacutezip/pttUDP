package litbang.hariff.litbangradio.calling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Layout;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import id.zelory.compressor.Compressor;
import litbang.hariff.litbangradio.calling.Modul.AckImageSender;
import litbang.hariff.litbangradio.calling.Modul.DataImage;
import litbang.hariff.litbangradio.calling.Modul.FileChooser;
import litbang.hariff.litbangradio.calling.Modul.ImageSender;
import litbang.hariff.litbangradio.calling.Modul.Kirim;
import litbang.hariff.litbangradio.calling.Modul.ModelKirim;
import litbang.hariff.litbangradio.calling.Modul.Sender;
import litbang.hariff.litbangradio.calling.Patch.AudioVoice;
import litbang.hariff.litbangradio.calling.Service.DataParser;
import litbang.hariff.litbangradio.calling.Service.DataService;
import litbang.hariff.litbangradio.calling.Service.DataServiceSound;
import litbang.hariff.litbangradio.calling.Service.FloathingWidgetService;
import litbang.hariff.litbangradio.calling.Service.FloatingWindows;
import litbang.hariff.litbangradio.calling.Service.KirimData;
import litbang.hariff.litbangradio.calling.Service.Model.DataParserVoice;
import litbang.hariff.litbangradio.calling.Service.Model.ModelReceivePacket;
import litbang.hariff.litbangradio.calling.Service.ModelPacket;

import com.obteq.android.codec2.Codec2;
import com.score.rahasak.utils.OpusDecoder;
import com.score.rahasak.utils.OpusEncoder;


public class MainActivity extends AppCompatActivity {
    Button btn_start,btn_receiver,btn_startingppt,btnimageViews,btn_phone,btn_cam,btn_camfromfile,btn_SendImage;
    public static boolean phone = false;
    public static ArrayList<byte[]> imageblock;
    public static ArrayList<Boolean> ackimgblock;
    public static ArrayList<byte[]> dataqueue ;
    public static ArrayList<byte[]> imagercvdarray;
    Boolean start_toogle = false,start_receiver = false;
    String TAG = "MainActivity";
    public static boolean keycode = false;
    public static ImageSender imagesenderrcvd;
    public static int SizeImageCutter = 1024;
    public static DatagramSocket socket;
    //public static MulticastSocket socket;
    public static DatagramSocket socket3;
    public static int port=50005;         //which port??
    public static int port2=50004;         //which port??
    public static int portdata=50006;         //which port??
    public static int portdata2=50007;
    public static int portimage=50003;
    public static int portimagercvd=50004;
    public static String Ipsender = "192.168.66.39";
    public static String myip ;
    AudioRecord recorder;
    public static java.util.concurrent.ConcurrentLinkedQueue<Pair<String, Object>> QueueReceiveVoice = new java.util.concurrent.ConcurrentLinkedQueue<>();

    //Audio Configuration.
    private int sampleRate = 8000;//16000;//11025;//11025;//8000;      //How much will be ideal?
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    public static final int REQUEST_PICTURE = 10 , REQUEST_PICTUREFROMPATH = 11;
    public static boolean status = true;
    public String gambar_filesrc = "" ,gambar_fileimage = "";

    public static DatagramSocket socketdata;
    public static DatagramSocket socket2;
    public static DatagramSocket socket4 , socketimage;
    //public static MulticastSocket  socket2;
    public static AudioTrack speaker;
    public static AudioTrack speaker2;
    //Audio Configuration.
    private int sampleRate2 = 8000;//16000;//11025;//11025;//8000;      //How much will be ideal?
    private int channelConfig2 = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat2 = AudioFormat.ENCODING_PCM_16BIT;
    public static java.util.concurrent.ConcurrentLinkedQueue<Pair<String, Object>> QueueRcvd = new java.util.concurrent.ConcurrentLinkedQueue<>();
    Handler handlerqueue = new Handler();
    Handler handlerqueue1 = new Handler();
    Handler handlerqueuevoice = new Handler();
    Handler handlerqueue1voice = new Handler();
    public static boolean status2 = true;
    public static boolean status4 = true;
    public static boolean statusimage = true;
    Thread receiveThread;
    Thread receiveThreaddata;
    Thread receiveThreadimage;
    Thread receiveThread2;
    byte[] bufferx = new byte[5000];
    TextView txtview;
    Context ctx;
    ImageView imageViews,imageViewGambar;
    Sender send;
    public static ProgressBar progressBar;

    public static final int SAMPLE_RATE = 8000;//16000;//48000//8000//16000
    public static final int NUM_CHANNELS = 1;
    public static final int FRAME_SIZE = 160;//320;//960;//160;320;
    public static boolean mIsStarted = false;
    AudioThread mAudioThread;
    public static ArrayList<byte[]> jitter;
    public static ArrayList<short[]> jitter2;
    ArrayList<byte[]> voiceplay;
    KirimData kirim;
    ReadThread read;
    Runnable runnable;
    Handler handlervoice = new Handler();
    public static int CountJitter = 0 , FramePlay = 164 ;
    long FrameLast = 0;
    Thread voicethread;
    short[] buffers;
    public static int plays = 0;
    public static boolean play = false;
    public static boolean pttstart = false;
    Thread thread;
    SendThread sendthread;
    TimerTask mTt1;
    Timer mTimer1;
    Handler mTimerHandler = new Handler();
    ImageView Calling;
    ReceiveDataTHread readReceivethread;
    private View mFloatingWidget;
    AudioManager am;
    AudioVoice audio;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = MainActivity.this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 102);//102
        } else {
        }
        stopService(new Intent(MainActivity.this, FloathingWidgetService.class));

        audio = new AudioVoice(ctx);
        audio.init();

        jitter = new ArrayList<>();
        voiceplay = new ArrayList<>();
        kirim = new KirimData(ctx);
        handlerqueuevoice.postDelayed(runnablequeuevoice, 0);
        //handlervoice.postDelayed(runnablexxx , 0);

        //handlerqueue.postDelayed(HandlerQueue2, 100);
        dataqueue = new ArrayList<>();
        send = new Sender(ctx);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);
        Ipsender = pref.getString("ipsender", "192.168.66.39");
        port = Integer.valueOf(pref.getString("portsender", "50005"));
        port2 = Integer.valueOf(pref.getString("portreceive", "50004"));
        portdata = Integer.valueOf(pref.getString("portdata", "50006"));
        portdata2 = Integer.valueOf(pref.getString("portdata2", "50007"));
        FramePlay = Integer.valueOf(pref.getString("frameplay", "20"));


        btn_start = findViewById(R.id.btn_start);
        btn_receiver = findViewById(R.id.btn_receiver);
        btn_startingppt = findViewById(R.id.btn_startingppt);
        btn_phone = findViewById(R.id.btn_phone);
        btn_phone.setBackgroundColor(Color.RED);
        //imageViews = findViewById(R.id.imageView);
        btnimageViews = findViewById(R.id.btnimageView);
        btnimageViews.setBackgroundColor(Color.RED);
        imageViewGambar = findViewById(R.id.imageViewGambar);
        //imageViews.setBackgroundColor(Color.RED);
        btn_cam = findViewById(R.id.btn_cam);
        btn_camfromfile = findViewById(R.id.btn_camfromfile);
        btn_SendImage = findViewById(R.id.btn_SendImage);
        progressBar = findViewById(R.id.progressBar);

        btn_SendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send_Image("/sdcard/bms/foto/compress/" + gambar_fileimage, gambar_fileimage);
            }
        });

        btn_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilGambar();
            }
        });

        btn_camfromfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = android.os.Process.myPid();
                android.os.Process.killProcess(id);
                /*
                Intent intent1 = new Intent(MainActivity.this, FileChooser.class);
                startActivityForResult(intent1,REQUEST_PICTUREFROMPATH);
                */
            }
        });

        btn_phone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (phone == false) {
                    phone = true;
                    btn_phone.setText("TO BIG SPEAKER");
                    btn_phone.setBackgroundColor(Color.GREEN);
                } else if (phone = true) {
                    phone = false;
                    btn_phone.setText("TO CALL SPEAKER");
                    btn_phone.setBackgroundColor(Color.RED);
                }
            }
        });

        txtview = findViewById(R.id.view_text);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        myip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        txtview.setText("My Ip Address : " + myip);

        //startDataService(ctx);
        //startReceivingData();

        btn_startingppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pttstart == false) {
                    pttstart = true;
                    Log.d(TAG, "presdown");
                    byte[] data = new byte[2];
                    data[0] = 0x01;
                    data[1] = 0x01;
                    StartService(data);
                    status = true;
                    //startStreaming();
                    start();
                    btn_startingppt.setBackgroundResource(R.drawable.mic2);
                } else if (pttstart == true) {
                    pttstart = false;
                    Log.d(TAG, "presup");
                    status = false;
                    //recorder.release();
                    btn_startingppt.setBackgroundResource(R.drawable.mic);
                    stop();
                    byte[] data2 = new byte[2];
                    data2[0] = 0x01;
                    data2[1] = 0x02;
                    StartService(data2);
                }
            }
        });

        /*
        btn_startingppt.setOnTouchListener(new View.OnTouchListener() {
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
                        btn_startingppt.setBackgroundResource(R.drawable.mic2);
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG , "presup");
                        status = false;
                        //recorder.release();
                        btn_startingppt.setBackgroundResource(R.drawable.mic);
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
        /*
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_toogle==false){
                    start_toogle = true;
                    btn_start.setText("Stop Stream");
                    status = true;
                    startStreaming();
                    Log.d("VS","Recorder start");

                }else if(start_toogle==true){
                    start_toogle = false;
                    btn_start.setText("Start Stream");
                    status = false;
                    recorder.release();
                    Log.d("VS","Recorder released");
                }
            }
        });

        btn_receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_receiver==false){
                    start_receiver = true;
                    btn_receiver.setText("Stop Receiver");
                    status2 = true;
                    startReceiving();
                }else if(start_receiver==true){
                    start_receiver = false;
                    btn_receiver.setText("Start Receiver");
                    status2 = false;
                    socket2.close();
                    Log.d("VR","Speaker released");
                }
            }
        });
        */

        //if(receiveThread2==null) {
        //StartReceivingData();
        //}



        //if(receiveThreadimage==null){
          //startReceivingImage();
        //}

        am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        am.setParameters("noise_suppression=auto");
        am.setMode(AudioManager.MODE_IN_CALL);
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.d(TAG, "Audio SCO state: " + state);

                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    /*
                     * Now the connection has been established to the bluetooth device.
                     * Record audio or whatever (on another thread).With AudioRecord you can          record   with an object created like this:
                     * new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                     * AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);
                     *
                     * After finishing, don't forget to unregister this receiver and
                     * to stop the bluetooth connection with am.stopBluetoothSco();
                     */
                    unregisterReceiver(this);
                }

            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));

        //true
        am.setSpeakerphoneOn(true);
        am.setBluetoothScoOn(false);
        //false
        //am.setSpeakerphoneOn(false);
        //am.setBluetoothScoOn(true);

        am.startBluetoothSco();

        int minBufSize = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
        byte[] buffer = new byte[minBufSize];
        speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
        speaker2 = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
        speaker.play();
        speaker2.play();
        DataService.speaker = speaker;
        DataService.speaker2 = speaker2;
        DataServiceSound.speaker = speaker;
        DataServiceSound.speaker2 = speaker2;


        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        // TODO
                        Log.d(TAG , "BERUBAH : "+FloathingWidgetService.update);
                        if(FloathingWidgetService.statusview!=null) {
                            if (FloathingWidgetService.update == false) {
                                FloathingWidgetService.statusview.setBackgroundResource(R.drawable.ic_swap);
                                btnimageViews.setBackgroundColor(Color.RED);
                                //setbtnimage(btnimageViews,Color.RED);
                            } else if (FloathingWidgetService.update == true) {
                                FloathingWidgetService.statusview.setBackgroundResource(R.drawable.ic_swap_green);
                                btnimageViews.setBackgroundColor(Color.GREEN);
                                //setbtnimage(btnimageViews,Color.GREEN);
                            }
                        }else{
                            View mFloatingWidget =  LayoutInflater.from(ctx).inflate(R.layout.layout_floathing_widget, null);
                            FloathingWidgetService.statusview = mFloatingWidget.findViewById(R.id.collapsed_status);
                        }
                        if(FloathingWidgetService.plaayy==0) {
                            if (FloathingWidgetService.playservice == true) {
                                FloathingWidgetService.closebutton = true;
                                Log.d(TAG, "presdown");
                                byte[] datax = new byte[2];
                                datax[0] = 0x01;
                                datax[1] = 0x01;
                                StartService(datax);
                                status = true;
                                start();
                                if(Calling!=null) {
                                    Calling.setBackgroundResource(R.drawable.mic2);
                                }
                                FloathingWidgetService.plaayy += 1;
                            }else if(FloathingWidgetService.playservice==false) {
                                FloathingWidgetService.closebutton = false;
                                Log.d(TAG, "presup");
                                status = false;
                                if(Calling!=null) {
                                    Calling.setBackgroundResource(R.drawable.mic);
                                }
                                stop();
                                byte[] data2 = new byte[2];
                                data2[0] = 0x01;
                                data2[1] = 0x02;
                                StartService(data2);
                                FloathingWidgetService.plaayy += 1;
                            }
                        }
                    }
                });
            }
        };
        mTimer1.schedule(mTt1, 0, 200);

        //startReceivingData();

        //*
        status2 = true;
        jitter = new ArrayList<>();
        if (jitter.size() != 0) {
            jitter.clear();
        }
        CountJitter = 0;
        plays = 0;
        DataServiceSound.plays = 0;
        DataService.plays = 0;
        phone = false;
        FloathingWidgetService.plays = 0;
        FloathingWidgetService.phone = false;
        FloathingWidgetService.update = true;
        startReceiving();
        //*/
    }

    private void unregisterReceiver(Context ctx) {

    }
    public void turnOnBluetooth() {
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    System.err.println("bluetooth connected");
                    unregisterReceiver(this);
                } else if (AudioManager.SCO_AUDIO_STATE_DISCONNECTED == state) {
                    System.err.println("bluetooth disconnected");
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(
                AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));

        try {
            if (am.isBluetoothScoAvailableOffCall()) {
                if (am.isBluetoothScoOn()) {
                    am.stopBluetoothSco();
                    am.startBluetoothSco();
                    System.err.println("Bluetooth SCO On!");
                } else {
                    System.err.println("Bluetooth Sco Off!");
                    am.startBluetoothSco();
                }

            } else {
                System.err.println("Bluetooth SCO not available");
            }
        } catch (Exception e) {
            System.err.println("sco elsepart startBluetoothSCO " + e);
            unregisterReceiver(broadcastReceiver);
        }
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

    public class SendCommandService extends AsyncTask<byte[], Void, String> {
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
        protected String doInBackground(byte[]... params) {

            try {
                socket3 = new DatagramSocket();
                InetAddress destination = InetAddress.getByName(Ipsender);
                socket3.connect(destination, portdata);
                Log.d(TAG , "SOCKETT : "+Ipsender +" , "+portdata);
                DatagramPacket packet;
                packet = new DatagramPacket (params[0],params[0].length,destination,portdata);
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

            return "";
        }


        @Override
        protected void onPostExecute(String resultString) {
            super.onPostExecute(resultString);

        }
    }

    public void startStreaming() {


        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {





                    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                    socket = new DatagramSocket();
                    //socket = new MulticastSocket (port);
                    //InetAddress serverAddr = InetAddress.getByName(Ipsender);
                    //socket.joinGroup(serverAddr);
                    //socket.setReuseAddress(true);
                    Log.d("VS", "Socket Created");

                    //byte[] buffer = new byte[minBufSize];
                    byte[] buffer = new byte[FRAME_SIZE * NUM_CHANNELS * 2];
                    byte[] encBuf = new byte[1024];
                    short[] outBuf = new short[FRAME_SIZE * NUM_CHANNELS];

                    Log.d("VS","Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    final InetAddress destination = InetAddress.getByName(Ipsender);
                    Log.d("VS", "Address retrieved");


                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();

                    //int minBufSizex = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
                    //speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate2,channelConfig2,audioFormat2,minBufSizex,AudioTrack.MODE_STREAM);
                    //speaker.play();
                    OpusEncoder encoder = new OpusEncoder();

                    encoder.init(SAMPLE_RATE, NUM_CHANNELS, OpusEncoder.OPUS_APPLICATION_VOIP);

                    OpusDecoder decoder = new OpusDecoder();
                    decoder.init(SAMPLE_RATE, NUM_CHANNELS);

                    while(status == true) {


                        //reading data from MIC into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        int encoded = encoder.encode(buffer, FRAME_SIZE, encBuf);
                        Log.v(TAG, "Encoded " + buffer.length + " bytes of audio into " + encoded + " bytes");

                        //packet = new DatagramPacket (buffer,buffer.length,destination,port);
                        packet = new DatagramPacket (encBuf,encBuf.length,destination,port);
                        socket.send(packet);

                        int countBytesRead = packet.getLength();
                        StringBuilder sb = new StringBuilder();
                        String byteInput;

                        for (int i = 0; i < minBufSize; i++) {
                            byteInput = String.format(" %02x", buffer[i]);
                            sb.append(byteInput.trim()); sb.append(" ");
                        }
                        Log.d("TAG", "ASELI :"+minBufSize+":" + sb.toString());
                        sb.delete(0,sb.length());

                        for (int i = 0; i < encoded; i++) {
                            byteInput = String.format(" %02x", encBuf[i]);
                            sb.append(byteInput.trim()); sb.append(" ");
                        }
                        Log.d("TAG", "ENCODE :"+encoded+":" + sb.toString());
                        sb.delete(0,sb.length());

                        byte[] encBuf2 = Arrays.copyOf(encBuf, encoded);

                        int decoded = decoder.decode(encBuf2, outBuf, FRAME_SIZE);
                        Log.v(TAG, "Decoded back " + decoded * NUM_CHANNELS * 2 + " bytes");

                        byte[] buffers = new byte[FRAME_SIZE * NUM_CHANNELS * 2];

                        buffers = ShortToByte_Twiddle_Method(outBuf);


                        for (int i = 0; i < buffers.length; i++) {
                            byteInput = String.format(" %02x", buffers[i]);
                            sb.append(byteInput.trim()); sb.append(" ");
                        }
                        Log.d("TAG", "DECODE :"+decoded+":" + sb.toString());
                        sb.delete(0,sb.length());
                       // speaker.write(buffer, 0, minBufSizex);
                    }
                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    Log.e("VS", "IOException");
                }
            }

        });
        streamThread.start();
    }


    public void startReceivingData() {

        receiveThreaddata = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    socketdata = new DatagramSocket(portdata2);
                    Log.d("VR", "Socket Created" + portdata2);
                    byte[] buffer = new byte[20000];
                        try {
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            socketdata.receive(packet);
                            //Log.d("VR", "Packet Received");

                            //reading content from packet
                            buffer = packet.getData();
                            //Log.d("VR", "Packet data read into buffer");
                            int countBytesRead = packet.getLength();
                            StringBuilder sb = new StringBuilder();
                            String byteInput;
                            for (int i = 0; i < countBytesRead; i++) {
                                byteInput = String.format(" %02x", buffer[i]);
                                sb.append(byteInput.trim());
                                sb.append(" ");
                            }
                            Log.d("TAG", "ADA DATA:" + sb.toString());
                            sb.delete(0, sb.length());
                            //sending data to the Audiotrack obj i.e. speaker
                            //DataParser dataParser = new DataParser();
                            //dataParser.parseSbcSentence(buffer);


                            if (buffer[0] == 0x01) {
                                if (buffer[1] == 0x01) {
                                    status2 = true;
                                    //dataqueue.clear();
                                    startReceiving();

                                    setimage(imageViews, Color.GREEN);
                                    //imageViews.setBackgroundResource(R.drawable.point_green);
                                } else if (buffer[1] == 0x02) {
                                    status2 = false;
                                    phone = false;
                                    socket2.close();
                                    setimage(imageViews, Color.RED);

                                    //imageViews.setBackgroundResource(R.drawable.point_red);
                                }
                            }
                            //Log.d("VR", "Writing buffer content to speaker");
                        } catch (IOException e) {
                            Log.e("VR", "IOException");
                        }
                    } catch (SocketException e1) {
                    e1.printStackTrace();
                }
            }
        });
        receiveThreaddata.start();
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
                        //InetAddress serverAddr = InetAddress.getByName("0.0.0.0");
                        InetAddress serverAddr = InetAddress.getByName(MainActivity.myip);
                        socket2 = new DatagramSocket(port2 , serverAddr);
                        socket2.setReuseAddress(true);
                        Log.d("VR", "Socket Created " + MainActivity.myip+" , "+port2+ " , "+status2);
                        //minimum buffer size. need to be careful. might cause problems. try setting manually if any problems faced
                        int minBufSize = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
                        byte[] buffer = new byte[minBufSize];

                        //OpusDecoder decoder = new OpusDecoder();
                        //decoder.init(SAMPLE_RATE, NUM_CHANNELS);
                        while(status2 == true) {
                            Log.d("VR", "Socket RECEIVE"+status2);
                            try {
                                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);

                                socket2.receive(packet);

                                Log.d("VR", "Packet Received");
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
                                /* decode opus
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
                               //*/

                                jitter.add(buffer1);

                                Log.d(TAG, "jitter : " + jitter.size());
                                DataServiceSound.jitter = jitter;
                                DataService.jitter = jitter;
                                if(jitter.size()==FramePlay){
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
    public void startReceiving() {

        read = new ReadThread();
        read.execute("");

    }

    public void startReceivingImage() {

        receiveThreadimage = new Thread (new Runnable() {

            @Override
            public void run() {
                try {
                    socketimage = new DatagramSocket(portimage);
                    Log.d("VR", "Socket Created");
                    while(statusimage == true) {
                        try {
                            java.util.Arrays.fill(bufferx,(byte) 0);

                            DatagramPacket packet = new DatagramPacket(bufferx,bufferx.length);
                            socketimage.receive(packet);
                            byte[] buffer = packet.getData();
                            int countBytesRead = packet.getLength();
                            StringBuilder sb = new StringBuilder();
                            String byteInput;
                            for (int i = 0; i < countBytesRead; i++) {
                                byteInput = String.format(" %02x", buffer[i]);
                                sb.append(byteInput.trim()); sb.append(" ");
                            }
                            Log.d("TAG", "ADA :" + sb.toString());
                            sb.delete(0,sb.length());

                        } catch(IOException e) {
                            Log.e("VR","IOException");
                        }
                    }
                } catch (SocketException e) {
                    Log.e("VR", "SocketException");
                }
            }

        });
        receiveThreadimage.start();

    }

    public class ReceiveDataTHread extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {

                    receiveThread2 = new Thread(new Runnable() {
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
                                Log.d(TAG, "SOCKET $ :" + myip + " , " + portdata2 + " , " + status4);
                                while (status4 == true) {
                                    try {
                                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                                        if (packet != null) {
                                            if (socket4.isClosed()) {
                                            } else {
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
                                                if (buffer[0] == 0x01) {
                                                    if (buffer[1] == 0x01) {
                                                        status2 = true;
                                                        //setphone(btn_phone, Color.RED, "TO CALL SPEAKER");
                                                        //setbtnimage(btnimageViews, Color.GREEN);
                                                        //btnimageViews.setBackgroundColor(Color.GREEN);
                                                        jitter = new ArrayList<>();
                                                        if (jitter.size() != 0) {
                                                            jitter.clear();
                                                        }
                                                        CountJitter = 0;
                                                        plays = 0;
                                                        DataServiceSound.plays = 0;
                                                        DataService.plays = 0;
                                                        phone = false;
                                                        FloathingWidgetService.plays = 0;
                                                        FloathingWidgetService.phone = false;
                                                        FloathingWidgetService.update = true;
                                                        startReceiving();
                                                        //handlervoice.postDelayed(runnablexxx , 0);
                                                        //imageViews.setBackgroundResource(R.drawable.point_green);
                                                    } else if (buffer[1] == 0x02) {
                                                        //status2 = false;
                                                        phone = false;
                                                        FloathingWidgetService.plays = 0;
                                                        FloathingWidgetService.phone = false;
                                                        FloathingWidgetService.update = false;
                                                        //if(socket2!=null) {
                                                        //socket2.close();
                                                        //}
                                                        //setimage(imageViews,R.drawable.point_red);
                                                        //setbtnimage(btnimageViews, Color.RED);
                                                        //handlervoice.removeCallbacks(runnablexxx);
                                                        if (read != null) {
                                                            read.cancel(true);
                                                        }
                                                        if (receiveThread != null) {
                                                            receiveThread.interrupt();
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        Log.e("VR", "IOException");
                                        e.printStackTrace();
                                    }
                                }

                            } catch (SocketException e) {
                                Log.e("VR", "IOException2");
                                e.printStackTrace();
                                //Log.e("VR", "SocketException");
                            } catch (UnknownHostException e) {
                                Log.e("VR", "IOException3");
                                e.printStackTrace();
                            }
                        }

                    });
                    receiveThread2.start();

                return "";
            }


            @Override
            protected void onPostExecute (String resultString){
                super.onPostExecute(resultString);

            }

    }

    public void StartReceivingData(){

        readReceivethread = new ReceiveDataTHread();
        readReceivethread.execute("");


    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        stopDataService(ctx);
        if(mTimer1!=null) {
            mTimer1.cancel();
            mTimer1.purge();
            mTt1.cancel();
            mTt1 = null;
            mTimerHandler = null;
            mTimer1 = null;
        }
        //receiveThread2.interrupt();
        //readReceivethread.cancel(true);
        //if(receiveThread2!=null) {
            //receiveThread2.interrupt();
            //receiveThread2 = null;
        //}
        //status4 = false;
        //status2 = false;
        //status = false;
        //*
        if(socket3!=null) {
            //socket3.close();
            //socket3.disconnect();
            //socket3 = null;
        }
        if(socket!=null) {
            //socket.close();
            //socket.disconnect();
            //socket = null;
        }
        if(socket4!=null) {
            //socket4.close();
            //socket4.disconnect();
            //socket4 = null;
        }
        if(socket2!=null) {
            //socket2.close();
            //socket2.disconnect();
            //socket2 = null;
        }
        //*/
        kirim.SendOff();
        handlerqueuevoice.removeCallbacks(runnablequeuevoice);
        handlervoice.removeCallbacks(runnablexxx);
        // speaker.flush();
        //speaker.stop();
        // speaker.release();
        //speaker2.flush();
        //speaker2.stop();
        //speaker2.release();
        //statusimage = false;
        //socketimage.close();

        //int id= android.os.Process.myPid();
        //android.os.Process.killProcess(id);

        startService(new Intent(MainActivity.this, FloathingWidgetService.class));
    }
    private void setimage(final ImageView image,final int value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image.setBackgroundColor(value);
            }
        });
    }
    private void setbtnimage(final Button image,final int value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image.setBackgroundColor(value);
            }
        });
    }
    private void setphone(final Button image,final int value , final String phonee){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image.setBackgroundColor(value);
                image.setText(phonee);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    private void start() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    1);
            return;
        }
        mIsStarted = true;
        Log.d(TAG , "playSend");
        mAudioThread = new AudioThread();
        mAudioThread.start();
        //sendthread = new SendThread();
        //sendthread.execute("");
    }

    public class SendThread extends AsyncTask<String, Void, String> {
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



@Override
protected void onPause() {
    super.onPause();
        if (mIsStarted) {
            stop();
        }
 }

@Override
public void onResume() {
    super.onResume();
    final SharedPreferences pref = PreferenceManager
            .getDefaultSharedPreferences(this);
    final SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);
    Ipsender = pref.getString("ipsender", "192.168.66.39");
    port = Integer.valueOf(pref.getString("portsender", "50005"));
    port2 = Integer.valueOf(pref.getString("portreceive", "50004"));
    portdata = Integer.valueOf(pref.getString("portdata", "50006"));
    portdata2 = Integer.valueOf(pref.getString("portdata2", "50007"));
    FramePlay = Integer.valueOf(pref.getString("frameplay", "20"));
    /*
    if(receiveThread2!=null){
        socket4.close();
        socket4 = null;
        StartReceivingData();
    }
    */


}

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i(TAG, "Result: " + resultCode + " from request: " + requestCode);
        Log.i(TAG, "Result Code: " + String.valueOf(resultCode));

        switch (requestCode) {
            case REQUEST_PICTURE:
                try {
                    File compressedImage = new Compressor(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath("/sdcard/bms/foto/compress/")
                            .compressToFile(new File(gambar_filesrc));

                    File filecompress = new File("/sdcard/bms/foto/compress/"+gambar_fileimage);
                    if(filecompress.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(filecompress.getAbsolutePath());
                        imageViewGambar.setImageBitmap(myBitmap);
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    Toast.makeText(ctx,"EROR COMPRESS" , Toast.LENGTH_LONG);
                }

                break;
            case REQUEST_PICTUREFROMPATH:
                if(intent!=null) {
                    String curFileName = intent.getStringExtra("GetFileName");
                    String GetPath = intent.getStringExtra("GetPath");
                    Log.d(TAG, "DDD:" + curFileName + " : " + GetPath);
                    gambar_filesrc = GetPath + "/" + curFileName;
                    gambar_fileimage = curFileName;
                    Log.d(TAG, "DDD:" + gambar_filesrc);

                    File imgFile = new File(gambar_filesrc);
                    try {
                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath("/sdcard/bms/foto/compress/")
                                .compressToFile(new File(gambar_filesrc));

                        File filecompress = new File("/sdcard/bms/foto/compress/" + gambar_fileimage);
                        if (filecompress.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(filecompress.getAbsolutePath());
                            imageViewGambar.setImageBitmap(myBitmap);
                        }
                    } catch (IOException e) {
                        //e.printStackTrace();
                        Toast.makeText(ctx, "EROR COMPRESS", Toast.LENGTH_LONG);
                    }
                }
                break;
            default:
                break;
        }
    }

public void Send_Image(String path,String filename){
    File file = new File(path);
    imageblock = new ArrayList<>();
    ackimgblock = new ArrayList<>();
    byte[] dataimage  = null;
    try {
        dataimage = readByteFile(file);
        imageblock = ParseImageBlock(dataimage);
    }catch(Exception e){}

    ImageSender sender = new ImageSender();
    sender.setData(imageblock.get(0));
    sender.setArraysize(imageblock.size());
    progressBar.setMax(imageblock.size());
    for(int i = 0 ;i<imageblock.size();i++){
        ackimgblock.add(false);
    }

    sender.setArraynow(0);
    sender.setSizeimage(dataimage.length);
    sender.setTitle(filename);
    sender.setLocation(path);

    Kirim kirim = new Kirim();
    kirim.setImageSender(sender);
    if(imageblock.size()!=0) {
        send.Packet("IMAGEINIT", kirim);
    }

}

    private ArrayList<byte[]> ParseImageBlock(byte[] data) {
        ArrayList<byte[]> dataimagesplit =new ArrayList<>();
        int blockSize = SizeImageCutter;
        int blockCount = (data.length + blockSize - 1) / blockSize;

        byte[] range = null;

        for (int i = 1; i < blockCount; i++) {
            int idx = (i - 1) * blockSize;
            range = Arrays.copyOfRange(data, idx, idx + blockSize);
            dataimagesplit.add(range);
       }
        int end = -1;
        if (data.length % blockSize == 0) {
            end = data.length;
        } else {
            end = data.length % blockSize + blockSize * (blockCount - 1);
        }

        range = Arrays.copyOfRange(data, (blockCount - 1) * blockSize, end);
        dataimagesplit.add(range);
        return dataimagesplit;
    }

    private byte[] readByteFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

public void AmbilGambar(){
    //pointermessage = "image";
    String timeStamp;
    String sendDateHex = Long.toHexString(System.currentTimeMillis()/1000);
    long lRcvDate = Long.parseLong(sendDateHex, 16) * 1000;
    String Date2 = (String) DateFormat.format("dd:MM:yy-hh:mm:ss", lRcvDate);

    timeStamp = Date2;
    String imageFileName = "UT_" + timeStamp + ".jpg";

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    String picturePath = "/sdcard/bms/foto/" + imageFileName;
    gambar_filesrc = picturePath;
    gambar_fileimage = imageFileName;
    File file2 = new File("/sdcard/bms/foto/");
    if(!file2.exists()){
        file2.mkdir();
    }

    File f = new File("/sdcard/bms/foto/" + imageFileName);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
    intent.putExtra("path", picturePath);

    Log.i(TAG, "File: " + f.toString());
    startActivityForResult(intent, REQUEST_PICTURE);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_name:
                Intent modifySettings=new Intent(ctx, SettingActivity.class);
                startActivity(modifySettings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static void startDataService(Context ctx) {
        litbang.hariff.litbangradio.calling.Service.DataService.forFlag = true;
        litbang.hariff.litbangradio.calling.Service.DataService.finishFlag = false;
        Intent DataTestService = new Intent(ctx,litbang.hariff.litbangradio.calling.Service.DataService.class);
        ctx.startService(DataTestService);
        Log.i("SRV-DATA SERVICE", "startDataService");
    }
    private static void stopDataService(Context ctx) {
        litbang.hariff.litbangradio.calling.Service.DataService.forFlag = false;
        litbang.hariff.litbangradio.calling.Service.DataService.finishFlag = true;
        Intent DataTestService = new Intent(ctx,litbang.hariff.litbangradio.calling.Service.DataService.class);
        ctx.stopService(DataTestService);
        Log.i("SRV-DATA SERVICE", "stopDataService");
    }


    Runnable HandlerQueue2 = new Runnable() {
        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            while (!QueueRcvd.isEmpty()) {
                Pair<String, Object> data = QueueRcvd.poll();
                if (data != null) {
                    switch (data.first) {
                        case "IMAGEINIT":
                            if (data.second != null) {
                                ImageSender image = (ImageSender) data.second;
                                imagercvdarray = new ArrayList<>();
                                imagesenderrcvd = new ImageSender();
                                imagesenderrcvd = image;
                                progressBar.setMax((int)image.arraysize);
                                progressBar.setProgress((int) image.arraynow);
                                progressBar.invalidate();
                                imagercvdarray.add(image.getData());
                                AckImageSender ack = new AckImageSender();
                                ack.setArraynow(image.arraynow);
                                ack.setSize(image.getSizeimage());
                                ack.setArraysize(image.getArraysize());
                                Kirim kirim = new Kirim();
                                kirim.setAckImageSender(ack);
                                send.Packet("IMAGEACK",kirim);
                            }
                            break;
                        case "IMAGE":
                            if (data.second != null) {
                                ImageSender image = (ImageSender) data.second;
                                imagercvdarray.add(image.getData());
                                AckImageSender ack = new AckImageSender();
                                ack.setArraynow(image.arraynow);
                                ack.setSize(image.getSizeimage());
                                ack.setArraysize(image.getArraysize());
                                Kirim kirim = new Kirim();
                                kirim.setAckImageSender(ack);
                                send.Packet("IMAGEACK",kirim);
                                progressBar.setProgress((int) image.arraynow);
                                progressBar.invalidate();
                                if(image.getArraynow()==image.getArraysize()-1) {
                                    try {
                                        int xx = 0;
                                        byte[] result = new byte[(int) image.getSizeimage()];
                                        for (int i = 0; i < imagercvdarray.size(); i++) {
                                            for (int x = 0; x < imagercvdarray.get(i).length; x++) {
                                                result[xx] = imagercvdarray.get(i)[x];
                                                xx += 1;
                                            }
                                        }
                                        saveReceivedImage(result, result.length, imagesenderrcvd.getTitle());
                                        File dsa = new File("/sdcard/bms/foto/"+imagesenderrcvd.getTitle());
                                        if(dsa.exists()) {
                                            Bitmap myBitmap = BitmapFactory.decodeFile("/sdcard/bms/foto/" + imagesenderrcvd.getTitle());
                                            imageViewGambar.setImageBitmap(myBitmap);
                                        }
                                        progressBar.setProgress((int) image.arraysize);
                                        progressBar.invalidate();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                }
                            break;
                        case "IMAGEACK":
                            if (data.second != null) {
                                AckImageSender ack = (AckImageSender) data.second;

                                //imagercvdarray.add(ack);


                                int block = 0;
                                int c = 0;
                                int d = 1;
                                for(int i = 0;i<ackimgblock.size();i++){

                                    if(i==ack.getArraynow()){
                                        ackimgblock.set(i , true);
                                        progressBar.setProgress(i);
                                        progressBar.invalidate();
                                    }
                                    if(ackimgblock.get(i)==false&&c<1){
                                        block = i;
                                        c+=1;
                                        d += 1;
                                    }else{}
                                    Log.d(TAG , "ACK : ("+i+") "+ackimgblock.get(i));

                                }
                                Log.d(TAG , "KIRIMAN : "+block);

                                if(d==1) {

                                }else{
                                    ImageSender sender = new ImageSender();
                                    sender.setData(imageblock.get(block));
                                    sender.setArraysize(imageblock.size());
                                    sender.setArraynow(block);
                                    sender.setSizeimage(ack.getSize());
                                    //sender.setSizeimage(dataimage.length);
                                    //sender.setTitle(filename);
                                    //sender.setLocation(path);
                                    Kirim kirim = new Kirim();
                                    kirim.setImageSender(sender);
                                    send.Packet("IMAGE", kirim);
                                }

                                if(ack.getArraysize()-1==ack.getArraynow()){
                                    progressBar.setProgress((int) ack.arraysize);
                                    progressBar.invalidate();
                                }
                            }
                            break;
                    }
                }
            }
            handlerqueue1.postDelayed(this, Integer.parseInt("100"));

        }
    };

    private void saveReceivedImage(byte[] imageByteArray, int numberOfBytes, String imageName){
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, numberOfBytes);
            File path = new File("/sdcard/bms/foto/");
            if(!path.exists()){
                path.mkdirs();
            }
            File outFile = new File(path, imageName);
            if(!outFile.exists()){
                outFile.createNewFile();
            }
            Log.e(TAG, "Create File : "+path+imageName);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Saving received message failed with", e);
        } catch (IOException e) {
            Log.e(TAG, "Saving received message failed with", e);
        }
    }

    private static String byteArrayToHexString(byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }


    byte[] tobytes(short[] shorts, boolean bigendian) {
        int n = 0;
        byte[] bytes = new byte[2*shorts.length];

        for (n=0; n < shorts.length; n++) {
            byte lsb = (byte) (shorts[n] & 0xff);
            byte msb = (byte) ((shorts[n] >> 8) & 0xff);
            if (bigendian) {
                bytes[2*n]   = msb;
                bytes[2*n+1] = lsb;
            } else {
                bytes[2*n]   = lsb;
                bytes[2*n+1] = msb;
            }
        }
        return bytes;
    }

    byte [] ShortToByte_Twiddle_Method(short [] input)
    {
        int short_index, byte_index;
        int iterations = input.length;

        byte [] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for(/*NOP*/; short_index != iterations; /*NOP*/)
        {
            buffer[byte_index]     = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }
    byte [] ShortToByte_ByteBuffer_Method(short [] input)
    {
        int index;
        int iterations = input.length;

        ByteBuffer bb = ByteBuffer.allocate(input.length * 2);

        for(index = 0; index != iterations; ++index)
        {
            bb.putShort(input[index]);
        }

        return bb.array();
    }


    private class AudioThread extends Thread {
        // Sample rate must be one supported by Opus.
        //static final int SAMPLE_RATE = 8000;
        // Number of samples per frame is not arbitrary,
        // it must match one of the predefined values, specified in the standard.
        //static final int FRAME_SIZE = 960;
        // 1 or 2
        static final int NUM_CHANNELS = 1;
        @Override
        public void run() {
            int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                    NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            // initialize audio recorder
            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,//MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufSize);
            // init opus encoder
            OpusEncoder encoder = new OpusEncoder();
            encoder.init(SAMPLE_RATE, NUM_CHANNELS, OpusEncoder.OPUS_APPLICATION_VOIP);
            // init audio track
            AudioTrack track = new AudioTrack(AudioManager.STREAM_SYSTEM,
                    SAMPLE_RATE,
                    NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufSize,
                    AudioTrack.MODE_STREAM);
            // init opus decoder
            OpusDecoder decoder = new OpusDecoder();
            decoder.init(SAMPLE_RATE, NUM_CHANNELS);
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
                    /*
                    int encoded = encoder.encode(inBuf, FRAME_SIZE, encBuf);
                    //Log.v(TAG, "Encoded " + inBuf.length + " bytes of audio into " + encoded + " bytes");
                    packet = new DatagramPacket(encBuf, encoded, destination, port);
                    socket.send(packet);
                    StringBuilder sb = new StringBuilder();
                    String byteInput;
                    for (int i = 0; i <encoded; i++) {
                        byteInput = String.format(" %02x",encBuf[i]);
                        sb.append(byteInput.trim());
                        sb.append(" ");
                    }
                    Log.d("TAG", "SEND :" +encoded + ":" + sb.toString());
                    sb.delete(0, sb.length());
                    //*/

                    /* codec2
                    char[] sd = new char[5000];
                    int size = inBuf.length;
                    short[] shortArray = getShortAudioBuffer(inBuf);
                    StringBuilder sb3 = new StringBuilder();
                    String byteInput3;
                    for (int index = 0; index < shortArray.length; index++) {
                        byteInput3 = String.format(" %02x",shortArray[index]);
                        sb3.append(byteInput3.trim());
                        sb3.append(" ");
                    }
                    Log.d("TAG", "asal :" +size + ":" + sb3.toString());
                    sb3.delete(0, sb3.length());
                    long con = Codec2.create(Codec2.CODEC2_MODE_2400);
                    int bitsSize = Codec2.getBitsSize(con);
                    int samples = Codec2.getSamplesPerFrame(con)*2;
                    Log.d("TAG", "Sample :" +samples );
                    char[] bits = new char[bitsSize];
                    Codec2.encode(con,shortArray, bits);
                    StringBuilder sb = new StringBuilder();
                    String byteInput;
                    for (int i = 0; i <bits.length; i++) {
                        //byteInput = String.format(" %02x",bits[i]);
                        byteInput = String.valueOf(bits[i]);
                        sb.append(byteInput.trim());
                        sb.append(" ");
                    }
                    Log.d("TAG", "SEND codec2:" +bits.length + ":" + sb.toString());
                    short[] d = new short[samples];
                    Codec2.decode(con,d, bits);
                    StringBuilder sb2 = new StringBuilder();
                    String byteInput2;
                    for (int i = 0; i <d.length; i++) {
                        byteInput2 = String.format(" %02x",d[i]);
                        sb2.append(byteInput2.trim());
                        sb2.append(" ");
                    }
                    Log.d("TAG", "SEND decode:" +d.length + ":" + sb2.toString());
                    speaker.write(d, 0, d.length );
                    Codec2.destroy(con);
                    //*/

                    //Log.d("TAG", "SEND codec2:" +encode + ":" + sd);
                    //kirim.SendPacket(packet , encoded);

                    //decode
                    //byte[] encBuf2 = Arrays.copyOf(encBuf, encoded);
                    //int decoded = decoder.decode(encBuf2, outBuf, FRAME_SIZE);
                    //Log.v(TAG, "Decoded back " + decoded * NUM_CHANNELS * 2 + " bytes");
                    //speaker2.write(outBuf, 0, decoded * NUM_CHANNELS);
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

    public short[] getShortAudioBuffer(byte[] b){
        short audioBuffer[] = null;
        int index = 0;
        int audioSize = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);

        if ((b ==null) && (b.length<2)){
            return null;
        }else{
            audioSize = (b.length - (b.length%2));
            audioBuffer   = new short[audioSize/2];
        }

        if ((audioSize/2) < 2)
            return null;

        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);


        for(int i=0;i<audioSize/2;i++){
            index = i*2;
            byteBuffer.put(b[index]);
            byteBuffer.put(b[index+1]);
            audioBuffer[i] = byteBuffer.getShort(0);
            byteBuffer.clear();
            System.out.print(Integer.toHexString(audioBuffer[i]) + " ");
        }
        System.out.println();

        return audioBuffer;
    }

    Runnable runnablequeuevoice = new Runnable() {
        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            while (!QueueReceiveVoice.isEmpty()) {
                Pair<String, Object> data = QueueReceiveVoice.poll();
                if (data != null) {
                    switch (data.first) {
                        case "ReceiveQueue":
                            if (data.second != null) {
                                ModelReceivePacket datas = (ModelReceivePacket) data.second;
                                Log.d("TAG", "MASUK QUEUE :" + datas.getPacketlenght() );
                                /*
                                OpusDecoder decoder = new OpusDecoder();
                                decoder.init(SAMPLE_RATE, NUM_CHANNELS);
                                short[] outBuf = new short[FRAME_SIZE * NUM_CHANNELS];
                                int decoded = 0;
                                try {
                                    decoded = decoder.decode(datas.getPacket(), outBuf, FRAME_SIZE);
                                }catch (Exception e){}
                                */
                                //Log.v(TAG, "Decoded back " + decoded * NUM_CHANNELS * 2 + " bytes");
                                jitter.add(datas.getPacket());
                                byte[] buffer = new byte[datas.getPacketlenght()];
                                StringBuilder sb1 = new StringBuilder();
                                String byteInput1;
                                for (int i = 0; i < datas.getPacket().length; i++) {
                                    buffer[i] = datas.getPacket()[i];
                                    byteInput1 = String.format(" %02x", datas.getPacket()[i]);
                                    sb1.append(byteInput1.trim()); sb1.append(" ");
                                }
                                //Log.d("TAG", "BUFFERX :"+decoded+ ":"+ sb1.toString());
                                sb1.delete(0,sb1.length());


                                /*
                                int vv = 0;
                                for(int i = 0 ;i<jitter.size();i++){
                                    vv +=jitter.get(i).length;
                                }
                                buffers = buildIntArray(jitter,vv);

                                StringBuilder sb = new StringBuilder();
                                String byteInput;
                                for (int i = 0; i < buffers.length; i++) {
                                    byteInput = String.format(" %02x", buffers[i]);
                                    sb.append(byteInput.trim()); sb.append(" ");
                                }
                                //Log.d("TAG", "BUFFER :"+vv+ ":"+ sb.toString());
                                sb.delete(0,sb.length());
                                //*/

                                /*
                                if (phone == false) {
                                    if(speaker==null){
                                        int minBufSize = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
                                        speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
                                    }
                                    speaker.write(buffer, 0, vv * NUM_CHANNELS);
                                }else{
                                    if(speaker2==null){
                                        int minBufSize = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
                                        speaker2 = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
                                    }
                                    speaker2.write(buffer, 0, vv * NUM_CHANNELS);
                                }
                                */
                                //*

                                //*
                                Log.d(TAG, "jitter : " + jitter.size());
                                DataServiceSound.jitter = jitter;
                                DataService.jitter = jitter;
                                if(jitter.size()==20){
                                    play = true;
                                    DataService.play = true;
                                    //DataServiceSound.play = true;
                                    //new PlaySound().cancel(true);
                                    //new PlaySound().execute("play");
                                    //playSoundClip();
                                }
                                //*/
                                /*
                                if (phone == false) {
                                    speaker.write(outBuf, 0, decoded * NUM_CHANNELS);
                                }else{
                                    speaker2.write(outBuf, 0, decoded * NUM_CHANNELS);
                                }
                                //*/
                            }
                            break;
                    }
                }
            }
            handlerqueuevoice.postDelayed(this, 0);
        }


    };

    private short[] buildIntArray(List<short[]> integers , int size) {
        short[] ints = new short[size];
        int i = 0;
        for(int x=0;x<integers.size();x++) {
            for(int xx = 0;xx<integers.get(x).length;xx++){
                ints[i++] = integers.get(x)[xx];
            }
        }
        return ints;
    }
    Runnable runnablexxx = new Runnable() {

        @Override
        public void run() {
            //Log.d(TAG, "ada " + String.valueOf(FramePlay) + " , jitter : " + jitter.size());
            //*
            //if(play==true){
                //if(plays<=jitter.size()-1){
                    //Log.d(TAG, "plays " + String.valueOf(plays));
                    if(jitter.size()!=0) {
                        if(play==true) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            String currentDateandTime = sdf.format(new Date());
                            Log.d(TAG, "plays " + String.valueOf(plays) + " , jitter : " + jitter.size()+" , DATE : "+currentDateandTime);

                            if(plays<=jitter.size()-1) {
                                //Log.d(TAG, "plays " + String.valueOf(plays) + " , jitter : " + jitter.size());
                                if (phone == false) {
                                    speaker.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                                } else {
                                    speaker2.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                                }
                                plays +=1;
                            }
                        }
                    }
                //}else{play=false;}
                //plays+=1;
            //}

                /*
                if (jitter.size() != 0) {
                    for(int i=0;i<jitter.size();i++) {
                        Log.d(TAG, "Load : " + String.valueOf(CountJitter));
                        if (jitter.get(i) != null) {
                            if (phone == false) {
                                speaker.write(jitter.get(i), 0, jitter.get(i).length * NUM_CHANNELS);
                            } else {
                                speaker2.write(jitter.get(i), 0, jitter.get(i).length * NUM_CHANNELS);
                            }
                            jitter.remove(i);
                            //CountJitter += 1;
                        }
                    }

                }*/


            //FrameLast = Long.valueOf(jitter.size());
            //*/
            //Count += 1;
            handlervoice.postDelayed(this, 0);
        }
    };



    public class PlaySound extends AsyncTask<String, Void, String> {
        boolean ddx= false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {

            /*
            thread = new Thread(new Runnable() {

                public void run() {

                    try {
                        createAudioTrack();

                        while (true) {
                            //if (audio) {
                                playSoundClip();
                            //}


                            Thread.sleep(0);
                        }
                    } catch (InterruptedException e) {
                    } finally {
                        //stopSoundClip();
                    }
                }



            });
            thread.start();
            */
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
                        //if(plays==jitter.size()-1){
                            //play = false;
                        //}
                    //}
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

    private void createAudioTrack() {

        short[] samples = buffers;
        //speaker = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, samples.length * 2, AudioTrack.MODE_STATIC);
        int minBufSize = AudioRecord.getMinBufferSize(sampleRate2, channelConfig2, audioFormat2);
        speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate2,channelConfig2,audioFormat2,minBufSize,AudioTrack.MODE_STREAM);
        if (speaker.getState() != AudioTrack.STATE_UNINITIALIZED) {
            speaker.write(samples, 0, samples.length);
        }
    }

    private void playSoundClip() {
        if (speaker != null && speaker.getState() != AudioTrack.STATE_UNINITIALIZED) {
            speaker.stop();
            speaker.reloadStaticData();
            speaker.play();
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

    /*
    @Override
    public boolean dispatchKeyEvent(KeyEvent KEvent)
    {
        int keyaction = KEvent.getAction();
        System.out.println("DEBUG MESSAGE KEY=");
        //416
        if(keyaction == KeyEvent.KEYCODE_VOLUME_DOWN && keyaction == KeyEvent.ACTION_DOWN) {
            int keycode = KEvent.getKeyCode();
            int keyunicode = KEvent.getUnicodeChar(KEvent.getMetaState() );
            char character = (char) keyunicode;

            System.out.println("DEBUG MESSAGE KEY=" + character + " KEYCODE=" +  keycode);
        }else if(keyaction == KeyEvent.KEYCODE_VOLUME_DOWN && keyaction == KeyEvent.ACTION_UP){
                int keycode = KEvent.getKeyCode();
                int keyunicode = KEvent.getUnicodeChar(KEvent.getMetaState() );
                char character = (char) keyunicode;
                System.out.println("DEBUG MESSAGE KEY=" + character + " KEYCODE=" +  keycode);
        }
        return super.dispatchKeyEvent(KEvent);
    }
    //*/


    /*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    System.out.println("DEBUG MESSAGE KEY=true");
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:


                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO

                    if(keycode==false) {
                        System.out.println("DEBUG MESSAGE KEY=true");
                        keycode=true;
                    }
                }else  if (action == KeyEvent.ACTION_UP) {
                    System.out.println("DEBUG MESSAGE KEY=false");
                    keycode=false;
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    */
    //*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("DEBUG MESSAGE KEY=down , " + keyCode+" , "+event.getKeyCode());
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //Intent intent = new Intent();
                    //intent.putExtra("link", "true");
                    //intent.setComponent(new ComponentName("litbang.hariff.litbangradio.calling",    "litbang.hariff.litbangradio.calling.Service.PlayService"));
                    //startService(intent);
                    //System.out.println("DEBUG MESSAGE KEY=true");
                }else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_UP) {
                    //System.out.println("DEBUG MESSAGE KEY=false");
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                //*
                if(keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Intent intent = new Intent();
                    intent.putExtra("link", "false");
                    intent.setComponent(new ComponentName("litbang.hariff.litbangradio.calling",    "litbang.hariff.litbangradio.calling.Service.PlayService"));
                    startService(intent);
                }

                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        System.out.println("DEBUG MESSAGE KEY=UP , " + keyCode+" , "+event.getKeyCode());
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //Intent intent = new Intent();
                    //intent.putExtra("link", "true");
                    //intent.setComponent(new ComponentName("litbang.hariff.litbangradio.calling",    "litbang.hariff.litbangradio.calling.Service.PlayService"));
                    //startService(intent);
                    //System.out.println("DEBUG MESSAGE KEY=true");
                }else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_UP) {
                    //System.out.println("DEBUG MESSAGE KEY=false");
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                //*
                if(keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Intent intent = new Intent();
                    intent.putExtra("link", "false");
                    intent.setComponent(new ComponentName("litbang.hariff.litbangradio.calling",    "litbang.hariff.litbangradio.calling.Service.PlayService"));
                    startService(intent);
                }

                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
    //*/
}
