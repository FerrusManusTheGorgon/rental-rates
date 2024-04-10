package jobs

import scala.concurrent.Future

trait RateCrawler {

  def loadRateData(): Future[Unit]

}
