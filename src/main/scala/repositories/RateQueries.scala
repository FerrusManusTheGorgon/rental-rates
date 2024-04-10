package repositories

import db.DefaultQuillContext
import models.Rates



trait RateQueries {
  this: DefaultQuillContext =>

  import ctx._

  def rates = quote(querySchema[Rates]("rates"))

  def selectRateByLane(model: String, originZip: String, destinationZip: String, pickUpDate: String) = quote {
    rates.filter(t =>
      t.model == lift(model) &&
        t.originZip == lift(originZip) &&
        t.destinationZip == lift(destinationZip) &&
        t.pickUpDate == lift(pickUpDate)
    )
  }

  def insertRate(newRates:Rates) = quote {
    rates.insert(lift(newRates))
  }


  def deleteRate(model: String, originZip: String, destinationZip: String, pickUpDate: String) = quote {
    rates.filter(t =>
      t.model == lift(model) &&
        t.originZip == lift(originZip) &&
        t.destinationZip == lift(destinationZip) &&
        t.pickUpDate == lift(pickUpDate)
    ).delete
  }

  def updateRateRecord(rates: Rates, model: String, originZip: String, destinationZip: String, pickUpDate: String) = quote {
    selectRateByLane(model, originZip, destinationZip, pickUpDate).update(lift(rates))
  }
}
