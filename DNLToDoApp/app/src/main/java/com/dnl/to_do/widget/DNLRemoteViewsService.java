package com.dnl.to_do.widget;


import android.content.Intent;
import android.widget.RemoteViewsService;

public class DNLRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DNLRemoteViewsFactory(this.getApplicationContext());
    }
}
