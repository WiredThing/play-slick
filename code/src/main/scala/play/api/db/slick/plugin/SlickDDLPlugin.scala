package play.api.db.slick.plugin

import play.api.{Logger, Application, Plugin, Mode}
import play.api.libs.Files
import scala.Option.option2Iterable
import scala.annotation.tailrec
import play.api.db.slick.ddl.TableScanner
import play.api.db.slick.ddl.SlickDDLException
import play.api.db.slick.Config

class SlickDDLPlugin(app: Application) extends Plugin {

  private val configKey = "slick3"

  private def isDisabled: Boolean = app.configuration.getString("evolutionplugin").map(_ == "disabled").headOption.getOrElse(false)

  override def enabled = !isDisabled

  override def onStart(): Unit = {
    val conf = app.configuration.getConfig(configKey)
    conf.foreach { conf =>
      conf.entrySet.foreach { case (k,v) => println(s"$k=$v")}
      conf.keys.foreach { key =>
        Logger.debug(s"Checking evo for $key")
        val packageNames = conf.getString(key).getOrElse(throw conf.reportError(key, "Expected key " + key + " but could not get its values!", None)).split(",").toSet
        Logger.debug(s"package names: $packageNames")
        if (app.mode != Mode.Prod) {
          val evolutionsEnabled = !"disabled".equals(app.configuration.getString("evolutionplugin"))
          if (evolutionsEnabled) {
            val evolutions = app.getFile("conf/evolutions/" + key + "/1.sql");
            if (!evolutions.exists() || Files.readFile(evolutions).startsWith(CreatedBy)) {
              try {
                evolutionScript(key, packageNames)(app).foreach { evolutionScript =>
                  Files.createDirectory(app.getFile("conf/evolutions/" + key));
                  Files.writeFileIfChanged(evolutions, evolutionScript);
                }
              } catch {
                case e: SlickDDLException => throw conf.reportError(key, e.message, Some(e))
              }
            }
          }
        }
      }
    }
  }

  private val CreatedBy = "# --- Created by "

  def evolutionScript(driverName: String, names: Set[String])(app: Application): Option[String] = {
    Logger.debug(s"evolutionScript called with driverName '$driverName'")
    val driver = Config.driver(driverName)(app)
    val ddls = TableScanner.reflectAllDDLMethods(names, driver, app.classloader)

    val delimiter = ";" //TODO: figure this out by asking the db or have a configuration setting?

    if (ddls.nonEmpty) {     
      val ddl = ddls
          .toSeq.sortBy(a => a.createStatements.mkString ++ a.dropStatements.mkString) //sort to avoid generating different schemas
          .reduceLeft((a, b) => a.asInstanceOf[driver.SchemaDescription] ++ b.asInstanceOf[driver.SchemaDescription])

      Some(CreatedBy + "Slick DDL\n" +
        "# To stop Slick DDL generation, remove this comment and start using Evolutions\n" +
        "\n" +
        "# --- !Ups\n\n" +
        ddl.createStatements.mkString("", s"$delimiter\n", s"$delimiter\n") +
        "\n" +
        "# --- !Downs\n\n" +
        ddl.dropStatements.mkString("", s"$delimiter\n", s"$delimiter\n") +
        "\n")
    } else None
  }

}
