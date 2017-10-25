lazy val sparkVersion = "2.0.0"
val framelessVersion = "0.3.0"

lazy val root = project

organization := "com.github.shokohara"
scalaVersion := "2.11.8"
name := "titanic"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-streaming" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-mllib" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-hive" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-tags" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-mllib-local" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-network-common" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-graphx" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-catalyst" % sparkVersion % Provided,
  "org.typelevel" %% "frameless-cats" % framelessVersion,
  "org.typelevel" %% "frameless-dataset" % framelessVersion
)
run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated
mainClass := Option("com.github.shokohara.titanic.Main")
mainClass in assembly := Option("com.github.shokohara.titanic.Main")
test in assembly := {}
assemblyJarName in assembly := "main.jar"
