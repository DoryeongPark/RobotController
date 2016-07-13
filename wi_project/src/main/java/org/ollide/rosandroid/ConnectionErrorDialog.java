package org.ollide.rosandroid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.ros.android.RosActivity;

/**
 * Created by Felix on 2016-07-13.
 */
public class ConnectionErrorDialog extends Dialog {

    private Context context;

    private TextView urlTv;
    private Button btn;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        this.setCancelable(false);

        setContentView(R.layout.dialog_connection_error);

        initComponents();

    }

    public ConnectionErrorDialog(Context context){

        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.context = context;

    }

    public void initComponents(){

        urlTv = (TextView)findViewById(R.id.dialog_connection_error_url);
        btn = (Button)findViewById(R.id.dialog_connection_error_btn);

        btn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                ConnectionErrorDialog.this.dismiss();

                ((MainActivity)context).posDisconnection();

            }

        });

    }

    public void setErrorUri(String errorUri) {

        urlTv.setText(errorUri);

    }

}
