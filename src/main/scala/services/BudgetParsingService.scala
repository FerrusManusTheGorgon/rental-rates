package services

import models.Rates

trait BudgetParsingService {
 def extractBudgetTruckRates(pageSource: String, pickUpDate: String, originZip: String, destinationZip: String): List[Rates]
}




