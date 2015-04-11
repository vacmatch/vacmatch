

val spring = "org.springframework"

def getSpringBootDependencies(version:String) = {
  val bootorg = spring ++ ".boot"
  List(
    "data-jpa",
    "batch",
    "web",
    "thymeleaf",
    "actuator",
    "security",
    "jetty",
    "test"
  )
    .map("spring-boot-starter-" ++ _)
    .map(bootorg % _ % version)
}


val springBoot = getSpringBootDependencies("1.1.9.RELEASE")

val springTx = List("tx", "jdbc")
  .map("spring-" ++ _)
  .map(spring % _ % "4.0.8.RELEASE")


val ourDeps = Seq (
  "org.springframework" % "springloaded" % "1.2.3.RELEASE",
  "org.thymeleaf.extras" % "thymeleaf-extras-springsecurity3" % "2.1.1.RELEASE",
  "org.resthub" % "springmvc-router" % "1.2.0",
  "com.andersen-gott" %% "scravatar" % "1.0.3",
  "log4j" % "log4j" % "1.2.16"
) ++ springBoot ++ springTx ++ Seq(
  "org.xerial" % "sqlite-jdbc" % "3.8.7"
)

val testDeps = Seq(
  "info.cukes" %% "cucumber-scala" % "1.2.0" % "test",
  "info.cukes" % "cucumber-junit" % "1.2.0" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.1" % "test",
  "org.mockito" % "mockito-core" % "1.+" % "test",
  "org.pegdown" % "pegdown" % "1.4.2" % "test",
  "org.scoverage" %% "scalac-scoverage-plugin" % "1.0.2" % "test",
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.0.2" % "test"
)


lazy val root = (project in file("."))
  .settings(
    organization := "com.vac",
    version := "0.0.1",
    scalaVersion := "2.11.4",
    name := "manager",

    resolvers ++= Seq(
      Resolver.mavenLocal,
      Resolver.jcenterRepo,
      Resolver.sonatypeRepo("snapshots")
    ),

    libraryDependencies ++= ourDeps ++ testDeps
  )

// Enable WAR packaging
tomcat()

