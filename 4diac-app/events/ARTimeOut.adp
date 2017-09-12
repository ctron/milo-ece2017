<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE AdapterType SYSTEM "http://www.holobloc.com/xml/LibraryElement.dtd">
<AdapterType Comment="Interface for a resetable time out service" Name="ARTimeOut">
  <Identification Standard="61499-1"/>
  <VersionInfo Author="AZ" Date="2014-10-21" Organization="4DIAC-Consortium" Version="1.0"/>
  <CompilerInfo/>
  <InterfaceList>
    <EventInputs>
      <Event Comment="Request from Socket" Name="TimeOut" Type="Event"/>
    </EventInputs>
    <EventOutputs>
      <Event Comment="start/reset timeout notifcation" Name="START" Type="Event">
        <With Var="DT"/>
      </Event>
      <Event Comment="stop timeout notification" Name="STOP" Type="Event"/>
    </EventOutputs>
    <InputVars/>
    <OutputVars>
      <VarDeclaration Comment="timeout duration" InitialValue="" Name="DT" Type="TIME"/>
    </OutputVars>
  </InterfaceList>
  <Service Comment="Interface for a resetable time out service" LeftInterface="SOCKET" RightInterface="PLUG">
    <ServiceSequence Name="Timeout">
      <ServiceTransaction>
        <InputPrimitive Event="START" Interface="PLUG" Parameters="TD"/>
        <OutputPrimitive Event="START" Interface="SOCKET" Parameters="TD"/>
      </ServiceTransaction>
      <ServiceTransaction>
        <InputPrimitive Event="TimeOut" Interface="SOCKET" Parameters=""/>
        <OutputPrimitive Event="TimeOut" Interface="PLUG"/>
      </ServiceTransaction>
    </ServiceSequence>
    <ServiceSequence Name="normal operation">
      <ServiceTransaction>
        <InputPrimitive Event="Start" Interface="PLUG" Parameters="TD"/>
        <OutputPrimitive Event="Start" Interface="SOCKET" Parameters="TD"/>
      </ServiceTransaction>
      <ServiceTransaction>
        <InputPrimitive Event="STOP" Interface="PLUG" Parameters=""/>
        <OutputPrimitive Event="STOP" Interface="SOCKET" Parameters=""/>
      </ServiceTransaction>
    </ServiceSequence>
  </Service>
</AdapterType>
