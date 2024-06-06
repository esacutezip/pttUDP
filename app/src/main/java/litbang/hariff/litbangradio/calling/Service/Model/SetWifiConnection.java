package litbang.hariff.litbangradio.calling.Service.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

import static android.content.Context.WIFI_SERVICE;

public class SetWifiConnection {

    //private static String LOG_TAG = "debugdata-LocalDataService-SetWifiConnection";
    private static String LOG_TAG = "debugdata-SetWifi";
    private boolean statusWifiConfiguration;

    public boolean getCurrentWiFiConfiguration(Context context, ConfigWifiModel configWifiModel) {

        WifiConfiguration wifiConf = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Log.d(LOG_TAG, "networkInfo.isConnected:" + networkInfo.isConnected());
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();

            int ipAddress = connectionInfo.getIpAddress();
            String ssidName = connectionInfo.getSSID().replace("\"", "");
            String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
            Log.d(LOG_TAG, "networkInfo:" + ssidName + "-getIpAddress:" + ip +
                    "==DB:" + configWifiModel.getSSIDName() + "-" + configWifiModel.getMyIp());
            if ( (configWifiModel.getSSIDName().equalsIgnoreCase(ssidName)) && (configWifiModel.getMyIp().equalsIgnoreCase(ip)) ){
                statusWifiConfiguration = true;
                Log.d(LOG_TAG, "SetWifi SSID and Ip is the same");
            }else{
                Log.d(LOG_TAG, "SetWifi SSID and Ip is not the same");
                statusWifiConfiguration = false;
                Log.d(LOG_TAG, "SetWifi...");

                boolean setWifi = SET_WIFI(context, configWifiModel);

                Log.d(LOG_TAG, "setWifi status:" + setWifi);
            }
            /*if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
                if (configuredNetworks != null) {
                    for (WifiConfiguration conf : configuredNetworks) {
                        if (conf.networkId == connectionInfo.getNetworkId()) {
                            wifiConf = conf;
                            break;
                        }
                    }
                }
            }*/
        }else{
            //connect wifi
            statusWifiConfiguration = false;
            Log.d(LOG_TAG, "SetWifi...");
            boolean setWifi = SET_WIFI(context, configWifiModel);
            Log.d(LOG_TAG, "setWifi status:" + setWifi);
        }
        return statusWifiConfiguration;
    } //end of WifiConfiguration

    private boolean SET_WIFI(Context context,ConfigWifiModel configWifiModel) {
        // TODO Auto-generated method stub
        try {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            WifiManager wifiManager=(WifiManager)context.getApplicationContext().getSystemService(WIFI_SERVICE);
            wifiConfig.SSID = String.format("\"%s\"", configWifiModel.getSSIDName().toString());
            wifiConfig.preSharedKey = String.format("\"%s\"", configWifiModel.getSSIDPassword().toString());

            setStaticIpConfiguration(wifiManager, wifiConfig,
                    InetAddress.getByName(configWifiModel.getMyIp()),
                    Integer.valueOf(24),
                    InetAddress.getByName(configWifiModel.getMyGateway().toString()),
                    new InetAddress[]{InetAddress.getByName("8.8.8.8"), InetAddress.getByName("8.8.4.4")});

            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            Log.d(LOG_TAG, "bef reconnect");
            boolean reconnect = wifiManager.reconnect();
            Log.d(LOG_TAG, "after reconnect:" + reconnect);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void setStaticIpConfiguration(WifiManager manager, WifiConfiguration config, InetAddress ipAddress, int prefixLength, InetAddress gateway, InetAddress[] dns) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException {
        // First set up IpAssignment to STATIC.
        Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "STATIC");
        callMethod(config, "setIpAssignment", new String[]{"android.net.IpConfiguration$IpAssignment"}, new Object[]{ipAssignment});

        // Then set properties in StaticIpConfiguration.
        Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");
        Object linkAddress = newInstance("android.net.LinkAddress", new Class<?>[]{InetAddress.class, int.class}, new Object[]{ipAddress, prefixLength});

        setField(staticIpConfig, "ipAddress", linkAddress);
        setField(staticIpConfig, "gateway", gateway);
        getField(staticIpConfig, "dnsServers", ArrayList.class).clear();
        for (int i = 0; i < dns.length; i++)
            getField(staticIpConfig, "dnsServers", ArrayList.class).add(dns[i]);

        callMethod(config, "setStaticIpConfiguration", new String[]{"android.net.StaticIpConfiguration"}, new Object[]{staticIpConfig});

        int netId = manager.updateNetwork(config);
        boolean result = netId != -1;
        if (result) {
            //boolean isDisconnected = manager.disconnect();
            boolean configSaved = manager.saveConfiguration();
            boolean isEnabled = manager.enableNetwork(config.networkId, true);
            boolean isReconnected = manager.reconnect();
        }
    }

    private Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        return newInstance(className, new Class<?>[0], new Object[0]);
    }

    private Object newInstance(String className, Class<?>[] parameterClasses, Object[] parameterValues) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        Class<?> clz = Class.forName(className);
        Constructor<?> constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object getEnumValue(String enumClassName, String enumValue) throws ClassNotFoundException {
        Class<Enum> enumClz = (Class<Enum>) Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }

    private void setField(Object object, String fieldName, Object value) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }

    private <T> T getField(Object object, String fieldName, Class<T> type) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        return type.cast(field.get(object));
    }

    private void callMethod(Object object, String methodName, String[] parameterTypes, Object[] parameterValues) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterClasses[i] = Class.forName(parameterTypes[i]);

        Method method = object.getClass().getDeclaredMethod(methodName, parameterClasses);
        method.invoke(object, parameterValues);
    }

}
