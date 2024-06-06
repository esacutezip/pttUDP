package litbang.hariff.litbangradio.calling.Service.Model;

import java.net.DatagramPacket;

public class ModelReceivePacket {
    public byte[] getPacket() {
        return packet;
    }

    public void setPacket(byte[] packet) {
        this.packet = packet;
    }

    byte[] packet;

    public int getPacketlenght() {
        return packetlenght;
    }

    public void setPacketlenght(int packetlenght) {
        this.packetlenght = packetlenght;
    }

    int packetlenght;

}
