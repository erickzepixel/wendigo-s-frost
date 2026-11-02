# Wendigo's Frost — como gerar o JAR para mods

## GitHub (sem instalar Gradle)
1. Descompacte `WendigosFrost-codigo-fonte-0.2.0.zip`.
2. Envie o conteúdo extraído para a raiz do repositório `erickzepixel/wendigo-s-frost` no GitHub.
3. Acesse https://github.com/erickzepixel/wendigo-s-frost/actions, escolha **Compilar Wendigos Frost** e **Run workflow**.
4. Veja o resultado. Se **verde**, baixe e descompacte `WendigosFrost-JAR` em **Artifacts**.
5. Copie **wendigosfrost-0.2.0.jar** para a pasta `.minecraft/mods` do perfil Forge 1.20.1.
6. Também instale GeckoLib 4.4.9 e TerraBlender 3.0.1.7, ambos para Forge 1.20.1.
7. Teste em um mundo novo (ou copie seu mundo para backup antes de jogar).

## Windows
Instale JDK 17 e Gradle 8.1.1 e execute `COMPILAR_WINDOWS.bat` na pasta do projeto. O arquivo final estará em `build/libs/` após uma compilação bem-sucedida.

## Status de validação
O projeto foi atualizado, mas este ambiente não pôde executar Gradle nem obter ForgeGradle. Ainda faltam testes em jogo e o bioma exclusivo Ice Caverns via TerraBlender, além de alucinações/jumpscares avançados. Não trate ZIP como um arquivo `.jar`.
