package com.kingberry.liuhao.drag;

import android.view.View;

/**
 * Created by Administrator on 2017/7/17.
 */

public interface DragSource {
    void setDragController(DragController dragger);
    void onDropCompleted(View target, boolean success);
    boolean isDelete();
}