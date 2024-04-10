package repositories

import models.Rates

import scala.concurrent.Future

trait RateRepository {
  def listAllRates: Future[List[Rates]]

  def findRateByLane(model: String, originZip: String, destinationZip: String, pickUpDate: String): Future[Option[Rates]]

  def createRates(rates: Rates): Future[Int]

  def deleteRateByLane(model: String, originZip: String, destinationZip: String, pickUpDate: String): Future[Int]

  def updateRates(rates: Rates): Future[Int]

}
