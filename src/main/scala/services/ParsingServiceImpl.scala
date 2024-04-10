package services

import models.Rates



  class ParsingServiceImpl
  extends ParsingService {
    override def extractTruckRates(htmlString: String, originZip: String, destinationZip: String, pickUpDate: String): List[Rates] = {
      val regex = """<form class="collapse" id="formProcessRequest_(.*?)".*?>(?:.|[\r\n])*?\$([\d,.]+)""".r
      val matches = regex.findAllMatchIn(htmlString)

      val ratesList = matches.flatMap { matchResult =>
        val modelCode = matchResult.group(1)
        val amount = matchResult.group(2)
        List(Rates(
          model = modelCode,
          amount = amount,
          pickUpDate = pickUpDate,
          originZip = originZip,
          destinationZip = destinationZip
        ))
      }.toList

      ratesList
    }
  }





