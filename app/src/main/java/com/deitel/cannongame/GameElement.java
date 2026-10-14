// GameElement.java
// Representa um elemento de jogo delimitado por um retângulo
package com.deitel.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameElement {
   protected CannonView view; // view que contém este GameElement
   protected Paint paint = new Paint(); // Paint usado para desenhar
   protected Rect shape; // limites retangulares do GameElement
   private float velocityY; // velocidade vertical
   private int soundId; // som associado a este GameElement

   // construtor público
   public GameElement(CannonView view, int color, int soundId, int x,
                      int y, int width, int length, float velocityY) {
      this.view = view;
      paint.setColor(color);
      shape = new Rect(x, y, x + width, y + length); // define os limites
      this.soundId = soundId;
      this.velocityY = velocityY;
   }

   // atualiza a posição e verifica colisões com as paredes
   public void update(double interval) {
      // atualiza a posição vertical
      shape.offset(0, (int) (velocityY * interval));

      // se colidir com o topo/fundo, inverte a direção
      if (shape.top < 0 && velocityY < 0 ||
              shape.bottom > view.getScreenHeight() && velocityY > 0)
         velocityY *= -1; // inverte a velocidade vertical
   }

   // acesso à velocidade vertical para as subclasses (movimento complexo)
   protected float getVelocityY() {
      return velocityY;
   }

   protected void setVelocityY(float velocityY) {
      this.velocityY = velocityY;
   }

   // multiplica a velocidade vertical (usado para aumentar a dificuldade)
   public void scaleSpeed(float factor) {
      velocityY *= factor;
   }

   // inverte a direção do movimento vertical
   public void reverseDirection() {
      velocityY *= -1;
   }

   // reduz a altura mantendo o centro, sem passar de minLength
   public void scaleLength(float factor, int minLength) {
      int newLength = Math.max(minLength, (int) (shape.height() * factor));
      int centerY = shape.centerY();
      shape.top = centerY - newLength / 2;
      shape.bottom = shape.top + newLength;
   }

   // desenha este GameElement no Canvas
   public void draw(Canvas canvas) {
      canvas.drawRect(shape, paint);
   }

   // toca o som correspondente a este tipo de GameElement
   public void playSound() {
      view.playSound(soundId);
   }
}