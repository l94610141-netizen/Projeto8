package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class GameView extends View {
    Paint tinta = new Paint();
    GerenciadorJogador jogador;
    String nomeCidade;
    
    class Bola {
        float x, y, raio, vx, vy;
        int cor, numero;
        boolean branca;
        
        Bola(float x, float y, float raio, int cor, int numero, boolean branca) {
            this.x = x;
            this.y = y;
            this.raio = raio;
            this.cor = cor;
            this.numero = numero;
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
            // Bola
            paint.setColor(cor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, raio, paint);
            
            // Número da bola
            if (!branca && numero > 0) {
                paint.setColor(Color.WHITE);
                paint.setTextSize(raio * 0.9f);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(String.valueOf(numero), x, y + raio * 0.3f, paint);
            }
            
            // Bola branca - contorno
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
    boolean breakFeito = false;
    float startX = -1, startY = -1;
    boolean arrastando = false;
    
    // Caçapas
    float[][] cacapas = {
        {70, 70},
        {0, 0},
        {0, 0},
        {70, 0},
        {0, 0},
        {0, 0}
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
        breakFeito = false;
        
        // Posicionar bolas para o break (triângulo)
        float centroX = getWidth() - 200;
        float centroY = getHeight() / 2;
        
        // Bola branca (lado esquerdo)
        bolaBranca = new Bola(250, centroY, 14, Color.WHITE, 0, true);
        bolas.add(bolaBranca);
        
        // Formação triangular 8 Ball (15 bolas)
        // Fileira 1: 1 bola
        // Fileira 2: 2 bolas
        // Fileira 3: 3 bolas (bola 8 no centro)
        // Fileira 4: 4 bolas
        // Fileira 5: 5 bolas
        float espacamento = 25;
        float[][] posicoes = {
            // Fileira 1
            {centroX, centroY},
            // Fileira 2
            {centroX + espacamento, centroY - 13}, 
            {centroX + espacamento, centroY + 13},
            // Fileira 3 (bola 8 no centro)
            {centroX + espacamento * 2, centroY - 26},
            {centroX + espacamento * 2, centroY},      // Bola 8
            {centroX + espacamento * 2, centroY + 26},
            // Fileira 4
            {centroX + espacamento * 3, centroY - 39},
            {centroX + espacamento * 3, centroY - 13},
            {centroX + espacamento * 3, centroY + 13},
            {centroX + espacamento * 3, centroY + 39},
            // Fileira 5
            {centroX + espacamento * 4, centroY - 52},
            {centroX + espacamento * 4, centroY - 26},
            {centroX + espacamento * 4, centroY},
            {centroX + espacamento * 4, centroY + 26},
            {centroX + espacamento * 4, centroY + 52}
        };
        
        // Cores e números das bolas (padrão 8 Ball)
        int[][] bolasConfig = {
            {1, Color.YELLOW},   // Bola 1 - amarela
            {2, Color.BLUE},     // Bola 2 - azul
            {3, Color.RED},      // Bola 3 - vermelha
            {4, Color.MAGENTA},  // Bola 4 - roxa
            {5, Color.parseColor("#FF8800")}, // Bola 5 - laranja
            {6, Color.GREEN},    // Bola 6 - verde
            {7, Color.parseColor("#8B0000")}, // Bola 7 - marrom
            {8, Color.BLACK},    // Bola 8 - preta
            {9, Color.YELLOW},   // Bola 9 - amarela
            {10, Color.BLUE},    // Bola 10 - azul
            {11, Color.RED},     // Bola 11 - vermelha
            {12, Color.MAGENTA}, // Bola 12 - roxa
            {13, Color.parseColor("#FF8800")}, // Bola 13 - laranja
            {14, Color.GREEN},   // Bola 14 - verde
            {15, Color.parseColor("#8B0000")}  // Bola 15 - marrom
        };
        
        for (int i = 0; i < 15; i++) {
            int num = bolasConfig[i][0];
            int cor = bolasConfig[i][1];
            // Ajuste: bola 8 no centro (posição 4)
            int idx = (i < 4) ? i : (i == 4 ? 14 : i - 1);
            bolas.add(new Bola(posicoes[i][0], posicoes[i][1], 13, cor, num, false));
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
        canvas.drawColor(Color.parseColor("#1a1a2e"));
        
        // Cabeçalho (avatar e informações)
        desenharCabecalho(canvas);
        
        // Mesa
        float margem = 50;
        tinta.setColor(Color.parseColor("#2d7d46"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawRect(margem, 50, getWidth() - margem, getHeight() - 50, tinta);
        
        // Bordas
        tinta.setColor(Color.parseColor("#8B4513"));
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(12);
        canvas.drawRect(margem, 50, getWidth() - margem, getHeight() - 50, tinta);
        
        // Caçapas
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
        
        // Mensagem "Break" se não foi feito
        if (!breakFeito && !jogoFinalizado) {
            tinta.setColor(Color.argb(100, 255, 255, 255));
            tinta.setTextSize(40);
            tinta.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("🔥 FAÇA O BREAK!", getWidth()/2, 120, tinta);
        }
        
        // Mira
        if (arrastando && startX != -1 && !jogoFinalizado) {
            tinta.setColor(Color.argb(150, 255, 255, 255));
            tinta.setStrokeWidth(2);
            canvas.drawLine(bolaBranca.x, bolaBranca.y, startX, startY, tinta);
            
            // Barra de potência
            float dx = startX - bolaBranca.x;
            float dy = startY - bolaBranca.y;
            float dist = (float) Math.sqrt(dx*dx + dy*dy);
            float potencia = Math.min(dist / 15, 15);
            
            tinta.setColor(Color.parseColor("#333366"));
            tinta.setStyle(Paint.Style.FILL);
            canvas.drawRect(50, getHeight() - 40, 250, getHeight() - 15, tinta);
            
            int corPotencia = potencia > 10 ? Color.RED : potencia > 5 ? Color.YELLOW : Color.GREEN;
            tinta.setColor(corPotencia);
            canvas.drawRect(50, getHeight() - 40, 50 + potencia * 13, getHeight() - 15, tinta);
        }
        
        // Fim de jogo
        if (jogoFinalizado) {
            desenharFimJogo(canvas);
        }
        
        postInvalidateDelayed(16);
    }
    
    private void desenharCabecalho(Canvas canvas) {
        // Avatar (círculo)
        float avatarX = 90;
        float avatarY = 40;
        float avatarRaio = 30;
        
        // Círculo do avatar
        tinta.setColor(Color.parseColor("#FFD700"));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawCircle(avatarX, avatarY, avatarRaio, tinta);
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.STROKE);
        tinta.setStrokeWidth(3);
        canvas.drawCircle(avatarX, avatarY, avatarRaio, tinta);
        
        // Emoji do avatar (temporário)
        tinta.setColor(Color.BLACK);
        tinta.setStyle(Paint.Style.FILL);
        tinta.setTextSize(35);
        tinta.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("🎱", avatarX, avatarY + 12, tinta);
        
        // Indicador de clique
        tinta.setColor(Color.argb(50, 255, 255, 255));
        tinta.setStyle(Paint.Style.FILL);
        canvas.drawCircle(avatarX, avatarY, avatarRaio + 5, tinta);
        
        // Nome/Nível
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(18);
        tinta.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("⭐ Nível " + jogador.getNivel(), avatarX + avatarRaio + 15, avatarY - 5, tinta);
        
        // XP
        tinta.setColor(Color.GRAY);
        tinta.setTextSize(14);
        canvas.drawText(jogador.getXp() + "/" + jogador.getXpProximoNivel() + " XP", avatarX + avatarRaio + 15, avatarY + 18, tinta);
        
        // Bolas afundadas (canto direito)
        tinta.setColor(Color.WHITE);
        tinta.setTextSize(20);
        tinta.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("🎯 " + bolasAfundadas + "/15", getWidth() - 20, 40, tinta);
    }
    
    private void desenharFimJogo(Canvas canvas) {
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
        
        tinta.setColor(Color.parseColor("#333366"));
        tinta.setStyle(Paint.Style.FILL);
        by = getHeight()/2 + 160;
        canvas.drawRoundRect(bx, by, bx + 240, by + 60, 20, 20, tinta);
        tinta.setColor(Color.WHITE);
        canvas.drawText("🏠 MENU", getWidth()/2, by + 40, tinta);
    }
    
    private void atualizarFisica() {
        float margem = 50;
        float w = getWidth();
        float h = getHeight();
        
        // Verificar se o break foi feito
        boolean todasParadas = true;
        for (Bola b : bolas) {
            if (b.vx != 0 || b.vy != 0) {
                todasParadas = false;
                break;
            }
        }
        if (!breakFeito && !todasParadas) {
            breakFeito = true;
        }
        
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
                        b.x = 250;
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
        todasParadas = true;
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
                
                if (x > bx && x < bx + 240 && y > by && y < by + 60) {
                    iniciarJogo();
                    invalidate();
                    return true;
                }
                
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
        
        // Verificar clique no avatar (botão perfil)
        float avatarX = 90;
        float avatarY = 40;
        float avatarRaio = 30;
        float dx = x - avatarX;
        float dy = y - avatarY;
        if (e.getAction() == MotionEvent.ACTION_UP && Math.sqrt(dx*dx + dy*dy) < avatarRaio + 10) {
            ((MainActivity) getContext()).trocarTela(new TelaPerfil(getContext()));
            return true;
        }
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = x - bolaBranca.x;
                dy = y - bolaBranca.y;
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
