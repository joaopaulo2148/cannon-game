# Cannon Game — Programação III

Implementação do Capítulo 6 (Cannon Game App) do livro *Android 6 for Programmers: An App-Driven Approach* (3ª edição),
trabalho da disciplina de Programação III (UEMG — Sistemas de Informação).

## Integrantes

- [João Paulo Borges Pimenta] — [@joaopaulo2148](https://github.com/joaopaulo2148)
- [João Victor Neves de Souza Mateus Dizaró] — [@joaodizaro](https://github.com/joaodizaro)

## Objetivo

Aplicar em um jogo completo os conceitos da disciplina: animação quadro a quadro, desenho com `Canvas` e `Paint`,
sons com `SoundPool`, game loop em `Thread`, `SurfaceView`/`SurfaceHolder`, modo imersivo e eventos de toque.

## Como executar

1. Instale o Android Studio (versão estável recente) e o Git.
2. `git clone https://github.com/joaopaulo2148/cannon-game.git` e abra a pasta com **File > Open**.
3. Aguarde o Gradle Sync (instale o Android SDK Platform 37 se solicitado).
4. Execute em um emulador ou celular (**Run ▶**). O jogo funciona em orientação **landscape**.

Requisitos: `minSdk 23`, `compileSdk/targetSdk 37`, Java 11.

## Como jogar

Toque (ou arraste) na tela para mirar o canhão e atirar. Acerte os 9 alvos antes de o tempo acabar:
cada alvo atingido soma tempo e cada batida no bloqueador desconta tempo.

## Funcionalidades implementadas

Base (Capítulo 6):
- Canhão que mira na direção do toque, com uma bola por vez
- 9 alvos e 1 bloqueador com movimento vertical
- Contagem regressiva com recompensa por acerto e penalidade
- Efeitos sonoros de disparo, acerto no alvo e acerto no bloqueador
- Game loop em `CannonThread` desenhando em `SurfaceView`
- Modo imersivo e diálogo de fim de jogo com estatísticas

Recursos extras (sugestões do professor):
- **Pontuação com sequência e multiplicador** (recurso 1): 100 pontos por alvo × multiplicador (x1 a x5),
  sequência zerada ao errar o tiro ou bater no bloqueador, bônus por tempo restante ao vencer,
  HUD com pontos e sequência atualizado em tempo real e pontuação no diálogo final
- **Ranking local** (recurso 9): as 5 maiores pontuações ficam salvas no aparelho com `SharedPreferences`
  (classe `HighScores`); o diálogo de fim de jogo avisa novo recorde, informa a posição e lista o ranking

## Screenshots

*(adicionar capturas de tela ou GIF da execução)*


## Como o projeto foi montado no Android Studio (histórico)

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
- `surfaceDestroyed` protegido contra `cannonThread == null`.
- `updatePositions` não executa após o fim do jogo, evitando o diálogo de fim de jogo duplicado.
- Diálogo de fim de jogo feito com `AlertDialog` direto (em vez de `DialogFragment` do `android.app`), que fechava o app em Android recente.

## Erros de digitação no PDF (não copie)
- Fig. 6.34 diz "DoodleView", mas é CannonView.
- Texto cita `Build.VERSION_CODES_KITKAT` (correto: `Build.VERSION_CODES.KITKAT`).
- Texto de 6.13.6 cita `TARGET_HEIGHT_PERCENT` / `BLOCKER_HEIGHT_PERCENT` (as constantes são `..._LENGTH_PERCENT`).
- Seção 6.10 cita `hitPenalty` (o atributo é `hitReward`).
- Fig. 6.30 comenta "CannonGameFragment" (a classe é MainActivityFragment).