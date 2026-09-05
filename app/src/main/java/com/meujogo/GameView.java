package com.meujogo;

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
    
    // Bolas
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
            vx *= 0.98f;
            vy *= 0.98f;
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
    
    // Controles
    float startX = -1, startY = -1;
    boolean arrastando = false;

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
        
        // Bola branca
        bolaBranca = new Bola(200, 500, 12, Color.WHITE, true);
        bolas.add(bolaBranca);
        
        // Formação triangular
        Random rand = new Random();
        float[][] pos = {
            {550, 500},
            {580, 470}, {580, 530},
            {610, 440}, {610, 500}, {610, 560},
            {640, 410}, {640, 470}, {640, 530}, {640, 590},
            {670, 380}, {670, 440}, {670, 500}, {670, 560}, {670, 620}
        };
        int[] cores = {Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA, 
                       Color.CYAN, Color.GREEN, Color.GRAY, Color.BLACK,
                       Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA,
                       Color.CYAN, Color.GREEN, Color.GRAY};
        
        for (int i = 0; i < 15; i++) {
            bolas.add(new Bola(pos[i][0], pos[i][1], 11, cores[i], false));
        }
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
        
        // Mesa (simplificada)
        float margem = 50;
        tinta.setColor(Color.parseColor("#35654d"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRect(margem, 50, getWidth() - margem, getHeight() - 50, tinta);
        
        // Bordas
        tinta.setColor(Color.parseColor("#8B4513"));
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(10);
        canvas.drawRect(margem, 50, getWidth() - margem, getHeight() - 50, tinta);
        
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
            tinta.setTextAlign(Paint.Align.CENTER);
            int xpGanho = venceu ? 100 : 20;
            canvas.drawText("⭐ +" + xpGanho + " XP", getWidth()/2, getHeight()/2 + 20, tinta);
            
            tinta.setColor(Color.parseColor("#2ECC71"));
            tinta.setStyle(Paint.Style.FILL);
            float bx = getWidth()/2 - 100;
            float by = getHeight()/2 + 80;
            canvas.drawRoundRect(bx, by, bx + 200, by + 60, 20, 20, tinta);
            tinta.setColor(Color.BLACK);
            tinta.setTextSize(25);
            canvas.drawText("🔄 JOGAR", getWidth()/2, by + 40, tinta);
        }
        
        postInvalidateDelayed(16);
    }
    
    private void atualizarFisica() {
        // Movimento
        for (Bola b : bolas) {
            b.atualizar();
            
            // Colisão com bordas
            float margem = 50;
            if (b.x - b.raio < margem) {
                b.x = margem + b.raio;
                b.vx = -b.vx * 0.8f;
            }
            if (b.x + b.raio > getWidth() - margem) {
                b.x = getWidth() - margem - b.raio;
                b.vx = -b.vx * 0.8f;
            }
            if (b.y - b.raio < 50) {
                b.y = 50 + b.raio;
                b.vy = -b.vy * 0.8f;
            }
            if (b.y + b.raio > getHeight() - 50) {
                b.y = getHeight() - 50 - b.raio;
                b.vy = -b.vy * 0.8f;
            }
            
            // Caçapas (simplificado)
            if (b.x < 70 && b.y < 70) afundarBola(b);
            else if (b.x > getWidth() - 70 && b.y < 70) afundarBola(b);
            else if (b.x < 70 && b.y > getHeight() - 70) afundarBola(b);
            else if (b.x > getWidth() - 70 && b.y > getHeight() - 70) afundarBola(b);
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
    }
    
    private void afundarBola(Bola b) {
        if (b.branca) {
            // Bola branca volta
            b.x = 200;
            b.y = 500;
            b.vx = 0;
            b.vy = 0;
            return;
        }
        
        bolas.remove(b);
        bolasAfundadas++;
        
        if (bolasAfundadas >= 15) {
            jogoFinalizado = true;
            venceu = true;
            jogador.addXp(100);
            jogador.registrarPartida(true);
        }
    }
    
    private void verificarDerrota() {
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
                float bx = getWidth()/2 - 100;
                float by = getHeight()/2 + 80;
                if (x > bx && x < bx + 200 && y > by && y < by + 60) {
                    iniciarJogo();
                    invalidate();
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
