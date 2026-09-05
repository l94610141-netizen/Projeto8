package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;

public class TelaAbertura extends View {
    Paint tinta = new Paint();
    Handler handler = new Handler();
    float alpha = 0;
    boolean subindo = true;

    public TelaAbertura(Context context) {
        super(context);
        setBackgroundColor(Color.parseColor("#0a0a1a"));
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) getContext()).trocarTela(new TelaMenu(getContext()));
            }
        }, 3000);
        
        handler.post(new Runnable() {
            @Override
            public void run() {
                invalidate();
                if (subindo) {
                    alpha += 0.02f;
                    if (alpha >= 1) subindo = false;
                } else {
                    alpha -= 0.02f;
                    if (alpha <= 0.3f) subindo = true;
                }
                handler.postDelayed(this, 50);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(120);
        tinta.setTextAlign(Paint.Align.CENTER);
        tinta.setAlpha((int)(alpha * 255));
        canvas.drawText("🎱 PROJETO 8", getWidth()/2, getHeight()/2 - 80, tinta);
        
        tinta.setTextSize(80);
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setAlpha((int)(alpha * 200));
        canvas.drawText("POOL", getWidth()/2, getHeight()/2 + 60, tinta);
        
        tinta.setTextSize(30);
        tinta.setColor(Color.GRAY);
        tinta.setAlpha(150);
        canvas.drawText("★ Chicago Edition ★", getWidth()/2, getHeight()/2 + 150, tinta);
        
        tinta.setTextSize(20);
        tinta.setAlpha(100);
        canvas.drawText("v1.0", getWidth()/2, getHeight() - 50, tinta);
    }
}
