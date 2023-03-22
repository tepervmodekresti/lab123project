package com.example.lab1project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1project.databinding.ActivityMainBinding;
import com.google.android.filament.View;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.DecoderException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex;

import java.text.DecimalFormat;

//import org.apache.commons.codec.DecoderException;

interface TransactionEvents {
    String enterPin(int ptc, String amount);
    void transactionResult(boolean result);
}

public class MainActivity extends AppCompatActivity implements TransactionEvents {
    private String pin;
    ActivityResultLauncher activityResultLauncher;
    static {
        System.loadLibrary("lab1project");
        System.loadLibrary("mbedcrypto");
    }
    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }

    public static native byte[] encrypt(byte[] key, byte[] array);
    public static native byte[] decrypt(byte[] key, byte[] array);
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    // Used to load the 'lab1project' library on application startup.
    static {
        System.loadLibrary("lab1project");
        System.loadLibrary("mbedcrypto");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.lab1project.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int res = initRng();
        byte [] v = randomBytes(16);
        byte [] s = randomBytes(30);
        byte [] e = encrypt(v, s);
        byte [] de = decrypt(v, e);

        // Example of a call to a native method
        activityResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback() {
                    @Override
                    public void onActivityResult(Object result) {
                        if (((ActivityResult) result).getResultCode() == Activity.RESULT_OK) {
                            Intent data = ((ActivityResult) result).getData();
                            // обработка результата
                            //String pin = data.getStringExtra("pin");
                            //Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this){
                                MainActivity.this.notify();
                            }
                        }
                    }
                });

    }

    public native String stringFromJNI();
    public static byte[] stringToHex(String s) {
        byte[] hex;
        try { hex = Hex.decodeHex(s.toCharArray()); }
        catch (DecoderException ex) { hex = null; }
        return hex;
    }


    public void onButtonClick(android.view.View view) {
//        Intent it = new Intent(this, PinpadActivity.class);
//        startActivity(it);
//        activityResultLauncher.launch(it);
        new Thread(()-> {
            try {
                byte[] trd = stringToHex("9F0206000000000100");
                boolean ok = transaction(trd);
            } catch (Exception ex) {
                // todo: log error
            }
        }).start();

    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }
    public native boolean transaction(byte[] trd);
}

