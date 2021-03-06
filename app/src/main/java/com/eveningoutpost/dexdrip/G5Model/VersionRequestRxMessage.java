package com.eveningoutpost.dexdrip.G5Model;

import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.Models.UserError;
import com.eveningoutpost.dexdrip.Services.G5CollectionService;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

/**
 * Created by jamorham on 25/11/2016.
 */


public class VersionRequestRxMessage extends TransmitterMessage {

    private final static String TAG = G5CollectionService.TAG; // meh

    public static final byte opcode = 0x4B;

    public int status;
    public String firmware_version_string;
    public String bluetooth_firmware_version_string;
    public int hardwarev;
    public String other_firmware_version;
    public int asic;


    public VersionRequestRxMessage(byte[] packet) {
        UserError.Log.e(TAG, "VersionRX dbg: " + JoH.bytesToHex(packet));
        if (packet.length >= 18) {
            // TODO check CRC??
            data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
            if (data.get() == opcode) {
                status = data.get();
                firmware_version_string = dottedStringFromData(data, 4);
                bluetooth_firmware_version_string = dottedStringFromData(data, 4);
                hardwarev = data.get();
                other_firmware_version = dottedStringFromData(data, 3);
                asic = ((data.get() & 0xff) + ((data.get() & 0xff) << 8)); // check signed vs unsigned & byte order!!
            }
        }
    }

    public String toString() {
        return String.format(Locale.US, "Status: %s / Firmware: %s / BT-Firmware: %s / Other-FW: %s / hardwareV: %d / asic: %d",
                TransmitterStatus.getBatteryLevel(status).toString(), firmware_version_string, bluetooth_firmware_version_string, other_firmware_version, hardwarev, asic);
    }

    private static String dottedStringFromData(ByteBuffer data, int length) {

        final byte[] bytes = new byte[length];
        data.get(bytes);
        final StringBuilder sb = new StringBuilder(100);
        for (byte x : bytes) {
            if (sb.length() > 0) sb.append(".");
            sb.append(String.format(Locale.US, "%d", (x & 0xff)));
        }
        return sb.toString();
    }
}