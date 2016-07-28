package org.ollide.rosandroid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by Felix on 2016-07-19.
 */
public class UserSettingDialog extends Dialog {

    private int parsedAngular = 25;
    private int parsedSpeed = 25;

    private SeekBar angularSeekBar;
    private SeekBar speedSeekBar;

    private TextView angularTv;
    private TextView speedTv;

    private Button exitBtn;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        this.setCancelable(false);

        setContentView(R.layout.dialog_setting);

        initComponents();

        angularSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                angularTv.setText(String.valueOf(angularSeekBar.getProgress()));

            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                speedTv.setText(String.valueOf(speedSeekBar.getProgress()));

            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

    }

    public void setWeight(float angularWeight, float speedWeight){

        parsedAngular = (int)((angularWeight - 0.5f) * 100.0f);
        parsedSpeed = (int)((speedWeight - 0.75f) * 100.0f);

        this.angularSeekBar.setProgress(parsedAngular);
        this.speedSeekBar.setProgress(parsedSpeed);

        this.angularTv.setText(String.valueOf(parsedAngular));
        this.speedTv.setText(String.valueOf(parsedSpeed));

    }

    public Vector<Float> getWeight(){

        Vector<Float> result = new Vector<Float>();

        result.add(((float)(this.angularSeekBar.getProgress()) + 50.0f) / 100.0f);
        result.add(((float)(this.speedSeekBar.getProgress()) + 75.0f) / 100.0f);

        return result;

    }

    private void initComponents(){

        exitBtn = (Button)findViewById(R.id.setting_dialog_button);

        exitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                UserSettingDialog.this.dismiss();

            }

        });

        angularSeekBar = (SeekBar)findViewById(R.id.angular_seekbar);
        speedSeekBar = (SeekBar)findViewById(R.id.speed_seekbar);

        angularTv = (TextView)findViewById(R.id.setting_dialog_angular_value);
        speedTv = (TextView)findViewById(R.id.setting_dialog_speed_value);

    }

    public UserSettingDialog(Context context) {

        super(context, android.R.style.Theme_Translucent_NoTitleBar);

    }

}
