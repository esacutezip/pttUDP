package litbang.hariff.litbangradio.calling.Service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import litbang.hariff.litbangradio.calling.MainActivity;

public class KirimData {
    Context ctx;
    public static java.util.concurrent.ConcurrentLinkedQueue<Pair<String, Object>> QueueSendMessage = new java.util.concurrent.ConcurrentLinkedQueue<>();
    Handler HandlerQueue = new Handler();
    Handler handlerqueue2 = new Handler();
    DatagramSocket socket;
    public KirimData(Context ctx) {
    this.ctx = ctx;
        handlerqueue2.postDelayed(runnablequeue, 0);
    }

    Runnable runnablequeue = new Runnable() {
        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            while (!QueueSendMessage.isEmpty()) {
                Pair<String, Object> data = QueueSendMessage.poll();
                if (data != null) {
                    switch (data.first) {
                        case "SendQueue":
                            if (data.second != null) {
                                ModelPacket datas = (ModelPacket) data.second;
                                KirimPacket(datas);
                            }
                            break;
                    }
                }
            }
            handlerqueue2.postDelayed(this, 0);
        }
    };

    private void KirimPacket(ModelPacket packet) {
        try {
            socket = new DatagramSocket();
                socket.send(packet.getData());
            socket.close();
                StringBuilder sb = new StringBuilder();
                String byteInput;
                for (int i = 0; i < packet.getData().getLength(); i++) {
                    byteInput = String.format(" %02x", packet.getData().getData()[i]);
                    sb.append(byteInput.trim());
                    sb.append(" ");
                }
                Log.d("TAG", "SEND :" + packet.getData().getLength() + ":" + sb.toString());
                sb.delete(0, sb.length());
        } catch(UnknownHostException e) {
            Log.e("VS", "UnknownHostException");
        } catch (IOException e) {
            Log.e("VS", "IOException");
        }
    }


    public void SendPacket(DatagramPacket packet , int lenght) {
        //Log.d("TAG", "KIRIM ::");
        ModelPacket packets = new ModelPacket();
        packets.setData(packet);
        QueueSendMessage.add(Pair.create("SendQueue", (Object) packets));
    }

    public void SendOff() {
        handlerqueue2.removeCallbacks(runnablequeue);
    }
}
