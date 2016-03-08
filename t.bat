@setlocal
@set MY_CLASS_DIR=%~dp0classes
@set SAMPLE_DATA=%~dp0sample.txt
java -cp "%MY_CLASS_DIR%" Main 0< %SAMPLE_DATA%
@endlocal
