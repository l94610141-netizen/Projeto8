package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    Paint tinta = new Paint();
    GerenciadorJogador jogador;
    String nomeCidade;
    
    class Bola {
        float x, y, raio, vx, vy;
        int cor;
        boolean branca;
        
        Bola(float x, float y, float raio, int cor, boolean branca) {
            this.x = x;
            this.y = y;
            this.raio = raio;
            this.cor = cor;
            this.branca = branca;
            this.vx = 0;
            this.vy = 0;
        }
        
        void atualizar() {
            x += vx;
            y += vy;
            vx *= 0.985f;
            vy *= 0.985f;
            if (Math.abs(vx) < 0.01f) vx = 0;
            if (Math.abs(vy) < 0.01f) vy = 0;
        }
        
        void desenhar(Canvas canvas, Paint paint) {
            paint.setColor(cor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, raio, paint);
            if (branca) {
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                canvas.drawCircle(x, y, raio, paint);
            }
        }
        
        boolean colidiu(Bola outra) {
            float dx = x - outra.x;
            float dy = y - outra.y;
            return Math.sqrt(dx*dx + dy*dy) < raio + outra.raio;
        }
    }
    
    ArrayList<Bola> bolas = new ArrayList<>();
    Bola bolaBranca;
    int bolasAfundadas = 0;
    boolean jogoFinalizado = false;
    boolean venceu = false;
    float startX = -1, startY = -1;
    boolean arrastando = false;
    
    // Caçapas
    float[][] cacapas = {
        {70, 70},        // Canto superior esquerdo
        {0, 0},          // Será calculado
        {0, 0},          // Será calculado
        {70, 0},         // Canto inferior esquerdo
        {0, 0},          // Será calculado
        {0, 0}           // Será calculado
    };
    float raioCacapa = 30;

    public GameView(Context context, String cidade) {
        super(context);
        this.nomeCidade = cidade;
        this.jogador = new GerenciadorJogador(context);
        iniciarJogo();
    }
    
    private void iniciarJogo() {
        bolas.clear();
        bolasAfundadas = 0;
        jogoFinalizado = false;
        venceu = false;
        
        bolaBranca = new Bola(200, getHeight()/2, 12, Color.WHITE, true);
        bolas.add(bolaBranca);
        
        float cx = getWidth() - 200;
        float cy = getHeight()/2;
        float[][] pos = {
            {cx, cy},
            {cx + 30, cy - 25}, {cx + 30, cy + 25},
            {cx + 60, cy - 50}, {cx + 60, cy}, {cx + 60, cy + 50},
            {cx + 90, cy - 75}, {cx + 90, cy - 25}, {cx + 90, cy + 25}, {cx + 90, cy + 75},
            {cx + 120, cy - 100}, {cx + 120, cy - 50}, {cx + 120, cy}, {cx + 120, cy + 50}, {cx + 120, cy + 100}
        };
        int[] cores = {Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA, 
                       Color.CYAN, Color.GREEN, Color.GRAY, Color.BLACK,
                       Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA,
                       Color.CYAN, Color.GREEN, Color.GRAY};
        
        for (int i = 0; i < 15; i++) {
            bolas.add(new Bola(pos[i][0], pos[i][1], 11, cores[i], false));
        }
        
        // Calcular posições das caçapas
        float margem = 50;
        float w = getWidth();
        float h = getHeight();
        cacapas[1] = new float[]{w/2, margem};
        cacapas[2] = new float[]{w - margem, margem};
        cacapas[4] = new float[]{w/2, h - margem};
        cacapas[5] = new float[]{w - margem, h - margem};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#0a0a1a"));
        
        // Cabeçalho
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setTextSize(20);
        tinta.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("⭐ Nível " + jogador.getNivel(), 20, 30, tinta);
        
        tinta.setColor(Color.WHITE);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("🏙️ " + nomeCidade, getWidth()/2, 30, tinta);
        
        tinta.setColor(Color.GRAY);
        tinta.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("🎯 " + bolasAfundadas + "/15", getWidth() - 20, 30, tinta);
        
        // Mesa
        float margem = 50;
        tinta.setColor(Color.parseColor("#35654d"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRect(margem, 50, getWidth() - margem, getHeight() - 50, tinta);
        
        // Bordas
        tinta.setColor(Color.parseColor("#8B4513"));
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(10);
        canvas.drawRect(margem, 50, getWidth() - margem, getHeight() - 50, tinta);
        
        // Caçapas (buracos)
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.FILL);
        for (float[] c : cacapas) {
            canvas.drawCircle(c[0], c[1], raioCacapa, tinta);
        }
        
        // Atualizar física
        if (!jogoFinalizado) {
            atualizarFisica();
        }
        
        // Desenhar bolas
        for (Bola b : bolas) {
            b.desenhar(canvas, tinta);
        }
        
        // Mira
        if (arrastando && startX != -1) {
            tinta.setColor(Color.argb(150, 255, 255, 255));
            tinta.setStrokeWidth(2);
            canvas.drawLine(bolaBranca.x, bolaBranca.y, startX, startY, tinta);
            
            // Potência
            float dx = startX - bolaBranca.x;
            float dy = startY - bolaBranca.y;
            float dist = (float) Math.sqrt(dx*dx + dy*dy);
            float potencia = Math.min(dist / 15, 15);
            
            tinta.setColor(Color.parseColor("#333366"));
            tinta.setStyle(Paint.Style.FILL);
            canvas.drawRect(50, getHeight() - 40, 250, getHeight() - 15, tinta);
            
            tinta.setColor(potencia > 10 ? Color.RED : potencia > 5 ? Color.YELLOW : Color.GREEN);
            canvas.drawRect(50, getHeight() - 40, 50 + potencia * 13, getHeight() - 15, tinta);
        }
        
        // Fim de jogo
        if (jogoFinalizado) {
            tinta.setColor(Color.argb(200, 0, 0, 0));
            tinta.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, getWidth(), getHeight(), tinta);
            
            tinta.setColor(venceu ? Color.parseColor("#FFD700") : Color.RED);
            tinta.setTextSize(60);
            tinta.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(venceu ? "🏆 VITÓRIA!" : "💔 DERROTA", 
                           getWidth()/2, getHeight()/2 - 50, tinta);
            
            tinta.setColor(Color.WHITE);
            tinta.setTextSize(25);
            int xpGanho = venceu ? 100 : 20;
            canvas.drawText("⭐ +" + xpGanho + " XP", getWidth()/2, getHeight()/2 + 20, tinta);
            
            tinta.setColor(Color.parseColor("#2ECC71"));
            tinta.setStyle(Paint.Style.FILL);
            float bx = getWidth()/2 - 120;
            float by = getHeight()/2 + 80;
            canvas.drawRoundRect(bx, by, bx + 240, by + 60, 20, 20, tinta);
            tinta.setColor(Color.BLACK);
            tinta.setTextSize(25);
            canvas.drawText("🔄 JOGAR NOVAMENTE", getWidth()/2, by + 40, tinta);
            
            // Botão menu
            tinta.setColor(Color.parseColor("#333366"));
            tinta.setStyle(Paint.Style.FILL);
            by = getHeight()/2 + 160;
            canvas.drawRoundRect(bx, by, bx + 240, by + 60, 20, 20, tinta);
            tinta.setColor(Color.WHITE);
            canvas.drawText("🏠 MENU", getWidth()/2, by + 40, tinta);
        }
        
        postInvalidateDelayed(16);
    }
    
    private void atualizarFisica() {
        float margem = 50;
        float w = getWidth();
        float h = getHeight();
        
        for (Bola b : bolas) {
            b.atualizar();
            
            // Colisão com bordas
            if (b.x - b.raio < margem) { b.x = margem + b.raio; b.vx = -b.vx * 0.8f; }
            if (b.x + b.raio > w - margem) { b.x = w - margem - b.raio; b.vx = -b.vx * 0.8f; }
            if (b.y - b.raio < 50) { b.y = 50 + b.raio; b.vy = -b.vy * 0.8f; }
            if (b.y + b.raio > h - 50) { b.y = h - 50 - b.raio; b.vy = -b.vy * 0.8f; }
            
            // Verificar caçapas
            for (float[] c : cacapas) {
                float dx = b.x - c[0];
                float dy = b.y - c[1];
                if (Math.sqrt(dx*dx + dy*dy) < raioCacapa) {
                    if (b.branca) {
                        b.x = 200;
                        b.y = h/2;
                        b.vx = 0;
                        b.vy = 0;
                    } else {
                        bolas.remove(b);
                        bolasAfundadas++;
                        if (bolasAfundadas >= 15) {
                            jogoFinalizado = true;
                            venceu = true;
                            jogador.addXp(100);
                            jogador.registrarPartida(true);
                        }
                        return;
                    }
                }
            }
        }
        
        // Colisões entre bolas
        for (int i = 0; i < bolas.size(); i++) {
            for (int j = i+1; j < bolas.size(); j++) {
                Bola a = bolas.get(i);
                Bola b = bolas.get(j);
                if (a.colidiu(b)) {
                    float dx = a.x - b.x;
                    float dy = a.y - b.y;
                    float dist = (float) Math.sqrt(dx*dx + dy*dy);
                    if (dist == 0) continue;
                    
                    float overlap = (a.raio + b.raio - dist) / 2;
                    a.x += (dx/dist) * overlap;
                    a.y += (dy/dist) * overlap;
                    b.x -= (dx/dist) * overlap;
                    b.y -= (dy/dist) * overlap;
                    
                    float dvx = a.vx - b.vx;
                    float dvy = a.vy - b.vy;
                    float dvn = dvx * (dx/dist) + dvy * (dy/dist);
                    if (dvn > 0) continue;
                    
                    float impulso = 2 * dvn / 2;
                    a.vx -= impulso * (dx/dist);
                    a.vy -= impulso * (dy/dist);
                    b.vx += impulso * (dx/dist);
                    b.vy += impulso * (dy/dist);
                }
            }
        }
        
        // Verificar derrota
        boolean todasParadas = true;
        for (Bola b : bolas) {
            if (b.vx != 0 || b.vy != 0) {
                todasParadas = false;
                break;
            }
        }
        if (todasParadas && bolas.size() == 1 && bolas.get(0).branca && bolasAfundadas < 15) {
            jogoFinalizado = true;
            venceu = false;
            jogador.addXp(20);
            jogador.registrarPartida(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (jogoFinalizado) {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                float x = e.getX(), y = e.getY();
                float bx = getWidth()/2 - 120;
                float by = getHeight()/2 + 80;
                
                // Botão "Jogar Novamente"
                if (x > bx && x < bx + 240 && y > by && y < by + 60) {
                    iniciarJogo();
                    invalidate();
                    return true;
                }
                
                // Botão "Menu"
                by = getHeight()/2 + 160;
                if (x > bx && x < bx + 240 && y > by && y < by + 60) {
                    ((MainActivity) getContext()).trocarTela(new TelaMenu(getContext()));
                    return true;
                }
            }
            return true;
        }
        
        float x = e.getX();
        float y = e.getY();
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float dx = x - bolaBranca.x;
                float dy = y - bolaBranca.y;
                if (Math.sqrt(dx*dx + dy*dy) < 50 && bolaBranca.vx == 0 && bolaBranca.vy == 0) {
                    arrastando = true;
                    startX = x;
                    startY = y;
                }
                return true;
                
            case MotionEvent.ACTION_MOVE:
                if (arrastando) {
                    startX = x;
                    startY = y;
                    invalidate();
                }
                return true;
                
            case MotionEvent.ACTION_UP:
                if (arrastando) {
                    float dx2 = startX - bolaBranca.x;
                    float dy2 = startY - bolaBranca.y;
                    float dist = (float) Math.sqrt(dx2*dx2 + dy2*dy2);
                    if (dist > 20) {
                        float potencia = Math.min(dist / 15, 15);
                        bolaBranca.vx = (dx2 / dist) * potencia;
                        bolaBranca.vy = (dy2 / dist) * potencia;
                    }
                    arrastando = false;
                    startX = -1;
                    startY = -1;
                }
                return true;
        }
        return true;
    }
}
