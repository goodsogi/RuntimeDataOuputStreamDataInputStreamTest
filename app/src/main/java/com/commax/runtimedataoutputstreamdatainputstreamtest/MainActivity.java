package com.commax.runtimedataoutputstreamdatainputstreamtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "id: " + readAccountFile(), Toast.LENGTH_SHORT).show();
    }

    public String readAccountFile() {

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        String id = null;
        try {
            //루팅이 안되었으면 오류 발생
            process = Runtime.getRuntime().exec("su");
            process = Runtime.getRuntime().exec("cat /mnt/sdcard/CMXdata/CreateAccount.properties\n");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());

            //원래 권한인 crw------- 으로 돌림
            //dataOutputStream.writeBytes("chmod 666 /mnt/sdcard/CMXdata/CreateAccount.properties\n");
            //dataOutputStream.writeBytes("cat /mnt/sdcard/CMXdata/CreateAccount.properties\n");
            dataOutputStream.writeBytes("exit\n");


            String responseString = null;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while(( responseString =bufferedReader.readLine()) != null)
            {

                String[] tokens = responseString.split("=");
                if (tokens[0].contains("id")) {

                    id = tokens[1];
                    break;

                }

            }

            dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();
            process.waitFor();

        } catch (Exception e) {
            Log.d(LOG_TAG, "권한 설정 오류: " +  e.getMessage());

        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                Log.d(LOG_TAG, "권한 설정 오류: " +  e.getMessage());
            }
        }
        return id;
    }
}
