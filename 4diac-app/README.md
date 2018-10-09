# Eclipse 4DIAC™ example application

This is the accompanying example application for the OPC UA talk.
It is an application based on 4DIAC. 4DIAC provides two main
components: an IDE and a runtime environment (forte).

## Editing this application

This application can be edited with the 4DIAC IDE.

## Running this application

This application is intended to run on a Raspberry Pi
with SysFS GPIO support for controlling the LEDs.

However, you can also install the 4DIAC runtime, and this
application, on any other machine supporting 4DIAC. It might
only be that you have no blinking LED in this case.

The generic steps are:

* Install "forte" with OPC UA support
* Run `FORTE_BOOT_FILE=<path-to>/ece1_RaspberryPI.fboot forte`

The `ece1_RaspberryPI.fboot` is located in this repository at
[fboot/ece1_RaspberryPI.fboot](fboot/ece1_RaspberryPI.fboot).

## Running on Fedora

Fedora already provides 4DIAC, so you can simply install this by executing
(you can do the same for RHEL/CentOS if you enable EPEL):

    sudo dnf install 4diac-forte

Then copy the "boot file" to `/etc/4diac-forte-boot`, and then enable and start 4DIAC:

    sudo systemctl enable --now 4diac-forte

### Open up the firewall

**Note:** The next steps allow unauthorized access to this device. Think twice before you do this.

If you want to access the OPC UA server from outside the device, you need to open the firewall port:

    sudo firewall-cmd --zone=public --add-port=4840/tcp --permanent
    sudo firewall-cmd --reload

The same is true for the 4DIAC IDE monitor port:

    sudo firewall-cmd --zone=public --add-port=61499/tcp --permanent
    sudo firewall-cmd --reload
