package services

import models.Rates

trait ParsingService {
  def extractTruckRates(htmlString: String, originZip: String, destinationZip: String, pickUpDate: String): List[Rates]
}
