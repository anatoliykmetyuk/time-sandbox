val ScalaVer = "2.12.6"

val CatsEffect    = "1.0.0-RC3"
val Console4Cats  = "0.2"
val KindProjector = "0.9.7"

val JAnsi = "1.17.1"

lazy val commonSettings = Seq(
  name    := "Time Sandbox"
, version := "0.1.0"
, scalaVersion := ScalaVer
, libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % CatsEffect
  , "org.fusesource.jansi" % "jansi" % JAnsi
  )

, addCompilerPlugin("org.spire-math" %% "kind-projector" % KindProjector)
, scalacOptions ++= Seq(
      "-deprecation"
    , "-encoding", "UTF-8"
    , "-feature"
    , "-language:existentials"
    , "-language:higherKinds"
    , "-language:implicitConversions"
    , "-language:experimental.macros"
    , "-unchecked"
    // , "-Xfatal-warnings"
    // , "-Xlint"
    // , "-Yinline-warnings"
    , "-Ywarn-dead-code"
    , "-Xfuture"
    , "-Ypartial-unification")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    initialCommands := "import timesandbox._, Main._"
  )
