package litbang.hariff.litbangradio.calling.Modul;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import litbang.hariff.litbangradio.calling.MainActivity;

public class Sender {
    Context ctx;
    Handler handlerqueue3 = new Handler();
    Handler handlerqueue3s = new Handler();
    DatagramSocket clientSocket2;
    String TAG = "Sender";
    public static java.util.concurrent.ConcurrentLinkedQueue<Pair<String, Object>> QueueSend = new java.util.concurrent.ConcurrentLinkedQueue<>();
    InetAddress serverAddr = null;
    public static byte[] abIn = new byte[2048];
    public Sender(Context mctx) {
        this.ctx = mctx;
        handlerqueue3.postDelayed(HandlerQueue3, 100);
    }
    public void Packet(String pointer,  Kirim kirim) {

        if(pointer.equalsIgnoreCase("IMAGEINIT")){
            //oid
            ImageSender image = kirim.getImageSender();
            int count = 0;
            abIn[count++] = (byte) 0x01;
            abIn[count++] = (byte) 0xff;
            abIn[count++] = (byte) (Long.valueOf(image.getArraysize()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 24) & 0xff);

            abIn[count++] = (byte) (Long.valueOf(image.getSizeimage()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSizeimage()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSizeimage()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSizeimage()) >> 24) & 0xff);

            abIn[count++] = (byte) (Long.valueOf(image.getArraynow()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 24) & 0xff);

            byte[] title = image.getTitle().getBytes();
            int lenghtmessage = abIn[count++] = (byte) (image.getTitle().length() & 0xff);
            for (int i = 0; i < lenghtmessage; i++) {
                abIn[count++] = title[i];
            }
            byte[] location = image.getLocation().getBytes();
            int lenghtmessagelocation = abIn[count++] = (byte) (image.getLocation().length() & 0xff);
            for (int i = 0; i < lenghtmessagelocation; i++) {
                abIn[count++] = location[i];
            }

            int v = 0;
            abIn[count++] = (byte) (Long.valueOf(image.getData().length) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getData().length) >> 8) & 0xff);
            //int lenghtmessagelocation = abIn[count++] = (byte) (image.getLocation().length() & 0xff);
            for(int x = 0 ;x<image.getData().length;x++){
                abIn[count++] = image.getData()[x];
                v+=1;
            }
            int crc = crc16(abIn);
            //crc
            abIn[count++] = (byte) (crc & 0xff);
            abIn[count++] = (byte) ((crc >> 8) & 0xff);

            byte[] bytesend = new byte[count];
            for(int i = 0 ; i<count;i++){
                bytesend[i] = abIn[i];
            }
            ModelKirim kirimmodel = new ModelKirim();
            kirimmodel.setDatabinary(bytesend);
            kirimmodel.setPort(MainActivity.portimage);
            QueueSend.add(Pair.create("SendQueue", (Object) kirimmodel));

        }else if(pointer.equalsIgnoreCase("IMAGE")) {
            ImageSender image = kirim.getImageSender();
            int count = 0;
            abIn[count++] = (byte) 0x02;
            abIn[count++] = (byte) 0xff;
            abIn[count++] = (byte) (Long.valueOf(image.getArraysize()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 24) & 0xff);

            abIn[count++] = (byte) (Long.valueOf(image.getSizeimage()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSizeimage()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSizeimage()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSizeimage()) >> 24) & 0xff);

            abIn[count++] = (byte) (Long.valueOf(image.getArraynow()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 24) & 0xff);


            int v = 0;
            abIn[count++] = (byte) (Long.valueOf(image.getData().length) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getData().length) >> 8) & 0xff);
            //int lenghtmessagelocation = abIn[count++] = (byte) (image.getLocation().length() & 0xff);
            for(int x = 0 ;x<image.getData().length;x++){
                abIn[count++] = image.getData()[x];
                v+=1;
            }
            int crc = crc16(abIn);
            //crc
            abIn[count++] = (byte) (crc & 0xff);
            abIn[count++] = (byte) ((crc >> 8) & 0xff);

            byte[] bytesend = new byte[count];
            for(int i = 0 ; i<count;i++){
                bytesend[i] = abIn[i];
            }
            ModelKirim kirimmodel = new ModelKirim();
            kirimmodel.setDatabinary(bytesend);
            kirimmodel.setPort(MainActivity.portimage);
            QueueSend.add(Pair.create("SendQueue", (Object) kirimmodel));
        }else if(pointer.equalsIgnoreCase("IMAGEACK")) {
            AckImageSender image = kirim.getAckImageSender();
            int count = 0;
            abIn[count++] = (byte) 0x00;
            abIn[count++] = (byte) 0xff;
            abIn[count++] = (byte) (Long.valueOf(image.getArraysize()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraysize()) >> 24) & 0xff);

            abIn[count++] = (byte) (Long.valueOf(image.getArraynow()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getArraynow()) >> 24) & 0xff);

            abIn[count++] = (byte) (Long.valueOf(image.getSize()) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSize()) >> 8) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSize()) >> 16) & 0xff);
            abIn[count++] = (byte) ((Long.valueOf(image.getSize()) >> 24) & 0xff);



            byte[] bytesend = new byte[count];
            for(int i = 0 ; i<count;i++){
                bytesend[i] = abIn[i];
            }
            ModelKirim kirimmodel = new ModelKirim();
            kirimmodel.setDatabinary(bytesend);
            kirimmodel.setPort(MainActivity.portimage);
            QueueSend.add(Pair.create("SendQueue", (Object) kirimmodel));
        }
    }

    Runnable HandlerQueue3 = new Runnable() {
        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            while (!QueueSend.isEmpty()) {
                Pair<String, Object> data = QueueSend.poll();
                if (data != null) {
                    switch (data.first) {
                        case "SendQueue":
                            if (data.second != null) {
                                ModelKirim datas = (ModelKirim) data.second;
                                kirimbinaryBMS(ctx, datas.getDatabinary(),datas.getPort());
                            }
                            break;
                    }
                }
            }
            handlerqueue3s.postDelayed(this, Integer.parseInt("100"));

        }
    };

    public void kirimbinaryBMS(Context ctx, byte[] data , int Port) {
        if (data != null) {
            try {

                String byteInput;
                StringBuilder sb = new StringBuilder();
                Log.d(TAG, "Send Port " + String.valueOf(Port));
                for (int i = 0; i < data.length; i++) {
                    byteInput = String.format(" %02x", data[i]);
                    sb.append(byteInput.trim()); sb.append(" ");
                }
                Log.d(TAG, "Packet :" + sb.toString());
                sb.delete(0,sb.length());
                Log.d(TAG, "IP : " + MainActivity.Ipsender);
                clientSocket2 = new DatagramSocket();
                Log.d(TAG, "Packet lenght: " + data.length);

                try {
                    serverAddr = InetAddress.getByName(MainActivity.Ipsender);
                 DatagramPacket sendPacket2 = new DatagramPacket (data,data.length,serverAddr,Port);
                    clientSocket2.send(sendPacket2);
                    try {
                        if (clientSocket2.isConnected() && sendPacket2 != null) {
                            clientSocket2.send(sendPacket2);
                        }
                    } catch (IOException e) {
                        clientSocket2.close();
                        Log.e(TAG, "Error during sending Packet", e);
                    }
                    clientSocket2.close();
                } catch (UnknownHostException e1) {
                    Log.e(TAG, "error while getting IP server", e1);
                }

            } catch (SocketException e1) {
                Log.e(TAG, "error while making Datagram", e1);
                // e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static int crc16(final byte[] buffer) {
        int crc = 0xFFFF;

        for (int j = 0; j < buffer.length ; j++) {
            crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
            crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;

    }


}
