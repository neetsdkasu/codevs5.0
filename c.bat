@if not exist "%~dp0\classes" mkdir "%~dp0\classes"
javac -sourcepath "%~dp0\src" -d "%~dp0\classes"  "%~dp0\src\Main.java" "%~dp0\src\Player.java" 
