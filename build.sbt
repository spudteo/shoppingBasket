name := "shoppingBasket"
version := "1.0"
scalaVersion := "2.12.18"


libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)

scalafmtOnCompile := true

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := "4.8.14"

scalacOptions ++= Seq(
  "-Ywarn-unused",
  "-Ywarn-unused-import", 
  "-Xlint:unused",         
  "-deprecation",            
  "-feature",               
  "-unchecked"             
)

run / fork := true