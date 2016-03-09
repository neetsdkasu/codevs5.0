@pushd "%~dp0"
@if not exist classes mkdir classes
javac -sourcepath src -d classes .\src\Main.java
@popd
