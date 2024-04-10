package services

trait CrawlerService {
  def findTruckRate(originZip: String, destinationZip: String, pickUpDate: String): String
}
