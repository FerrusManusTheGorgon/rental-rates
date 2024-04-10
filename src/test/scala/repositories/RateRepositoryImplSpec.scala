package repositories

import com.typesafe.scalalogging.LazyLogging
import db.DbSetup
import models.Rates
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.time.{Minute, Span}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class RateRepositoryImplSpec extends AnyFunSpec with LazyLogging with ScalaFutures with DbSetup {
  lazy val repository = {
    logger.info("the repository is now")
    new RateRepositoryImpl()
  }

  describe("rate") {
    it("should insert a record") {
      val testRates = Rates(
        model = "TM",
        amount = "1,843.00",
        pickUpDate = "06/22/2023",
        originZip = "85015",
        destinationZip = "54115"
      )
      val test = repository.createRates(testRates)
      whenReady(test, timeout = Timeout(Span(1, Minute))) { numberOfRowsInserted =>
        assert(numberOfRowsInserted == 1)
      }
    }

    it("should read a rate") {
      val model = "TM"
      val originZip = "85015"
      val destinationZip = "54115"
      val pickUpDate = "06/22/2023"

      val test = repository.findRateByLane(model, originZip, destinationZip, pickUpDate)
      whenReady(test, timeout = Timeout(Span(1, Minute))) { rateO =>
        assert(rateO.isDefined)
        rateO.map { rates =>
          assert(rates.model == "TM")
          assert(rates.amount == "1,843.00")
          assert(rates.pickUpDate == "06/22/2023")
          assert(rates.originZip == "85015")
          assert(rates.destinationZip == "54115")
        }
      }
    }
    it("should update a rate") {
      val updateTestRate = Rates(
        model = "TM",
        amount = "1,843.00",
        pickUpDate = "06/22/2023",
        originZip = "85015",
        destinationZip = "54115"
      )
      val test = repository.updateRates(updateTestRate)
      whenReady(test, timeout = Timeout(Span(1, Minute))) { numberOfRowsUpdated =>
        assert(numberOfRowsUpdated == 1)
      }
    }
      it("should delete a rate"){
        val model = "TM"
        val originZip = "85015"
        val destinationZip = "54115"
        val pickUpDate = "06/22/2023"

        val test = repository.deleteRateByLane(model, originZip, destinationZip, pickUpDate)
        whenReady(test, timeout = Timeout(Span(1, Minute))) { numberOfRowsDeleted =>
          assert(numberOfRowsDeleted == 1)
        }
      }
    }
  }


