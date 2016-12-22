package com.view.media.api;

/**
 * Created by Destiny on 2016/12/20.
 */

public interface NetWorkStateListener {
    void onSuccess();

    void onError();

    void onCancelled();

    void onFinished();
}
