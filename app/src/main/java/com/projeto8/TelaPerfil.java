package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class TelaPerfil extends View {
    Paint tinta = new Paint();
    GerenciadorJogador jogador;
    RectF btnVoltar;
    String[] emojis = {"🎱", "🏆", "⭐", "🎯", "🔥", "💪", "👑", "🌟"};
    int avatarIndex = 0;
    
    public TelaPerfil(Context context) {
        super(context);
        jogador = new GerenciadorJogador(context);
        setBackgroundColor(Color.parseColor("#0d0d1a"));
        avatarIndex = (int)(System.currentTimeMillis() % emojis.length);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        btnVoltar = new RectF(40, 40, 180, 90);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int nivel = jogador.getNivel();
        int xp = jogador.getXp();
        int xpMax = jogador.getXpProximoNivel();
        int partidas = jogador.getPartidas();
        int vitorias = jogador.getVitorias();
        float progresso = (float)xp / xpMax * 100;
        
        // Título
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(50);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("👤 MEU PERFIL", getWidth()/2, 90, tinta);
        
        // Avatar grande
        float cx = getWidth()/2;
        float cy = 200;
        float raio = 80;
        
        // Círculo do avatar
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, raio, tinta);
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(5);
        canvas.drawCircle(cx, cy, raio, tinta);
        
        // Emoji do avatar
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.FILL);
        tinta.setTextSize(70);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(emojis[avatarIndex], cx, cy + 25, tinta);
        
        // Nível abaixo do avatar
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setTextSize(30);
        canvas.drawText("⭐ NÍVEL " + nivel, cx, cy + raio + 45, tinta);
        
        // Barra de XP
        float barX = 80;
        float barY = cy + raio + 70;
        float barWidth = getWidth() - 160;
        float barHeight = 30;
        
        tinta.setColor(Color.parseColor("#2a2a4a"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(barX, barY, barX + barWidth, barY + barHeight, 20, 20, tinta);
        
        tinta.setColor(Color.parseColor("#2ECC71"));
        canvas.drawRoundRect(barX, barY, barX + (barWidth * progresso / 100), barY + barHeight, 20, 20, tinta);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(20);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(xp + " / " + xpMax + " XP", barX + barWidth/2, barY + 22, tinta);
        
        // Estatísticas
        float statsY = barY + barHeight + 50;
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setTextSize(30);
        tinta.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("📊 ESTATÍSTICAS", 60, statsY, tinta);
        
        statsY += 50;
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(26);
        canvas.drawText("🎯 Partidas: " + partidas, 80, statsY, tinta);
        statsY += 45;
        canvas.drawText("🏆 Vitórias: " + vitorias, 80, statsY, tinta);
        statsY += 45;
        float taxa = partidas > 0 ? (vitorias * 100 / partidas) : 0;
        canvas.drawText("📈 Taxa de vitória: " + (int)taxa + "%", 80, statsY, tinta);
        
        // Botão voltar
        tinta.setColor(Color.parseColor("#333366"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(btnVoltar, 15, 15, tinta);
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(28);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("◀ VOLTAR", btnVoltar.centerX(), btnVoltar.centerY() + 10, tinta);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float x = e.getX(), y = e.getY();
            
            if (btnVoltar.contains(x, y)) {
                ((MainActivity) getContext()).trocarTela(new TelaMenu(getContext()));
                return true;
            }
            
            // Clique no avatar - troca o emoji (preparado para futuras imagens)
            float cx = getWidth()/2;
            float cy = 200;
            float raio = 80;
            float dx = x - cx;
            float dy = y - cy;
            if (Math.sqrt(dx*dx + dy*dy) < raio) {
                avatarIndex = (avatarIndex + 1) % emojis.length;
                invalidate();
                return true;
            }
        }
        return true;
    }
}
