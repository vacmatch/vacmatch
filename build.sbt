

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
  "org.apache.tomcat.embed" % "tomcat-embed-core" % "7.0.53" % "container",
  "org.apache.tomcat.embed" % "tomcat-embed-logging-juli" % "7.0.53" % "container",
  "org.apache.tomcat.embed" % "tomcat-embed-jasper" % "7.0.53" % "container",
  "org.springframework.boot" % "spring-boot-starter-tomcat" % "1.1.9.RELEASE" % "provided",
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
  "com.vacmatch.util" %% "i18n-gettext" % "0.2.1-SNAPSHOT",
  "org.thymeleaf.extras" % "thymeleaf-extras-springsecurity3" % "2.1.1.RELEASE",
  "org.resthub" % "springmvc-router" % "1.2.0",
  "com.andersen-gott" %% "scravatar" % "1.0.3",
  "org.apache.commons" % "commons-dbcp2" % "2.1",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
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

    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),

  resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.url("vacmatch-https-repo") ivys "https://ci.corp.vacmatch.com/userContent/ivy-repo/com/vacmatch/util/[module](_[scalaVersion])/[revision]/ivy-[revision].xml" artifacts "https://ci.corp.vacmatch.com/userContent/ivy-repo/com/vacmatch/util/[module](_[scalaVersion])/[revision]/[artifact](_[scalaVersion])(-[revision]).[ext]",
    // Resolver.ssh("vacmatch-ssh-repo", "corp.vacmatch.com") as("repo", Path.userHome / ".ssh" / "id_rsa") ivys "ssh://corp.vacmatch.com:/srv/docker/jenkins/home/userContent/ivy-repo/com/vacmatch/util/[module](_[scalaVersion])/[revision]/ivy-[revision].xml" artifacts "ssh://corp.vacmatch.com:/srv/docker/jenkins/home/userContent/ivy-repo/com/vacmatch/util/[module](_[scalaVersion])/[revision]/[artifact](_[scalaVersion])(-[revision]).[ext]",
    Resolver.jcenterRepo,
    Resolver.sonatypeRepo("snapshots")
  ),

  libraryDependencies ++= ourDeps ++ testDeps,

  webInfClasses in webapp := true,

  autoCompilerPlugins := true,

  addCompilerPlugin("tv.cntt" %% "xgettext" % "1.3"),

  scalacOptions :=
      scalacOptions.value :+ ("-P:xgettext:com.vacmatch.util.i18n.I18n")
  )

// Enable WAR packaging
tomcat()

scalariformSettings

import scalariform.formatter.preferences._
ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(PreserveDanglingCloseParenthesis, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)

