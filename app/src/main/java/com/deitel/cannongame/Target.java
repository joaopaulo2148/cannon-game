// Target.java
// Subclasse de GameElement customizada para o Target
package com.deitel.cannongame;

public class Target extends GameElement {
   private int hitReward; // recompensa de tempo ao acertar este alvo

   // construtor
   public Target(CannonView view, int color, int hitReward, int x, int y,
      int width, int length, float velocityY) {
      super(view, color, CannonView.TARGET_SOUND_ID, x, y, width, length,
         velocityY);
      this.hitReward = hitReward;
   }

   // retorna a recompensa deste Target
   public int getHitReward() {
      return hitReward;
   }
}
