@echo off
set ANT_OPTS=-Xmx512m -Dfile.encoding=UTF-8
set ANT_HOME=%~dp0apache-ant-1.9.1
set NPM_HOME=%~dp0npm
set NPM_BINARIES_HOME=%NPM_HOME%\node_modules
set NODE_HOME=%NPM_HOME%\node
set PATH=%NPM_BINARIES_HOME%\.bin;%NODE_HOME%;%ANT_HOME%\bin;%PATH%

cp %NPM_HOME%\phantomjs.exe %NPM_HOME%\node_modules\karma-phantomjs-launcher\node_modules\phantomjs\lib\phantom\bin\phantomjs.exe

rem deleting CLASSPATH as a workaround for PLA-8702
set CLASSPATH=
echo Setting ant home to: %ANT_HOME%

ant -version



