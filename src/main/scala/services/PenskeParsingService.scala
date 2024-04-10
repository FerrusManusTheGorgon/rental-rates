package services

import models.Rates

trait PenskeParsingService {
  def extractPenskeTruckRates(pageSource: String, originZip: String, destinationZip: String, pickUpDate: String): List[Rates]
}


