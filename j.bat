@if exist ManualPlay.jar goto update_label

jar cvf ManualPlay.jar -C classes .
jar uvf ManualPlay.jar LICENSE README.md

@exit /b

:update_label

jar uvf ManualPlay.jar -C classes .

