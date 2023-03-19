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
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex;

//import org.apache.commons.codec.DecoderException;

public class MainActivity extends AppCompatActivity {
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
        TextView tv = binding.sampleButton;
        tv.setText(stringFromJNI());
        activityResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback() {
                    @Override
                    public void onActivityResult(Object result) {
                        if (((ActivityResult) result).getResultCode() == Activity.RESULT_OK) {
                            Intent data = ((ActivityResult) result).getData();
                            // обработка результата
                            String pin = data.getStringExtra("pin");
                            Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public native String stringFromJNI();
    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (
                com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.DecoderException e) {
            throw new RuntimeException(e);
        }
        return hex;
    }


    public void onButtonClick(android.view.View view) {
        Intent it = new Intent(this, PinpadActivity.class);
        //startActivity(it);
        activityResultLauncher.launch(it);
    }
    ActivityResultLauncher activityResultLauncher;

}

