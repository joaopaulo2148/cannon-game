# Cannon Game — Programação III

Implementação do Capítulo 6 (Cannon Game App) do livro *Android 6 for Programmers*.

## Como montar no Android Studio

1. **New Project > Empty Views Activity**, linguagem **Java**.
   - Name: `Cannon Game`
   - Package name: `com.deitel.cannongame`
   - Minimum SDK: **API 23** (o código usa `getColor(id, theme)` e `SoundPool.Builder`)
2. Copie os arquivos desta pasta para o projeto, substituindo os gerados:
   - `java/com/deitel/cannongame/*.java` -> pacote `com.deitel.cannongame`
   - `res/layout/activity_main.xml` e `res/layout/fragment_main.xml`
   - `res/values/strings.xml`, `colors.xml` e `themes.xml`
     (se o projeto tiver `res/values-night/themes.xml`, apague-o ou ajuste o tema também)
   - `res/raw/*.wav` (crie a pasta: *res > New > Android Resource Directory > raw*)
   - `AndroidManifest.xml` (mantenha o seu `@mipmap/ic_launcher` / ícone)
3. Troque os `.wav` de teste pelos sons do livro (pasta `sounds` dos exemplos):
   `cannon_fire.wav`, `target_hit.wav`, `blocker_hit.wav`.
4. Sync Gradle e execute em um aparelho (ou AVD) em landscape.

## Mapeamento livro -> código atual (AndroidX)

| Livro (support library)            | Aqui (AndroidX)                    |
|------------------------------------|------------------------------------|
| android.support.v7.app.AppCompatActivity | androidx.appcompat.app.AppCompatActivity |
| android.support.v4.app.Fragment    | androidx.fragment.app.Fragment     |
| `<fragment android:name=...>`      | `androidx.fragment.app.FragmentContainerView` |

## Ajustes em relação ao texto do livro
- `canvas != null` verificado antes de `drawGameElements` (evita crash se `lockCanvas` retornar null).
- `playSound` / `releaseResources` protegidos contra `soundPool == null`.

## Erros de digitação no PDF (não copie)
- Fig. 6.34 diz "DoodleView", mas é CannonView.
- Texto cita `Build.VERSION_CODES_KITKAT` (correto: `Build.VERSION_CODES.KITKAT`).
- Texto de 6.13.6 cita `TARGET_HEIGHT_PERCENT` / `BLOCKER_HEIGHT_PERCENT` (as constantes são `..._LENGTH_PERCENT`).
- Seção 6.10 cita `hitPenalty` (o atributo é `hitReward`).
- Fig. 6.30 comenta "CannonGameFragment" (a classe é MainActivityFragment).
"# cannon-game" 
