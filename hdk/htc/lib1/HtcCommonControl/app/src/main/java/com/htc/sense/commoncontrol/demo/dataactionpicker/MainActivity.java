package com.htc.sense.commoncontrol.demo.dataactionpicker;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.lib1.cc.widget.dataactionpicker.DataActionPicker;
import com.htc.lib1.cc.widget.dataactionpicker.DataActionPicker.ActionHandler;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;

public class MainActivity extends CommonDemoActivityBase {
    private static final int ACTION_CUSTOM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        initDefaultActionPart(content);
        initCustomActionPart(content);

        setContentView(content);
    }

    private void initDefaultActionPart(ViewGroup content) {
        TextView label = new TextView(this);
        label.setText("DEFAULT ACTIONS FOR EACH DATA TYPE");
        content.addView(label);

        Object[][] items = {
                {DataActionPicker.DataType.PHONE_NUMBER, "Phone number", "0287615959"},
                {DataActionPicker.DataType.EMAIL, "E-mail", "Asia_pr@htc.com"},
                {DataActionPicker.DataType.URL, "URL", "http://htc.com"},
                {DataActionPicker.DataType.ADDRESS, "Postal address", "新北市新店區中興路3段88號"},
                };

        for (Object[] o : items) {
            final Object[] item = o;
            Button btn = new Button(this);
            btn.setText((String)item[1]);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataActionPicker picker = new DataActionPicker(MainActivity.this,
                            (DataActionPicker.DataType)item[0], (String)item[2]);
                    picker.show();
                }
            });
            content.addView(btn);
        }
    }

    private ActionHandler actionHandler = new ActionHandler() {
        @Override
        public boolean onClickAction(int action) {
            if (ACTION_CUSTOM == action) {
                //do something..
                Toast.makeText(MainActivity.this, "This is a custom action", 1000).show();
                return true;
            }
            return super.onClickAction(action);
        }
    };

    private void initCustomActionPart(ViewGroup content) {
        TextView label = new TextView(this);
        label.setText("CUSTOM ACTION");
        content.addView(label);

        Button btn = new Button(this);
        btn.setText("Add a custom action at default position");
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataActionPicker picker = new DataActionPicker(MainActivity.this, DataActionPicker.DataType.PHONE_NUMBER ,"0287615959");
                picker.addAction(ACTION_CUSTOM, "[Custom action]");
                picker.setActionHandler(actionHandler);
                picker.show();
            }
        });
        content.addView(btn);

        btn = new Button(this);
        btn.setText("Add a custom action after 'Call'");
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataActionPicker picker = new DataActionPicker(MainActivity.this, DataActionPicker.DataType.PHONE_NUMBER ,"0287615959");
                picker.addActionAfter(DataActionPicker.ACTION_CALL, ACTION_CUSTOM, "[Custom action]");
                picker.setActionHandler(actionHandler);
                picker.show();
            }
        });
        content.addView(btn);

        btn = new Button(this);
        btn.setText("Add a custom action before 'Copy'");
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataActionPicker picker = new DataActionPicker(MainActivity.this, DataActionPicker.DataType.PHONE_NUMBER ,"0287615959");
                picker.addActionBefore(DataActionPicker.ACTION_COPY, ACTION_CUSTOM, "[Custom action]");
                picker.setActionHandler(actionHandler);
                picker.show();
            }
        });
        content.addView(btn);

        btn = new Button(this);
        btn.setText("Skip 'Send message' action item");
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataActionPicker picker = new DataActionPicker(MainActivity.this, DataActionPicker.DataType.PHONE_NUMBER ,"0287615959");
                picker.setActionHandler(new ActionHandler() {
                    @Override
                    public boolean onCreateAction(int action) {
                        if (DataActionPicker.ACTION_SEND_MESSAGE == action) {
                            return false;
                        }
                        return super.onCreateAction(action);
                    }
                });
                picker.show();
            }
        });
        content.addView(btn);

        btn = new Button(this);
        btn.setText("Handle 'Call' action item");
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DataActionPicker picker = new DataActionPicker(MainActivity.this, DataActionPicker.DataType.PHONE_NUMBER ,"0287615959");
                picker.setActionHandler(new ActionHandler() {
                    @Override
                    public boolean onClickAction(int action) {
                        if (DataActionPicker.ACTION_CALL == action) {
                            Toast.makeText(MainActivity.this, "User click on 'Call'", 1000).show();
                            return true;
                        }
                        return super.onClickAction(action);
                    }
                });
                picker.show();
            }
        });
        content.addView(btn);
    }
}