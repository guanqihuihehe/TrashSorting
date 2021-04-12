package com.szu.trashsorting.common.download;

public interface DownloadListenerInterface {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}
