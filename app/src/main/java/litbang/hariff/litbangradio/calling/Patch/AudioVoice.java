package litbang.hariff.litbangradio.calling.Patch;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import litbang.hariff.litbangradio.calling.MainActivity;
import litbang.hariff.litbangradio.calling.Service.DataService;
import litbang.hariff.litbangradio.calling.Service.DataServiceSound;


public class AudioVoice {
    public static Context ctx;
    public static AudioManager am;
    String TAG = "AUDIOVOICE";
    public static boolean bluethooth = false , isreceived = true , isvoicetrue = true , play = false ,phone = false;
    public static DatagramSocket socketdatasend ,socketvoicesend , socketdatareceive , socketdatavoicereceive;
    public static String IpDataSenderTo = "192.168.0.1";
    public static int portIpDataSenderTo = 50004;
    public static String IpvoiceSenderTo = "192.168.0.1";
    public static int portIpVoiceSenderTo = 50007;
    public static String IpDataReceive = "192.168.0.1";
    public static int portIpDataReceive = 50006;
    public static String IpvoiceReceive = "192.168.0.1";
    public static int portIpVoiceReceive = 50005;
    public static AudioTrack speakerbig;
    public static AudioTrack speakersmall;
    public static int FramePlay = 20 ,plays = 0;

    Thread ThreadReadData , receiveThread;
    AudioThread mAudioThread;
    ReceiveDataThread readReceivethread;
    ReadVoiceThread readvoice;
    static final int SAMPLE_RATE = 8000;
    static final int FRAME_SIZE = 160;
    static final int NUM_CHANNELS = 1;
    ArrayList<byte[]> jitter;
    public AudioVoice(final Context ctx) {
        this.ctx = ctx;
        am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        am.setParameters("noise_suppression=auto");
        am.setMode(AudioManager.MODE_IN_CALL);
        ctx.registerReceiver(new BroadcastReceiver() {

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
                    ctx.unregisterReceiver(this);
                }

            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
        am.startBluetoothSco();
    }

    public void init() {
        jitter = new ArrayList<>();
        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,  AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        speakerbig = new AudioTrack(AudioManager.STREAM_MUSIC,SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,minBufSize,AudioTrack.MODE_STREAM);
        speakersmall = new AudioTrack(AudioManager.STREAM_VOICE_CALL,SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,minBufSize,AudioTrack.MODE_STREAM);
        speakerbig.play();
        speakersmall.play();

    }

    public void sendVoice(Activity activity){
        byte[] data = new byte[2];
        data[0] = 0x01;
        data[1] = 0x01;
        StartService(data);
        startvoice(activity);
    }

    public void stopVoice(){
        stopvoice();
        byte[] data = new byte[2];
        data[0] = 0x01;
        data[1] = 0x02;
        StartService(data);
    }

    private void stopvoice() {

        if(mAudioThread!=null) {
            mAudioThread.interrupt();
            try {
                mAudioThread.join();
            } catch (InterruptedException e) {
                Log.w(TAG, "Interrupted waiting for audio thread to finish");
            }
        }
    }

    public void StartService(byte[] data){
        try {
            socketdatasend = new DatagramSocket();
            InetAddress destination = InetAddress.getByName(IpDataSenderTo);
            socketdatasend.connect(destination, portIpDataSenderTo);
            Log.d(TAG , "SOCKETT : "+IpDataSenderTo +" , "+portIpDataSenderTo);
            DatagramPacket packet;
            packet = new DatagramPacket (data,data.length,destination,portIpDataSenderTo);
            Log.d(TAG , "SOCKETT : "+socketdatasend.isConnected() +" , "+packet);
            if(socketdatasend.isConnected()&&packet!=null) {
                socketdatasend.send(packet);
                socketdatasend.close();
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

    public void SetBluethooth(boolean bt){
        this.bluethooth = bt;
        refreshbt();
    }

    public void refreshbt(){
        if(this.bluethooth==false){
            am.setSpeakerphoneOn(false);
            am.setBluetoothScoOn(true);
        }else if (this.bluethooth==true){
            am.setSpeakerphoneOn(true);
            am.setBluetoothScoOn(false);
        }
    }

    private void startvoice(Activity activity) {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    1);
            return;
        }
        Log.d(TAG , "playSend");
        mAudioThread = new AudioThread();
        mAudioThread.start();
    }

    private class AudioThread extends Thread {
        // Sample rate must be one supported by Opus.
        //static final int SAMPLE_RATE = 8000;
        // Number of samples per frame is not arbitrary,
        // it must match one of the predefined values, specified in the standard.
        //static final int FRAME_SIZE = 960;
        // 1 or 2
        //static final int NUM_CHANNELS = 1;
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
                socketvoicesend = new DatagramSocket();
                DatagramPacket packet;
                final InetAddress destination = InetAddress.getByName(IpvoiceSenderTo);
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

                    packet = new DatagramPacket(inBuf, inBuf.length, destination, portIpVoiceSenderTo);
                    socketvoicesend.send(packet);
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


    public void StartReceivingData(){

        readReceivethread = new ReceiveDataThread();
        readReceivethread.execute("");


    }

    public class ReceiveDataThread extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            ThreadReadData = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress serverAddr = InetAddress.getByName(IpDataReceive);
                        socketdatareceive = new DatagramSocket(portIpDataReceive, serverAddr);
                        socketdatareceive.setReuseAddress(true);
                        byte[] buffer = new byte[2];
                        Log.d(TAG, "SOCKET $ :" + IpDataReceive + " , " + portIpDataReceive + " , " + isreceived);
                        while (isreceived == true) {
                            try {
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                                if (packet != null) {
                                    if (socketdatareceive.isClosed()) {
                                    } else {
                                        socketdatareceive.receive(packet);
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
                                                isvoicetrue = true;
                                                jitter = new ArrayList<>();
                                                if (jitter.size() != 0) {
                                                    jitter.clear();
                                                }
                                                plays = 0;
                                                phone = false;
                                                startReceiving();
                                             } else if (buffer[1] == 0x02) {
                                                isvoicetrue = false;
                                                phone = false;
                                                if (readvoice != null) {
                                                    readvoice.cancel(true);
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
            ThreadReadData.start();

            return "";
        }


        @Override
        protected void onPostExecute (String resultString){
            super.onPostExecute(resultString);

        }

    }

    public void startReceiving() {

        readvoice = new ReadVoiceThread();
        readvoice.execute("");

    }

    public class ReadVoiceThread extends AsyncTask<String, Void, String> {
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
                        InetAddress serverAddr = InetAddress.getByName(IpvoiceReceive);
                        socketdatavoicereceive = new DatagramSocket(portIpVoiceReceive , serverAddr);
                        socketdatavoicereceive.setReuseAddress(true);
                        Log.d("VR", "Socket Created " + MainActivity.myip+" , "+portIpVoiceReceive+ " , "+isvoicetrue);
                        //minimum buffer size. need to be careful. might cause problems. try setting manually if any problems faced
                        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                        byte[] buffer = new byte[minBufSize];

                        //OpusDecoder decoder = new OpusDecoder();
                        //decoder.init(SAMPLE_RATE, NUM_CHANNELS);
                        while(isvoicetrue == true) {
                            Log.d("VR", "Socket RECEIVE"+isvoicetrue);
                            try {
                                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);

                                socketdatavoicereceive.receive(packet);

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

                                //Log.d(TAG, "jitter : " + jitter.size());
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
                                e.printStackTrace();
                            }
                        }
                    } catch (SocketException e) {
                        Log.e("VR", "SocketException");
                    } catch (IOException e) {
                        Log.e("VR", "IOException2");
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
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                while(play==true) {
                    if(plays<=jitter.size()-1) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String currentDateandTime = sdf.format(new Date());
                        Log.d(TAG, "plays " + String.valueOf(plays) + " , jitter : " + jitter.size()+" , DATE : "+currentDateandTime);
                        if(jitter.size()!=0) {
                            if (phone == false) {
                                speakerbig.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                            } else {
                                speakersmall.write(jitter.get(plays), 0, jitter.get(plays).length * NUM_CHANNELS);
                            }
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
