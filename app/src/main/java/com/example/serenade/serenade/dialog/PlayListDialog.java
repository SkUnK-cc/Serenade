package com.example.serenade.serenade.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.serenade.serenade.utils.system.SystemUtil;

/**
 * Created by Serenade on 2017/8/1.
 */

public class PlayListDialog extends BottomSheetDialog {
    public PlayListDialog(@NonNull Context context) {
        super(context);
    }

    public PlayListDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    protected PlayListDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        FrameLayout bottomSheet = (FrameLayout) view.getParent();
        ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
        int height = (int) (SystemUtil.getScreenHeight(getContext())*3/5);
        params.height = height;
    }
}
