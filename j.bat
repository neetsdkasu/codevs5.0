@if exist ManualPlay.jar goto update_label

jar cvf ManualPlay.jar -C classes/ .

@exit /b

:update_label

jar uvf ManualPlay.jar -C classes/ .

