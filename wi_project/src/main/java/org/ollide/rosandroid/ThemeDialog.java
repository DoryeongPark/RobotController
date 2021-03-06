package org.ollide.rosandroid;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Vector;

/**
 * Created by Felix on 2016-06-29.
 */
public class ThemeDialog extends Dialog {

    private Context context;
    private Vector<Integer> imageSet;

    private Theme ca;
    private ThemeTypes ccs;

    private Button exitBtn;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_theme);

        initGridView();

        exitBtn = (Button)findViewById(R.id.dialog_btn_exit);

        exitBtn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                ThemeDialog.this.dismiss();

            }

        });

    }

    public ThemeDialog(Context context, Theme ca, Vector<Integer> imgSet) {

        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.imageSet = imgSet;
        this.context = context;

        this.ca = ca;
        ccs = new ThemeTypes();

    }

    private void initGridView(){

        GridView imgGridView = (GridView)findViewById(R.id.dialog_gridview);
        ImageGridAdapter iga = new ImageGridAdapter(context, imageSet);
        imgGridView.setAdapter(iga);

        imgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ca.setCurrentTheme(ccs.at(position));
                ca.adjustCurrentTheme();

            }
        });

    }


    public class ImageGridAdapter extends BaseAdapter {

        private Context context;
        private Vector<Integer> imageSet;

        public ImageGridAdapter(Context context, Vector<Integer> imageSet){

            this.context = context;
            this.imageSet = imageSet;

        }

        public int getCount(){
            return imageSet.size();
        }

        public Object getItem(int position){
            return imageSet.elementAt(position);
        }

        public long getItemId(int position){
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent){

            ImageView iv;

            if(null != convertView){

                iv = (ImageView)convertView;

            }else{

                Bitmap bmp = BitmapFactory.decodeResource(
                        context.getResources(), imageSet.elementAt(position));

                bmp = Bitmap.createScaledBitmap(bmp, 350, 220, false);

                iv = new ImageView(context);
                iv.setAdjustViewBounds(true);
                iv.setImageBitmap(bmp);

            }

            return iv;

        }

    }

}

