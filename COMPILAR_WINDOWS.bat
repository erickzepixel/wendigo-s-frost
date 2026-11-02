@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>nul || (echo ERRO: Java nao encontrado. Instale JDK 17.& pause & exit /b 1)
java -version
where gradle >nul 2>nul || (echo ERRO: Gradle nao encontrado. Instale Gradle 8.1.1.& pause & exit /b 1)
echo Compilando Wendigos Frost...
call gradle --no-daemon build --stacktrace
if errorlevel 1 (echo FALHA: consulte os erros acima.& pause & exit /b 1)
echo SUCESSO: procure o arquivo JAR em build\libs
pause
