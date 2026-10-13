// Particle.java
// Pequeno círculo colorido da explosão exibida quando um alvo é atingido
package com.deitel.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Particle {
    private float x; // posição do centro
    private float y;
    private final float velocityX; // velocidade em pixels por segundo
    private final float velocityY;
    private final float radius;
    private final int color;
    private final double lifetime; // duração total em segundos
    private double age = 0; // tempo de vida já consumido

    public Particle(float x, float y, double angle, float speed,
                    float radius, int color, double lifetime) {
        this.x = x;
        this.y = y;
        // movimento radial: a direção vem do ângulo sorteado
        this.velocityX = (float) (Math.cos(angle) * speed);
        this.velocityY = (float) (Math.sin(angle) * speed);
        this.radius = radius;
        this.color = color;
        this.lifetime = lifetime;
    }

    // atualiza a posição e o tempo de vida (interval em segundos)
    public void update(double interval) {
        x += velocityX * interval;
        y += velocityY * interval;
        age += interval;
    }

    // a partícula já "morreu"?
    public boolean isDead() {
        return age >= lifetime;
    }

    // desenha o círculo, diminuindo a opacidade conforme envelhece (fade out)
    public void draw(Canvas canvas, Paint paint) {
        double remaining = 1.0 - age / lifetime;
        if (remaining <= 0)
            return;

        paint.setColor(color);
        paint.setAlpha((int) (255 * remaining));
        canvas.drawCircle(x, y, radius, paint);
    }
}