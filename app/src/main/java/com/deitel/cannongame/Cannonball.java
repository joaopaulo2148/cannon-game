// Cannonball.java
// Representa a bola de canhão disparada pelo Cannon
package com.deitel.cannongame;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Cannonball extends GameElement {
   private float velocityX;
   private boolean onScreen;

   // construtor
   public Cannonball(CannonView view, int color, int soundId, int x,
      int y, int radius, float velocityX, float velocityY) {
      super(view, color, soundId, x, y,
         2 * radius, 2 * radius, velocityY);
      this.velocityX = velocityX;
      onScreen = true;
   }

   // retorna o raio da Cannonball
   private int getRadius() {
      return (shape.right - shape.left) / 2;
   }

   // testa se a Cannonball colide com o GameElement dado
   public boolean collidesWith(GameElement element) {
      return (Rect.intersects(shape, element.shape) && velocityX > 0);
   }

   // retorna true se a Cannonball está na tela
   public boolean isOnScreen() {
      return onScreen;
   }

   // inverte a velocidade horizontal da Cannonball
   public void reverseVelocityX() {
      velocityX *= -1;
   }

   // atualiza a posição da Cannonball
   @Override
   public void update(double interval) {
      super.update(interval); // atualiza a posição vertical

      // atualiza a posição horizontal
      shape.offset((int) (velocityX * interval), 0);

      // se a Cannonball sair da tela
      if (shape.top < 0 || shape.left < 0 ||
         shape.bottom > view.getScreenHeight() ||
         shape.right > view.getScreenWidth())
         onScreen = false; // marca para ser removida
   }

   // desenha a Cannonball no canvas
   @Override
   public void draw(Canvas canvas) {
      canvas.drawCircle(shape.left + getRadius(),
         shape.top + getRadius(), getRadius(), paint);
   }
}
