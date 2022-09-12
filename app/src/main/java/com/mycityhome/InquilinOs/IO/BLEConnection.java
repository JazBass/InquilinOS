package com.mycityhome.InquilinOs.IO;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BLEConnection {

    Activity mainActivity;

    public BLEConnection(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    int requestCodePermission;
    private static final String TAG = "Main Activity";


    private static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID NOTIFY_CHARACTERISTIC = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID WRITE_CHARACTER = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    BluetoothGattCharacteristic characteristicNotify;
    BluetoothGattCharacteristic characteristicWrite;
    BluetoothGatt myGatt;

    private boolean scanningEnd, send = false;
    String code;

    ServerHttp myHttpServer;
    HttpExchange httpExchange;

    /*--------------------------BLE--------------------------*/
    private boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void scanLeDevice() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        /*--------------------Bluetooth----------------------*/
        BluetoothLeScanner bluetoothLeScanner = btAdapter.getBluetoothLeScanner();
        byte[] setServiceData = new byte[]{
                0x11
        };
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter.Builder builder = new ScanFilter.Builder()
                .setServiceData(new ParcelUuid(SERVICE_UUID), setServiceData);
        filters.add(builder.build());
        ScanSettings setting = new ScanSettings.Builder().setCallbackType(CALLBACK_TYPE_ALL_MATCHES).build();
        //TODO: Filters for device's search
        if (!scanningEnd) {
            if (ActivityCompat.checkSelfPermission(mainActivity,
                    Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            }
            bluetoothLeScanner.startScan(leScannerCallback);
            Log.i(TAG, "scanLeDevice: start scan");
        } else {
            bluetoothLeScanner.stopScan(leScannerCallback);
            Log.i(TAG, "scanLeDevice: stop scan");
        }
    }


    private final ScanCallback leScannerCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!scanningEnd) {
                if (ActivityCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                }
                Log.i(TAG, "onScanResult: " + result.getDevice().getAddress() +
                        result.getDevice().getName());
                if (Objects.equals(result.getDevice().getName(), "WeLockAWPOR")) {
                    Log.i(TAG, "onScanResult: FOUNDED");
                    scanningEnd = true;
                    result.getDevice().connectGatt(mainActivity,
                            true, mGattCallback);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i(TAG, "onScanFailed: fail");
            super.onScanFailed(errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (ActivityCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                }
                gatt.discoverServices();
                myGatt = gatt;
                Log.i(TAG, "onConnectionStateChange: Discover Services");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            characteristicNotify = gatt.getService(SERVICE_UUID).getCharacteristic(NOTIFY_CHARACTERISTIC);
            if (ActivityCompat.checkSelfPermission(mainActivity,
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            }
            gatt.setCharacteristicNotification(characteristicNotify, true);
            BluetoothGattDescriptor desc = characteristicNotify.getDescriptor(CCCD_UUID);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);

        }

        public byte[] hexStringToByteArray(String s) {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
            return data;
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (characteristicWrite == characteristic) {
                Log.i(TAG, "Char: " + characteristic + " status: " + status);
                if (characteristic == characteristicWrite) {
                    characteristic.getValue();
                }
            } else Log.i(TAG, "ERROR: Write is not ok");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getValue()[0] == 85 & characteristic.getValue()[1] == 48) {
                int rndNumber = characteristic.getValue()[2] & 0xff;
                int battery = characteristic.getValue()[3] & 0xff;
                String myJason = "{\"rndNumber\":" + rndNumber + ", \"battery\":" + battery + "}";
                try {
                    myHttpServer.sendResponse(httpExchange, myJason);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    myHttpServer.sendResponse(httpExchange, "aaaa");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "onCharacteristicChanged: Received");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (descriptor.getCharacteristic() == characteristicNotify) {
                sendBLE(gatt);
            } else Log.i(TAG, "onDescriptorWrite: Descriptor is not connected");
        }

        /*---------------------write char---------------------*/

        public void sendBLE(BluetoothGatt gatt) {
            characteristicWrite = gatt.getService(SERVICE_UUID).
                    getCharacteristic(WRITE_CHARACTER);
            byte[] newValue = hexStringToByteArray(code);
            characteristicWrite.setValue(newValue);
            characteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
            }
            if (send) {
                Log.i(TAG, "Sending...");
                gatt.writeCharacteristic(characteristicWrite);
                send = false;
            }
        }
    };

    public void sendBleRequest(String message, HttpExchange httpExchange) {
        scanningEnd = false;
        send = true;
        code = message;
        this.httpExchange = httpExchange;
        if (!hasPermissions(mainActivity, permissions)) {
            mainActivity.requestPermissions(permissions, requestCodePermission);
        } else {
            scanLeDevice();
        }
    }
}
