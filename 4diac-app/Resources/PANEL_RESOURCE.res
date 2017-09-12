<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ResourceType SYSTEM "http://www.holobloc.com/xml/LibraryElement.dtd" >
<ResourceType Name="PANEL_RESOURCE" Comment="Resource for graphic Panel" >
  <Identification Standard="61499-2" />
  <VersionInfo Organization="Holobloc Inc" Version="0.3" Author="JHC" Date="2006-02-28" Remarks="Removed TRACE, set pkg to fb.rt.hmi." />
  <VersionInfo Organization="Rockwell Automation" Version="0.2" Author="JHC" Date="2000-05-28" Remarks="Set E_RESTART position." />
  <VersionInfo Organization="Rockwell Automation" Version="0.1" Author="JHC" Date="2000-05-10" Remarks="Removed &#34;Speed&#34; Attribute" />
  <VersionInfo Organization="Rockwell Automation" Version="0.0" Author="JHC" Date="2000-01-25" Remarks="Testing &#34;Speed&#34; Attribute" />
  <CompilerInfo header="package fb.rt.hmi; import java.awt.*;" >
    <Compiler Language="Java" Vendor="Sun" Product="JDK" Version="1.5.0" />
  </CompilerInfo>
  <VarDeclaration Name="ROWS" Type="DINT" Comment="Rows(if any)in grid layout" />
  <VarDeclaration Name="COLS" Type="DINT" Comment="Columns(if any)in grid layout" />
  <FBNetwork >
    <FB Name="START" Type="E_RESTART" x="11.7647" y="11.7647" />
  </FBNetwork>
</ResourceType>
