import router.Routes
import play.api.{BuiltInComponentsFromContext, LoggerConfigurator, ApplicationLoader}
import play.api.ApplicationLoader.Context

class WikiGrowthApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new WikiGrowthComponents(context).application
  }
}

class WikiGrowthComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  lazy val applicationController = new controllers.Application(environment)
  lazy val assets = new controllers.Assets(httpErrorHandler)
  // use router.Routes, generated by play
  override lazy val router = new Routes(httpErrorHandler, applicationController, assets)
}