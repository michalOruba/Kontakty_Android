package pl.michaloruba.kontakty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static String[] PERMISSIONS_CONTACTS = {
            Manifest.permission.READ_CONTACTS
    };
    private static final int REQUEST_CONTACTS_READ = 1;



    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            verifyContactsPermissions(MainActivity.this);
        }
        else {
            WebView webview = findViewById(R.id.webView1);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new CustomJavaScriptInterface(this), "HtmlAndroidBridge");

            webview.loadUrl("file:///android_asset/my_local_page1.html");
        }
    }

    public static void verifyContactsPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CONTACTS,
                    REQUEST_CONTACTS_READ
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_READ) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        //readContacts();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_READ);
                    }
                }
            }
        }
    }
}
