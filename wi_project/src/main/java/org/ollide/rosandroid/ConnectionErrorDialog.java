package org.ollide.rosandroid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Felix on 2016-07-13.
 */
public class ConnectionErrorDialog extends Dialog {

    public static final int UNABLE_TO_REGISTER = 0;
    public static final int DISCONNECTED_AFTER_REGISTERED = 1;

    private Handler uiHandler;

    private TextView uriTv;
    private Button btn;

    private int errorType;

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

    public ConnectionErrorDialog(Context context, Handler uiHandler, int errorType){

        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.uiHandler = uiHandler;
        this.errorType = errorType;

    }

    public void initComponents(){

        uriTv = (TextView)findViewById(R.id.dialog_connection_error_uri);

        btn = (Button)findViewById(R.id.dialog_connection_error_btn);

        TextView errorMsgTv = (TextView)findViewById(R.id.dialog_connection_error_msg);

        btn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                ConnectionErrorDialog.this.dismiss();

            }

        });

        if(errorType == UNABLE_TO_REGISTER){

            errorMsgTv.setText("Unable to make connection");

        }

        if(errorType == DISCONNECTED_AFTER_REGISTERED){

            LinearLayout.LayoutParams tvParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            tvParam.setMargins( 5, 5, 5, 70 );

            errorMsgTv.setLayoutParams(tvParam);
            errorMsgTv.setText("Network disconnected");

            uriTv.setVisibility(View.GONE);

        }

    }

    public void setErrorUri(String errorUri) {

        uriTv.setText(errorUri);

    }

}
