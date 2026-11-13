# Sem Shorts

App Android que bloqueia o YouTube Shorts sem bloquear o resto do YouTube.

Quando o player de Shorts abre no app oficial do YouTube (pela aba Shorts ou ao tocar em um Short no feed), o Sem Shorts aciona o "voltar" automaticamente e exibe o aviso "Shorts bloqueado".

## Como funciona

O app usa um serviço de acessibilidade (`AccessibilityService`) que observa apenas o pacote `com.google.android.youtube`. A cada mudança de tela, ele procura os identificadores de view do player de Shorts (`reel_recycler`, `reel_player_page_container`, `reel_watch_fragment_root`). Se algum estiver visível, executa a ação global de voltar.

A tela do app tem:
- status do serviço (ativado/desativado);
- atalho para as configurações de acessibilidade;
- botão para pausar o bloqueio;
- contador de Shorts bloqueados.

## Privacidade

- Não declara permissão de internet: nenhum dado sai do aparelho.
- Só recebe eventos do app do YouTube.
- Guarda localmente apenas o contador de bloqueios e o estado do botão liga/desliga.

## Instalação

Baixe o APK na página de [Releases](../../releases).

Como o app não vem da Play Store e usa acessibilidade, o Android impõe algumas barreiras:

1. **Play Protect** pode bloquear a instalação ("O app foi bloqueado para proteger seu dispositivo"). Alternativas: instalar via ADB (`adb install SemShorts.apk`) ou pausar temporariamente a verificação do Play Protect e religar logo depois.
2. **Configuração restrita**: tente ativar o serviço uma vez; depois vá em Configurações > Aplicativos > Sem Shorts > ⋮ > *Permitir configurações restritas*.
3. Ative o serviço em Configurações > Acessibilidade > Apps instalados > Sem Shorts.

Requer Android 10 ou superior. Testado em um Samsung Galaxy S24.

## Limitações

- As miniaturas de Shorts continuam aparecendo no feed; elas só não abrem.
- A detecção depende de identificadores internos do YouTube. Uma atualização do app pode mudá-los e o bloqueio parar até os IDs serem atualizados em `ShortsBlockerService.java`.
- Funciona apenas no app oficial do YouTube, não no navegador.

## Estrutura

```
src/        código Java (MainActivity e ShortsBlockerService)
apk/        AndroidManifest.xml, recursos e apktool.yml
build.sh    script de build
```

## Build

O projeto não usa Gradle. O `build.sh` executa quatro etapas:

1. `javac --release 8` contra o `android.jar` da API 34;
2. `dx` para converter as classes em `classes.dex`;
3. [Apktool](https://github.com/iBotPeaches/Apktool) para compilar os recursos e montar o APK;
4. [uber-apk-signer](https://github.com/patrickfav/uber-apk-signer) para alinhar e assinar.

Os caminhos das ferramentas no `build.sh` precisam ser ajustados para o seu ambiente. Um APK assinado com outra chave não atualiza uma instalação existente: desinstale a versão anterior antes.

## Aviso

Projeto pessoal, sem vínculo com o Google ou o YouTube.

## Uso de IA

Este projeto foi desenvolvido com auxílio de inteligência artificial. O código-fonte, os recursos e o script de build foram gerados pelo Claude (Anthropic) a partir de uma conversa em que descrevi o que eu queria: bloquear o Shorts sem bloquear o YouTube inteiro. O APK também foi compilado e assinado pela IA, com as ferramentas listadas na seção Build.

Minha parte foi definir o problema, instalar e testar o app no meu aparelho, resolver as barreiras de instalação do Android e publicar o projeto.

O código é curto (duas classes Java) e está aberto para quem quiser revisar.