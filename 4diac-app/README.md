# Eclipse 4DIAC™ example application

This is the accompanying example application for the Milo talk.
It is an application based on 4DIAC. 4DIAC provides two main
components: an IDE and a runtime environment (forte).

## Editing this application

This application can be edited with the 4DIAC IDE.

## Running this application

This application is intended to run on a Raspberry Pi
with SysFS GPIO support for controlling the LEDs.

* Install "forte" with OPC UA support
* Run `FORTE_BOOT_FILE=<path-to>/ece1_RaspberryPI.fboot forte`

The `ece1_RaspberryPI.fboot` is located in this repository at
[fboot/ece1_RaspberryPI.fboot](fboot/ece1_RaspberryPI.fboot).

During the talk the 4DIAC runtime will be installed on
a Samsung Artik 5 board, running Fedora 24. The example can be
installed on it with:

    dnf install https://dentrassi.de/download/4diac-forte-opcua/open62541-0.3-1.git.4103.3be5205.fc24.armv7hl.rpm
    dnf install https://dentrassi.de/download/4diac-forte-opcua/4diac-forte-1.9.0-1.git.421.ec469df.fc26.armv7hl.rpm

If you want to re-compile those binaries for other platforms
you can also download the exact source RPMs from https://dentrassi.de/download/4diac-forte-opcua
