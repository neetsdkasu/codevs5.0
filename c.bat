@setlocal
@set MY_CLASS_DIR=%~dp0classes
@set MY_SRC_DIR=%~dp0src
@if not exist "%MY_CLASS_DIR%" mkdir "%MY_CLASS_DIR%"
javac -sourcepath "%MY_SRC_DIR%" -d "%MY_CLASS_DIR%"  "%MY_SRC_DIR%\Main.java" "%MY_SRC_DIR%\Player.java"
@endlocal
