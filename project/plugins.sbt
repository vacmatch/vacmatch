
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

// Use spring-loaded for hot-reloading class files
addSbtPlugin("me.browder" % "sbt-spring-loaded" % "0.2.0")

// Scoverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

// WAR
addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "1.1.0")

// Scapegaoat
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "0.94.6")

