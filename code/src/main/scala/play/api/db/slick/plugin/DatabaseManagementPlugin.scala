package play.api.db.slick.plugin

import play.api.{Application, Plugin}

class DatabaseManagementPlugin(app: Application) extends Plugin {
  override def enabled = true

  override def onStart(): Unit = {
  }
}