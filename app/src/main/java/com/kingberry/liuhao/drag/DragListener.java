package com.kingberry.liuhao.drag;

import android.view.View;

/**
 * Created by Administrator on 2017/7/17.
 */

public interface DragListener {

    void onDragStarted(View source);
    void onDropCompleted(View source, View target, boolean success);

}
