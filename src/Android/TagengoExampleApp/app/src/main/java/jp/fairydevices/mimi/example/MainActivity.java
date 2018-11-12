package jp.fairydevices.mimi.example;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ai.fd.mimi.prism.ClientComCtrlExcepiton;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button srButton;
    private EditText srOutput;
    private EditText mtOutput;

    private boolean isRecording = false;
    private PrismClient prismClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(this);
        initView();

        prismClient = new PrismClient(srOutput, mtOutput);
        prismClient.updateToken(); //予めアクセストークンを取得しておく
    }

    private void initView() {
        srButton = findViewById(R.id.srButton);
        Button mtButton = findViewById(R.id.mtButton);
        Button ssButton = findViewById(R.id.ssButton);
        srOutput = findViewById(R.id.srOutputText);
        mtOutput = findViewById(R.id.mtOutputText);
        srButton.setOnClickListener(this);
        mtButton.setOnClickListener(this);
        ssButton.setOnClickListener(this);
    }

    private void checkPermission(Activity activity) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET};
        for (String p : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, p);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // Android 6.0 のみ、該当パーミッションが許可されていない場合
                ActivityCompat.requestPermissions(activity, new String[]{p}, 1);
            } else {
                // 許可済みの場合、もしくはAndroid 6.0以前
                // パーミッションが必要な処理
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.srButton:
                if (isRecording) {
                    // 録音(認識)中
                    prismClient.SRInputEnd();
                    isRecording = false;
                    srButton.setText(R.string.sr_button_off);
                } else {
                    // 待機中
                    prismClient.SRInputStart();
                    isRecording = true;
                    srButton.setText(R.string.sr_button_on);
                }
                break;
            case R.id.mtButton:
                prismClient.MT();
                break;
            case R.id.ssButton:
                prismClient.SS();
                break;
            default:
        }
    }
}
