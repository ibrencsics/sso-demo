@echo off
REM This batch file is used to create server certificates from certificate request files.
REM USAGE:  ca_create_server_cert.bat [inputfilename] [outputfile]
REM If either of the command line parameters are missing, you will be prompted for it.

SET basedir=.
if "%1"=="" (
 SET /P requestfile="Enter certificate request filename (should already be in %basedir%\work\requests): "
) ELSE (
 SET requestfile=%1
)

if "%2"=="" (
 SET /P outputfile="Enter output filename (with no extension): "
) ELSE (
 SET outputfile=%2
)

REM change to the work directory
cd %basedir%\work

echo requestfile=%requestfile%
echo outputfile=%outputfile%
echo binpath=%binpath%

REM create the certificate
openssl ca -policy policy_anything -config openssl.conf -cert certs\ca.cer -in requests\%requestfile% -keyfile keys\ca.key -days 1000 -out certs\%outputfile%.cer

REM convert it to an x509 format cert for IIS
openssl x509 -in certs\%outputfile%.cer -out certs\%outputfile%_x509.cer

echo If there were no error messages, the new certificate is located in:
echo %basedir%work\certs\%outputfile%_x509.cer