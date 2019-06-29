package com.lixd.costom.view.comment.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * 解决安卓EditText多行输入时，设置imeOptions失效问题解决
 * 参考文章:https://www.jianshu.com/p/d35e494f78f7
 */
public class ImeOptionsEditText extends android.support.v7.widget.AppCompatEditText {
    public ImeOptionsEditText(Context context) {
        super(context);
    }

    public ImeOptionsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImeOptionsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        if (inputConnection != null) {
            outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        return inputConnection;
    }
}
