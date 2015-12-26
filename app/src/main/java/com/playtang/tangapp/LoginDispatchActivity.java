package com.playtang.tangapp;

import com.playtang.tangapp.login.PlayTangLoginDispatchActivity;

/**
 * Created by 310131737 on 10/16/2015.
 */
public class LoginDispatchActivity extends PlayTangLoginDispatchActivity{
    @Override
    protected Class<?> getTargetClass() {
        return HomeScreen.class;
    }

    @Override
    protected Class<?> getTargetClass(boolean isLogin) {
        if(isLogin)
        return HomeScreen.class;
        else return getTargetClass();
    }
}
