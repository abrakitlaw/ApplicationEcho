package com.thesis.application.echo.post_module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesis.application.echo.R;

/**
 * Created by Abra Kitlaw on 10-Jul-18.
 */

public class TakePhotoPost extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_take_photo, container, false);
        
    }
}
