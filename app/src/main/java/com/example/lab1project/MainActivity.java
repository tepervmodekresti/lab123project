package com.example.lab1project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.lab1project.databinding.ActivityMainBinding;

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

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int res = initRng();
        byte [] v = randomBytes(16);
        byte [] s = randomBytes(30);
        byte [] e = encrypt(v, s);
        byte [] de = decrypt(v, e);
        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'lab1project' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
