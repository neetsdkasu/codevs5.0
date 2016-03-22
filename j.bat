@setlocal
@set MY_JAR=DefeatRandomAI.jar
@set MY_MAIN_CLASS=Main
@set MY_BASE_DIR=%~dp0
@set MY_CLASS_DIR=classes
@set MY_ARGS=%MY_JAR% %MY_MAIN_CLASS% -C %MY_CLASS_DIR%\ .
@set MY_ARGS2=%MY_JAR% LICENSE README.md

@pushd "%MY_BASE_DIR%"

@if exist %MY_JAR% goto update_label

jar cvfe %MY_ARGS%

@goto end_label

:update_label

jar uvfe %MY_ARGS%

:end_label

jar uvf %MY_ARGS2%

@popd

@endlocal