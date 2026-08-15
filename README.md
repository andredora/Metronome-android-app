# Metronome App

Metrónomo para Android desenvolvido com foco em precisão e UI baseada em componentes físicos.

## Tecnologias
* **Kotlin** + **Jetpack Compose**
* **Coroutines** (Temporização de alta precisão em nanossegundos)
* **SoundPool** (Áudio de baixa latência)
* **SplashScreen API** (Splash screen nativa do Android 12+)

## Funcionalidades
* **Motor de áudio auto-corretivo**: Garante BPM constante mesmo com oscilações de performance do sistema.
* **Pêndulo interativo**: Arrastar o peso na haste ajusta o BPM seguindo a escala clássica.
* **Controlo duplo**: Slider linear (precisão de 1 BPM) e pêndulo (escala clássica).
* **Temas**: Suporte completo para modo escuro e claro.

## Como configurar e correr
1. Abrir no Android Studio (Koala+).
2. Sincronizar o Gradle.
3. Executar em dispositivo com API 24+.

## Como gerar APK
No terminal:
```bash
./gradlew :app:assembleDebug
```
Localização do ficheiro: `app/build/outputs/apk/debug/app-debug.apk`

## Personalização de Sons
Para usar sons próprios, colocar ficheiros `.wav` em `app/src/main/res/raw/`:
* `primary_click.wav` - 1ª batida do compasso.
* `intermediate_click.wav` - Batidas acentuadas pelo utilizador.
* `normal_click.wav` - Batidas padrão.

## Especificações de Design
* **Grelha**: Base de 8dp.
* **Margens laterais**: 24dp.
* **Espaçamento entre blocos**: 24dp.
* **Cores**: Definidas em `ui/theme/Color.kt`.
