publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:playframework/play-slick.git</url>
    <connection>scm:git:git@github.com:playframework/play-slick.git</connection>
  </scm>
  <developers>
    <developer>
      <id>freekh</id>
      <name>Fredrik Ekholdt</name>
      <url>http://ch.linkedin.com/in/freekh</url>
    </developer>
    <developer>
      <id>ms-tg</id>
      <name>Marc Siegel</name>
      <email>marc.siegel@timgroup.com</email>
      <url>https://github.com/ms-tg</url>
    </developer>
    <developer>
      <id>loicd</id>
      <name>Loic Descotte</name>
      <email>loic.descotte@gmail.com</email>
      <url>https://github.com/loicdescotte</url>
    </developer>
    <developer>
       <id>cvogt</id>
       <name>Jan Christopher Vogt</name>
       <email>play-slick.nsp@cvogt.org</email>
       <url>https://github.com/cvogt</url>
    </developer>
  </developers>
)

credentials in ThisBuild += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := {
  if (isSnapshot.value)
    Some("WiredThing forked libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-forked-local")
  else
    Some("WiredThing forked libraries Repository" at "https://wiredthing.artifactoryonline.com/wiredthing/libs-forked-local")
}
