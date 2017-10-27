# How to run the examples

This guide provides some information on how to run the examples presented during the talk.

## Preparing

The easiest way is to check out the examples from Git and import them into the Eclipse IDE (Oxygen.1). This way
you can inspect the source code and run the examples from inside the IDE.

If course is it also possible to run the examples from the command line. For this you will need:

* Apach Maven 3.5+
* Java JDK 1.8

Compile the source code by executing:

    mvn package

Please also see [../4diac-app/README.md](../4diac-app/README.md) on how to set up the Samsung ARTIK 5
with the Eclipse 4DIAC Forte runtime and OPC UA support and the user application with the blinking LED. 

## Running the server example

In order for the server to properly start it must be able to bind to the
local TCP port 4840.

Issue the following command to start the server:

    mvn -f server-examples/pom.xml exec:java

The server will be started and list for connections on port 4840. To stop
the server press CTRL-C.

## Running the client examples

The client examples require a running OPC UA server instance. Some examples only
work with the 4DIAC application from the [4diac-app](4diac-app) directory.

In order to specifiy the target host and port it is possible to modify the file
[Constants.java](client-examples/src/main/java/de/dentrassi/ece2017/milo/Constants.java "title")
or pass "-Dhost=<my-host>" and "-Dport=4840" to the following Maven commands.

If you have an IPv6 link-local connection then the syntax for the hostname is like the
following example: `[fe80::2c82:aeff:fe0b:ac2%enp0s31f6]`. Where the address is wrapped in
square brackets and the address of the remote target has the interface name of the local interface
(in this case `enp0s31f6`) appended and joined by a percent sign (`%`). The following commands
will use `127.0.0.1` as an example, please be sure to update this value with the one appropriate
with your setup.

### Connect

Run the example by executing:

    mvn -f client-examples/pom.xml exec:java -Dhost=127.0.0.1 -Dexec.mainClass=de.dentrassi.ece2017.milo.step01.Connect

The example will connect to the remote OPC UA server and show the output on the console. Aside from the
Maven output the following output indicates a successful connection:

    Wait for completion
    Connected
    Bye bye

**Note:** There is a bug in the example program which will prevent this example from exiting if the connection
is not successful. This is on purpose and showed how not to do it. This can be fixed by fixing the "Semaphore"
construct with a proper call to `.get`. All other examples follow this pattern and don't suffer from this issue. 

### Browse

Run the example by executing:

    mvn -f client-examples/pom.xml exec:java -Dhost=127.0.0.1 -Dexec.mainClass=de.dentrassi.ece2017.milo.step02.Browse

This browses the full OPC UA namespace of the remote server.

### Lookup

Run the example by executing:

    mvn -f client-examples/pom.xml exec:java -Dhost=127.0.0.1 -Dexec.mainClass=de.dentrassi.ece2017.milo.step03.Lookup

This will look up the item `Objects/LedState/E_SR/Q` from the browser.

This example will only work in combination with the 4DIAC blinking LED application.

### Subscribe

Run the example by executing:

    mvn -f client-examples/pom.xml exec:java -Dhost=127.0.0.1 -Dexec.mainClass=de.dentrassi.ece2017.milo.step04.Subscribe

This will subscribe to the "value" attribute of nodes `ns=1;i=114` and `ns=1;i=117` and print out changes to the console. The
application will run for 15 minutes and then terminate.

This example will only work in combination with the 4DIAC blinking LED application.

### Write

Run the example by executing:

    mvn -f client-examples/pom.xml exec:java -Dhost=127.0.0.1 -Dexec.mainClass=de.dentrassi.ece2017.milo.step05.Write

This will write a boolean value to the remote application on node `ns=1;i=117`. By default it will write `false`, but the value
and also be provided by appending either of the following arguments to the command:

* `-Dexec.arguments=true`
* `-Dexec.arguments=false`

This example will only work in combination with the 4DIAC blinking LED application. Writing "true" will let the LED blink,
writing "false" will make it stop.

### Read

Run the example by executing:

    mvn -f client-examples/pom.xml exec:java -Dhost=127.0.0.1 -Dexec.mainClass=de.dentrassi.ece2017.milo.step06.Read

This example will read different nodes and attributes from the remote application.

This example will only work in combination with the 4DIAC blinking LED application.

