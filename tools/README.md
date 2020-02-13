# Tools for Neutron Modding

## Functionality

the packer takes a minecraft APK,
    unpack it,
    put native libraries into libs/eabiarm-v7
    put classes files in jar into classes.dex
    and instrument MainActivity of Minecraft for Neutron Runtime startup
then put it back.

## Usage
java -jar packer-all.jar
    -rtjar %PATH TO JAR% (such as ./neutron_runtime.jar, NOTE: it must be a path! ./ part is needed!)
    -lib %PATH TO NATIVE LIBRARY% (such as ./libneutron.so)
    -apk %PATH TO MINECRAFT APK% (such as ./mc.apk)

the result will be put in to mc-patched.apk in the current working directory

Note: THIS IS USED TO INSTALL NEUTRON RUNTIME, so a runtime jar is always needed!

Note: the outcome is a unsigned apk, you need to sign it to use it!

# Packer for mincraft mod