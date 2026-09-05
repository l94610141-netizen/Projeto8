package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class TelaMenu extends View {
    Paint tinta = new Paint();
    RectF btnJogar, btnPerfil, btnSair;
    GerenciadorJogador jogador;

    public TelaMenu(Context context) {
        super(context);
        jogador = new GerenciadorJogador(context);
        setBackgroundColor(Color.parseColor("#0a0a1a"));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float cx = w/2;
        float cy = h/2;
        
        btnJogar = new RectF(cx - 180, cy - 100, cx + 180, cy - 30);
        btnPerfil = new RectF(cx - 180, cy + 20, cx + 180, cy + 90);
        btnSair = new RectF(cx - 180, cy + 140, cx + 180, cy + 210);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Título
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(70);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("🎱 PROJETO 8", getWidth()/2, 120, tinta);
        
        // Nível
        int nivel = jogador.getNivel();
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setTextSize(35);
        canvas.drawText("⭐ NÍVEL " + nivel, getWidth()/2, 180, tinta);
        
        // Barra de XP
        int progresso = (int)((float)jogador.getXp() / jogador.getXpProximoNivel() * 100);
        float barX = getWidth()/2 - 180;
        float barY = 200;
        float barWidth = 360;
        float barHeight = 25;
        
        tinta.setColor(Color.parseColor("#333366"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(barX, barY, barX + barWidth, barY + barHeight, 15, 15, tinta);
        
        tinta.setColor(Color.parseColor("#2ECC71"));
        canvas.drawRoundRect(barX, barY, barX + (barWidth * progresso / 100), barY + barHeight, 15, 15, tinta);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(18);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(jogador.getXp() + "/" + jogador.getXpProximoNivel() + " XP", 
                        getWidth()/2, barY + 18, tinta);
        
        // Estatísticas rápidas
        tinta.setColor(Color.GRAY);
        tinta.setTextSize(22);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("🎯 " + jogador.getPartidas() + " partidas  🏆 " + jogador.getVitorias() + " vitórias", 
                        getWidth()/2, 260, tinta);
        
        // Linha
        tinta.setColor(Color.parseColor("#333366"));
        tinta.setStrokeWidth(2);
        canvas.drawLine(80, 290, getWidth()-80, 290, tinta);
        
        // Botões
        desenharBotao(canvas, btnJogar, "🏆 JOGAR", true);
        desenharBotao(canvas, btnPerfil, "👤 PERFIL", false);
        desenharBotao(canvas, btnSair, "🚪 SAIR", false);
        
        // Rodapé
        tinta.setTextSize(16);
        tinta.setColor(Color.parseColor("#333355"));
        canvas.drawText("© 2026 Projeto 8 - Android", getWidth()/2, getHeight() - 30, tinta);
    }

    private void desenharBotao(Canvas canvas, RectF rect, String texto, boolean destaque) {
        tinta.setColor(destaque ? Color.parseColor("#FFD700") : Color.parseColor("#2a2a4a"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, 20, 20, tinta);
        
        tinta.setColor(destaque ? Color.parseColor("#FFA500") : Color.parseColor("#444466"));
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(3);
        canvas.drawRoundRect(rect, 20, 20, tinta);
        
        tinta.setColor(destaque ? Color.BLACK : Color.WHITE);
        tinta.setTextSize(32);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(texto, rect.centerX(), rect.centerY() + 12, tinta);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float x = e.getX(), y = e.getY();
            
            if (btnJogar.contains(x, y)) {
                ((MainActivity) getContext()).trocarTela(new TelaMesas(getContext()));
                return true;
            }
            
            if (btnPerfil.contains(x, y)) {
                ((MainActivity) getContext()).trocarTela(new TelaPerfil(getContext()));
                return true;
            }
            
            if (btnSair.contains(x, y)) {
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
            }
        }
        return true;
    }
}
