package litbang.hariff.litbangradio.calling.Service;

import android.util.Log;
import android.util.Pair;

import litbang.hariff.litbangradio.calling.MainActivity;
import litbang.hariff.litbangradio.calling.Modul.AckImageSender;
import litbang.hariff.litbangradio.calling.Modul.Datareceiver;
import litbang.hariff.litbangradio.calling.Modul.ImageSender;

public class DataParser {
	private static final String LOG = "DataParser-Service";
	long arraysize  = 0,arraynow  = 0,sizeimage  = 0 , address = 0;
	int counttitle  = 0,countlocation  = 0;
	String location , image;

	public Datareceiver parseSbcSentence(byte[] buffer) throws SecurityException {
		String locationSentence = null;

		Datareceiver data = new Datareceiver();
		data.setI1(buffer[0]);
		data.setI2(buffer[1]);

		return data;
	}

}
