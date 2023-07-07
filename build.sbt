enablePlugins(ScalaJSPlugin)

name := "Curl to Scala"
scalaVersion := "3.3.0" // or any other Scala version >= 2.11.12

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
libraryDependencies ++= Seq(
  "com.raquo" %%% "laminar" % "15.0.0",
  "com.lihaoyi" %%% "fastparse" % "3.0.1"
)
