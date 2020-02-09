package bsdq.bsdq.ble;

import java.util.HashMap;

public class SampleGattAttributes {
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_MEASUREMENT2 = "0000ffe2-0000-1000-8000-00805f9b34fb";
    private static HashMap<String, String> attributes = new HashMap<>();

    static {
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put(DeviceUUID.SLIC_BLE_READ_SERVICE_DEVICE_INFO_UUID, "Device Information Service");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(DeviceUUID.SLIC_BLE_READ_CHARACTERISTICS_DEVICE_INFO_MANUFACTURER_NAME_UUID, "Manufacturer Name String");
    }

    public static String lookup(String str, String str2) {
        String str3 = (String) attributes.get(str);
        return str3 == null ? str2 : str3;
    }
}
