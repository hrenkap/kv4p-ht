package com.vagell.kv4pht.firmware;

import android.content.Context;
import android.util.Log;

import timber.log.Timber;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.vagell.kv4pht.R;
import com.vagell.kv4pht.firmware.bearconsole.CommandInterfaceESP32;
import com.vagell.kv4pht.firmware.bearconsole.UploadSTM32CallBack;
import com.vagell.kv4pht.firmware.bearconsole.UploadSTM32Errors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FirmwareUtils {
    private static boolean isFlashing = false;
    private static int progressPercent = 0;

    // Whenever there is new firmware, put the files in res/raw, and update these constants.
    public static final int PACKAGED_FIRMWARE_VER = 13;
    private static final int FIRMWARE_FILE_1_ID = R.raw.bootloader;
    private static final int FIRMWARE_FILE_2_ID = R.raw.partitions;
    private static final int FIRMWARE_FILE_3_ID = R.raw.boot_app0; // This one never changes, it's the Arduino ESP32 bootloader
    private static final int FIRMWARE_FILE_4_ID = R.raw.firmware_v13;

    public FirmwareUtils() {
    }

    public static boolean isFlashing() {
        return isFlashing;
    }

    public interface FirmwareCallback {
        public void connectedToBootloader();
        public void reportProgress(int percent);
        public void doneFlashing(boolean success);
    }

    public static void flashFirmware(Context ctx, UsbSerialPort usbSerialPort, FirmwareCallback callback) {
        if (isFlashing) {
            Timber.tag("DEBUG").d("Warning: Attempted to call FirmwareUtils.flashFirmware() while already flashing.");
            return;
        }
        isFlashing = true;
        boolean failed = false;
        InputStream firmwareFile1 = null;
        InputStream firmwareFile2 = null;
        InputStream firmwareFile3 = null;
        InputStream firmwareFile4 = null;
        CommandInterfaceESP32 cmd;

        UploadSTM32CallBack UpCallback = new UploadSTM32CallBack() {
            @Override
            public void onPreUpload() {
                Timber.tag("DEBUG").d("onPreUpload");
            }

            @Override
            public void onUploading(int value) {
                Timber.tag("DEBUG").d("onUploading: " + value);

                // Some of the file writes take a while, show finer-grained progress for those.
                if (progressPercent >= 20 && progressPercent < 30) {
                    int newPercent = Math.min(50, (int) (20 + (10 * ((float) value / 100.0f))));
                    trackProgress(callback, newPercent);
                }
                else if (progressPercent >= 50) {
                    int newPercent = Math.min(100, (int) (50 + (50 * ((float) value / 100.0f))));
                    trackProgress(callback, newPercent);
                }
            }

            @Override
            public void onInfo(String value) {
                Timber.tag("DEBUG").d("onInfo: " + value);
            }

            @Override
            public void onPostUpload(boolean success) {
                Timber.tag("DEBUG").d("onPostUpload: " + success);
            }

            @Override
            public void onCancel() {
                Timber.tag("DEBUG").d("onCancel");
            }

            @Override
            public void onError(UploadSTM32Errors err) {
                Timber.tag("DEBUG").d("onError: " + err);
            }
        };
        cmd = new CommandInterfaceESP32(ctx, UpCallback, usbSerialPort);

        firmwareFile1 = ctx.getResources().openRawResource(FIRMWARE_FILE_1_ID);
        firmwareFile2 = ctx.getResources().openRawResource(FIRMWARE_FILE_2_ID);
        firmwareFile3 = ctx.getResources().openRawResource(FIRMWARE_FILE_3_ID);
        firmwareFile4 = ctx.getResources().openRawResource(FIRMWARE_FILE_4_ID);

        Timber.tag("DEBUG").d("Attempting to init ESP32 for firmware flash");

        boolean ret = cmd.initChip();
        if (!ret) {
            Timber.tag("DEBUG").d("ESP32 failed to init, return value: " + ret);
            failed = true;
        }
        if (!failed) {
            callback.connectedToBootloader();
            // cmd.loadStubFromFile(); // Does not work
            // cmd.changeBaudRate(); // Faster baud can't work without stub loader
            trackProgress(callback, 10);
            cmd.init();
            trackProgress(callback, 20);

            Timber.tag("DEBUG").d("Flashing firmware");
            cmd.flashData(readFirmwareBytes(firmwareFile1), 0x1000, 0);
            trackProgress(callback, 30);
            cmd.flashData(readFirmwareBytes(firmwareFile2), 0x8000, 0);
            trackProgress(callback, 40);
            cmd.flashData(readFirmwareBytes(firmwareFile3), 0xe000, 0);
            trackProgress(callback, 50);
            cmd.flashData(readFirmwareBytes(firmwareFile4), 0x10000, 0);

            // we have finished flashing, reboot ESP32
            cmd.reset();

            Timber.tag("DEBUG").d("Done flashing firmware");
        }
        callback.doneFlashing(!failed);
        progressPercent = 0;
        isFlashing = false;
    }

    private static void trackProgress(FirmwareCallback callback, int newProgressPercent) {
        progressPercent = newProgressPercent;
        callback.reportProgress(progressPercent);
    }

    private static byte[] readFirmwareBytes(InputStream stream) {
        Timber.tag("DEBUG").d("Reading firmware binary file");
        ByteArrayOutputStream byteArrayOutputStream = null;
        int i;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            i = stream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = stream.read();
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }
}
