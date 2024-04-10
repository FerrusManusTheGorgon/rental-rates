package services

trait BudgetCrawlerService {
  def findBudgetTruckRate(originZip: String, destinationZip: String, pickUpDate: String): String
}

