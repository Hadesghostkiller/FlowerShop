package com.example.flowershop.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TypewriterTextView extends AppCompatTextView {

    private CharSequence text;
    private int index = 0;
    private long delay = 30; // Delay between each character in milliseconds
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable characterRunnable;

    public TypewriterTextView(Context context) {
        super(context);
    }

    public TypewriterTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TypewriterTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTypewriterText(CharSequence text) {
        this.text = text;
        index = 0;
        
        // Cancel any existing animation
        if (characterRunnable != null) {
            handler.removeCallbacks(characterRunnable);
        }
        
        setText("");
        
        characterRunnable = new Runnable() {
            @Override
            public void run() {
                if (index <= text.length()) {
                    setText(text.subSequence(0, index));
                    index++;
                    handler.postDelayed(this, delay);
                }
            }
        };
        
        handler.post(characterRunnable);
    }

    public void setTypewriterText(CharSequence text, long customDelay) {
        this.delay = customDelay;
        setTypewriterText(text);
    }

    public void stopAnimation() {
        if (characterRunnable != null) {
            handler.removeCallbacks(characterRunnable);
        }
        if (text != null && index < text.length()) {
            setText(text);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }
}