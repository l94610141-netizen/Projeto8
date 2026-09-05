package com.meujogo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class TelaMesas extends View {
    Paint tinta = new Paint();
    GerenciadorJogador jogador;
    RectF btnVoltar;
    
    class Mesa {
        String nome, cidade, descricao;
        int cor, nivelRequerido;
        RectF rect;
        
        Mesa(String nome, String cidade, String descricao, int cor, int nivelRequerido) {
            this.nome = nome;
            this.cidade = cidade;
            this.descricao = descricao;
            this.cor = cor;
            this.nivelRequerido = nivelRequerido;
        }
    }
    
    Mesa[] mesas;

    public TelaMesas(Context context) {
        super(context);
        jogador = new GerenciadorJogador(context);
        setBackgroundColor(Color.parseColor("#0d0d1a"));
        
        mesas = new Mesa[] {
            new Mesa("The Windy City", "Chicago", "🌆 Skyline moderna", 
                    Color.parseColor("#FF6B35"), 1),
            new Mesa("Big Apple", "Nova York", "🗽 Cidade que nunca dorme", 
                    Color.parseColor("#2ECC71"), 3),
            new Mesa("Sin City", "Las Vegas", "🎰 Luzes e cassinos", 
                    Color.parseColor("#E74C3C"), 5),
            new Mesa("City of Angels", "Los Angeles", "🌴 Sol e Hollywood", 
                    Color.parseColor("#3498DB"), 7)
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        float cardWidth = w - 80;
        float cardHeight = 90;
        float margin = 15;
        int nivelAtual = jogador.getNivel();
        
        for (int i = 0; i < mesas.length; i++) {
            float top = 150 + i * (cardHeight + margin);
            mesas[i].rect = new RectF(40, top, 40 + cardWidth, top + cardHeight);
        }
        
        btnVoltar = new RectF(40, 40, 180, 90);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(35);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("🏙️ ESCOLHA SUA MESA", getWidth()/2, 90, tinta);
        
        tinta.setTextSize(20);
        tinta.setColor(Color.parseColor("#FFD700"));
        canvas.drawText("Nível " + jogador.getNivel(), getWidth()/2, 120, tinta);
        
        for (Mesa m : mesas) {
            boolean desbloqueada = jogador.getNivel() >= m.nivelRequerido;
            
            tinta.setColor(desbloqueada ? Color.parseColor("#1a1a3e") : Color.parseColor("#111122"));
            tinta.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(m.rect, 25, 25, tinta);
            
            tinta.setColor(desbloqueada ? m.cor : Color.parseColor("#444466"));
            tinta.setStyle(Paint.Style.STROKE);
            tinta.setStrokeWidth(desbloqueada ? 4 : 2);
            canvas.drawRoundRect(m.rect, 25, 25, tinta);
            
            if (!desbloqueada) {
                tinta.setColor(Color.argb(180, 0, 0, 0));
                tinta.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(m.rect, 25, 25, tinta);
            }
            
            tinta.setTextSize(30);
            tinta.setTextAlign(Paint.Align.LEFT);
            tinta.setColor(desbloqueada ? Color.WHITE : Color.GRAY);
            canvas.drawText(m.cidade, m.rect.left + 30, m.rect.centerY() - 5, tinta);
            
            tinta.setTextSize(20);
            tinta.setColor(desbloqueada ? Color.parseColor("#FFD700") : Color.parseColor("#666688"));
            canvas.drawText(m.nome, m.rect.left + 30, m.rect.centerY() + 30, tinta);
            
            if (!desbloqueada) {
                tinta.setColor(Color.RED);
                tinta.setTextSize(18);
                tinta.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("🔒 Nível " + m.nivelRequerido, m.rect.right - 20, m.rect.centerY() + 8, tinta);
            }
        }
        
        tinta.setColor(Color.parseColor("#333366"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(btnVoltar, 15, 15, tinta);
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(25);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("◀ VOLTAR", btnVoltar.centerX(), btnVoltar.centerY() + 8, tinta);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float x = e.getX(), y = e.getY();
            
            if (btnVoltar.contains(x, y)) {
                ((MainActivity) getContext()).trocarTela(new TelaMenu(getContext()));
                return true;
            }
            
            for (Mesa m : mesas) {
                if (m.rect.contains(x, y)) {
                    if (jogador.getNivel() >= m.nivelRequerido) {
                        android.widget.Toast.makeText(getContext(), 
                            "🎱 Jogando em " + m.cidade + "!", 
                            android.widget.Toast.LENGTH_SHORT).show();
                        ((MainActivity) getContext()).trocarTela(
                            new GameView(getContext(), m.cidade));
                    } else {
                        android.widget.Toast.makeText(getContext(), 
                            "🔒 Nível " + m.nivelRequerido + " necessário!", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            }
        }
        return true;
    }
}

