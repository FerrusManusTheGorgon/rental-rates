package services

import models.Rates

trait PenskeCrawlerService {
  def findPenskeTruckRate(originZip: String, destinationZip: String, pickUpDate: String): String
}


