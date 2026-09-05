package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class TelaPerfil extends View {
    private Paint tinta = new Paint();
    private GerenciadorJogador jogador;
    private RectF btnVoltar;
    
    private float avatarX, avatarY, avatarRaio = 80;

    public TelaPerfil(Context context) {
        super(context);
        jogador = new GerenciadorJogador(context);
        setBackgroundColor(Color.parseColor("#0d0d1a"));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        btnVoltar = new RectF(40, 40, 180, 90);
        avatarX = w / 2f;
        avatarY = 200;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int nivel = jogador.getNivel();
        int xp = jogador.getXp();
        int xpMax = jogador.getXpProximoNivel();
        int partidas = jogador.getPartidas();
        int vitorias = jogador.getVitorias();
        int dolares = jogador.getDolares();
        int diamantes = jogador.getDiamantes();
        float progresso = (float)xp / xpMax;
        
        // Título
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(50);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("👤 MEU PERFIL", getWidth()/2, 90, tinta);
        
        // Avatar
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawCircle(avatarX, avatarY, avatarRaio, tinta);
        tinta.setColor(Color.WHITE);
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(5);
        canvas.drawCircle(avatarX, avatarY, avatarRaio, tinta);
        
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.FILL);
        tinta.setTextSize(70);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(jogador.getAvatarEmoji(), avatarX, avatarY + 25, tinta);
        
        // Nível
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setTextSize(30);
        canvas.drawText("⭐ NÍVEL " + nivel, getWidth()/2, avatarY + avatarRaio + 50, tinta);
        
        // Moedas
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(28);
        canvas.drawText("💵 " + dolares + "  💎 " + diamantes, getWidth()/2, avatarY + avatarRaio + 90, tinta);
        
        // Barra XP
        float barX = 80, barY = avatarY + avatarRaio + 120;
        float barWidth = getWidth() - 160, barHeight = 30;
        tinta.setColor(Color.parseColor("#2a2a4a"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(barX, barY, barX + barWidth, barY + barHeight, 20, 20, tinta);
        tinta.setColor(Color.parseColor("#2ECC71"));
        canvas.drawRoundRect(barX, barY, barX + (barWidth * progresso), barY + barHeight, 20, 20, tinta);
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(20);
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
        
        // Dica avatar
        tinta.setColor(Color.GRAY);
        tinta.setTextSize(20);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("👆 Clique no avatar para trocar", getWidth()/2, getHeight() - 60, tinta);
        
        // Voltar
        tinta.setColor(Color.parseColor("#333366"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(btnVoltar, 15, 15, tinta);
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(28);
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
            
            // Clique no avatar → trocar emoji
            float dx = x - avatarX;
            float dy = y - avatarY;
            if (Math.sqrt(dx*dx + dy*dy) < avatarRaio) {
                jogador.proximoAvatar();
                invalidate();
                return true;
            }
        }
        return true;
    }
}
