package com.money.loan.net;

import com.meituan.android.walle.WalleChannelReader;
import com.money.loan.MyApp;
import com.money.loan.R;

/**
 * - @Author:  闫世豪
 * - @Time:  2018/5/23 下午4:22
 * - @Email whynightcode@gmail.com
 */
public class Params {

    public static String getAppName(){
       return MyApp.getApplication().getResources().getString(R.string.appName);
    }

    public static String getChannel(){
        return  WalleChannelReader.getChannel(MyApp.getApplication());

    }
}
