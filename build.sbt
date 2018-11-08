organization  := "com.practicingtechie"

name := "http4s020"

version := "0.0.1"

scalaVersion := "2.12.7"

fork := true

scalacOptions := Seq("-feature", "-encoding", "utf8",
  "-deprecation", "-unchecked", "-Xlint", "-Yrangepos", "-Ypartial-unification")


val scalaLoggingVersion = "3.9.0"
val logBackVersion = "1.2.3"
val scallopVersion = "3.1.2"
val configVersion = "1.3.3"
val http4sVersion = "0.20.0-M2"
val circeVersion = "0.10.0"
//val slickVersion = "3.2.3"
//val slickPgVersion = "0.16.3"
val specs2Version = "4.0.2"
//val jwtVersion = "0.18.0"
val catsParVersion = "0.2.0"


libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "ch.qos.logback"       % "logback-classic"      % logBackVersion % "runtime",
  "org.http4s"          %% "http4s-dsl"           % http4sVersion,
  "org.http4s"          %% "http4s-blaze-server"  % http4sVersion,
  "org.http4s"          %% "http4s-blaze-client"  % http4sVersion,
  "org.http4s"          %% "http4s-circe"         % http4sVersion,
  "io.circe"            %% "circe-generic"        % circeVersion,
  "io.circe"            %% "circe-generic-extras" % circeVersion,
  "io.circe"            %% "circe-literal"        % circeVersion,
//  "com.typesafe.slick"  %% "slick"                % slickVersion,
//  "com.typesafe.slick"  %% "slick-hikaricp"       % slickVersion,
//  "com.github.tminglei" %% "slick-pg"             % slickPgVersion,
  "org.specs2"          %% "specs2-core"          % specs2Version % "test",
  "io.chrisdavenport"   %% "cats-par"             % catsParVersion,
)

mainClass in (Compile, run) := Some("com.practicingtechie.Server")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
