package services

import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import java.time.{Duration, LocalDate, Month}
import java.util.{Calendar, Random}
import java.time.format.DateTimeFormatter
import org.openqa.selenium.firefox.FirefoxDriver
import java.time.temporal.ChronoUnit

class PenskeCrawlerServiceImpl extends PenskeCrawlerService {
  lazy val random = new Random()

  override def findPenskeTruckRate(originZip: String, destinationZip: String, pickUpDate: String): String = {


    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val parsedDate = LocalDate.parse(pickUpDate, formatter)


    val pickUpMonth = parsedDate.getMonth
    val pickUpYear = parsedDate.getYear
    val pickUpDay = parsedDate.getDayOfMonth
    val pickUpMonthFormatted = parsedDate.getMonth.toString.toLowerCase.capitalize


    val formattedPickUpDate = pickUpDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    println(s"Formatted pick-up date: $formattedPickUpDate")


    //    val driver = new ChromeDriver()
    val driver = new FirefoxDriver()
    driver.get("https://www.pensketruckrental.com/")
    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000))
    // Wait for 1 seconds
    Thread.sleep(4000)


    val closeButton = driver.findElement(By.id("onetrust-close-btn-container"))
    closeButton.click()

    Thread.sleep(4000)
    199
    try {
      val dropOffLocationLabel = driver.findElement(By.cssSelector("div.rental-widget-container:nth-child(2) > div:nth-child(2) > div:nth-child(1) > section:nth-child(1) > form:nth-child(1) > div:nth-child(7) > fieldset:nth-child(1) > div:nth-child(1) > div:nth-child(1)"))
      dropOffLocationLabel.getAttribute("class") match {
        case "heading hidden" =>
          // Remove the "hidden" class from the label to make it visible
          driver.executeScript("arguments[0].classList.remove('hidden');", dropOffLocationLabel)

        case _ => // Label is already visible
      }
    } catch {
      case e: Exception => println("Drop-off location label not found")
    }

    // Now find and interact with the input field
    try {


      val dropOffLocationInput = driver.findElement(By.cssSelector("div.rental-widget-container:nth-child(2) > div:nth-child(2) > div:nth-child(1) > section:nth-child(1) > form:nth-child(1) > div:nth-child(7) > fieldset:nth-child(1) > div:nth-child(1) > div:nth-child(1) > input:nth-child(3)"))
      //            typeWithRandomDelay(dropOffLocationInput, destinationZip)
      //    dropOffLocationInput.sendKeys("85015")

      if (dropOffLocationInput.isDisplayed() && dropOffLocationInput.isEnabled()) {
        println("Drop-off location input element is visible and clickable")

        // Type into the input field and interact with it as needed
        typeWithRandomDelay(dropOffLocationInput, destinationZip)
      } else {
        println("Drop-off location input element not visible or not clickable")
      }
    } catch {
      case e: Exception => println("Drop-off location input element not found")
    }


    /// Lets go
    try {


      val pickUpLocationInput = driver.findElement(By.cssSelector("div.rental-widget-container:nth-child(2) > div:nth-child(2) > div:nth-child(1) > section:nth-child(1) > form:nth-child(1) > div:nth-child(6) > fieldset:nth-child(1) > div:nth-child(1) > div:nth-child(1) > input:nth-child(3)"))
      //            typeWithRandomDelay(dropOffLocationInput, destinationZip)
      //    dropOffLocationInput.sendKeys("85015")

      if (pickUpLocationInput.isDisplayed() && pickUpLocationInput.isEnabled()) {
        println("Drop-off location input element is visible and clickable")

        // Type into the input field and interact with it as needed
        typeWithRandomDelay(pickUpLocationInput, originZip)
      } else {
        println("Pick-Up location input element not visible or not clickable")
      }
    } catch {
      case e: Exception => println("Pick-Up location input element not found")
    }


    Thread.sleep(4000)


    try {
      val dateInput = driver.findElement(By.cssSelector("div.rental-widget-container:nth-child(2) > div:nth-child(2) > div:nth-child(1) > section:nth-child(1) > form:nth-child(1) > div:nth-child(8)"))

      if (dateInput.isDisplayed() && dateInput.isEnabled()) {
        println("dateInput element is visible and clickable")

        // Click the element
        dateInput.click()

        // Wait for a few seconds (e.g., 5 seconds)
        Thread.sleep(5000) // Adjust the sleep time as needed

        // Click the element again
        dateInput.click()
      } else {
        println("dateInput element not visible or not clickable")
      }
    } catch {
      case e: Exception => println("dateInput element not found")
    }

    // Wait for the "Next" button to be clickable
    val waitDriver = new WebDriverWait(driver, Duration.ofSeconds(10))
    val nextButton = waitDriver.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span.navigator:nth-child(3) > img:nth-child(2)")))


    // Month Clicker

    // Calculate the number of clicks required to reach the desired month
    val desiredMonth = Month.valueOf(pickUpMonth.toString.toUpperCase)
    val desiredYear = pickUpYear //2023
    val currentMonth = LocalDate.now().getMonth
    val currentYear = LocalDate.now().getYear
    val clicksRequired = ChronoUnit.MONTHS.between(LocalDate.of(currentYear, currentMonth, 1), LocalDate.of(desiredYear, desiredMonth, 1))

    var clickCount = 0

    while (clickCount < clicksRequired) {
      val nextButton = driver.findElement(By.cssSelector("span.navigator:nth-child(3) > img:nth-child(2)"))
      clickWithRandomDelay(nextButton, 500, 1000) // Set your desired delay range here (in milliseconds)
      clickCount += 1
    }

    def getDayIndexOnMonthGrid(calendar: Calendar, day: Int): Int = {
      // Get the maximum number of days in the month
      val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

      // Get the day of the week for the first day of the month (1 = Sunday, 2 = Monday, etc.)
      calendar.set(Calendar.DAY_OF_MONTH, 1)
      val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

      // Calculate the index of the day in the month grid
      val index = (day - 1 + firstDayOfWeek)


      index
    }

    // Create a Calendar instance and set it to the desired month
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, 2023) // Set the year
    //  calendar.set(Calendar.MONTH, Calendar.NOVEMBER) // Set the month (e.g., September)
    calendar.set(Calendar.MONTH, pickUpMonth.getValue - 1) // Set the month (subtract 1 because Calendar months are 0-based)
    // Get the index of the day in the month grid
    val dayIndex = getDayIndexOnMonthGrid(calendar, pickUpDay) // Replace 5 with the day you want to find the index of

    // Print the result
    println(s"Index of the day in the month grid: $dayIndex")


    val xpath = s"month.m-4:nth-child(1) > div:nth-child(3) > date:nth-child($dayIndex) > div:nth-child(1) > span:nth-child(1)"
    println("bbbbbbbbbbbbbbbbbbbbbbbbb")

    val elements = driver.findElements(By.cssSelector(xpath))
    if (elements.size > 0) {
      println("Element(s) found")

      // Click the first element if it exists
      val firstElement = elements.get(0) // Access the first element (index 0)
      firstElement.click()
    } else {
      // No element found
      println("Element not found.")
    }


    // Find the <button> element using id
    val buttonElement: WebElement = driver.findElement(By.cssSelector(".mat-focus-indicator"))

    // Click on the <button> element
    buttonElement.click()


    Thread.sleep(20000)


    // Find the button element by XPath
    val buttonElement2: WebElement = driver.findElement(By.cssSelector("div.rental-widget-container:nth-child(2) > div:nth-child(2) > div:nth-child(1) > section:nth-child(1) > form:nth-child(1) > div:nth-child(10) > div:nth-child(1) > div:nth-child(1) > button:nth-child(1)"))

    // Click on the <button> element
    buttonElement2.click()


    // Wait for the last page to load completely
    val waitObject = new WebDriverWait(driver, Duration.ofSeconds(30))
    waitObject.until(ExpectedConditions.urlContains("reservation-quote"))


    Thread.sleep(5000)
    try {
      // Extract everything from the page
      val pageSource = driver.getPageSource()
      println(pageSource)
      // Close the browser window outside the loop
      driver.quit()
    } catch {
      case e: Exception => println("Error while getting page source: " + e.getMessage)
    } finally {
      try {
        // Close the browser
        //      driver.quit()
      } catch {
        case e: Exception => println("Error while closing the browser: " + e.getMessage)
      }
    }


    try {
      // Extract everything from the page
      val pageSource = driver.getPageSource()
      println(pageSource)
      pageSource // Return the page source
    } catch {
      case e: Exception =>
        println("Error while getting page source: " + e.getMessage)
        "" // Return an empty string in case of exception
    } finally {
      try {
        // Close the browser
        // driver.quit()
      } catch {
        case e: Exception =>
          println("Error while closing the browser: " + e.getMessage)
      }
    }

  }

  def typeWithRandomDelay(element: org.openqa.selenium.WebElement, text: String): Unit = {
    for (c <- text.toCharArray) {
      element.sendKeys(c.toString)
      Thread.sleep(random.nextInt(550) + 150) // Random wait between 100ms and 399ms
    }
  }

  // Function to click on an element with a random delay
  def clickWithRandomDelay(element: WebElement, minDelay: Int, maxDelay: Int): Unit = {
    val delay = random.nextInt(maxDelay - minDelay + 1) + minDelay
    Thread.sleep(delay)
    element.click()
  }
}


