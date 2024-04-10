package repositories

import db.DefaultQuillContext
import models.Rates

import scala.concurrent.{ExecutionContext, Future}


class RateRepositoryImpl(implicit ec: ExecutionContext) extends RateQueries with DefaultQuillContext
  with RateRepository {
  import ctx._
  override def listAllRates: Future[List[Rates]] = {
    ctx.run(rates)
  }

  override def findRateByLane(model: String, originZip: String, destinationZip: String, pickUpDate: String): Future[Option[Rates]] = {
    ctx.run(selectRateByLane(model, originZip, destinationZip, pickUpDate)).map(_.headOption)
  }

  override def createRates(rates: Rates): Future[Int] = {
    ctx.run(insertRate(rates)).map(_.toInt)
  }

  override def deleteRateByLane(model: String, originZip: String, destinationZip: String, pickUpDate: String): Future[Int] = {
    ctx.run(deleteRate(model, originZip, destinationZip, pickUpDate)).map(_.toInt)
  }

  override def updateRates(rates: Rates): Future[Int] = {
    val model = rates.model
    val originZip = rates.originZip
    val destinationZip = rates.destinationZip
    val pickUpDate = rates.pickUpDate
    ctx.run(updateRateRecord(rates, model, originZip, destinationZip, pickUpDate)).map(_.toInt)
  }
}






