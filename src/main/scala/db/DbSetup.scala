package db

import org.flywaydb.core.Flyway
import scala.jdk.CollectionConverters._


trait DbSetup {
  val dbConfig = "jdbc:postgresql://localhost:5433/postgres?user=postgres&password=root"

  def dbSetup(): Unit = {
    val flyway = Flyway.configure()
      .configuration(Map("flyway.locations" -> "filesystem:/Users/seanyoung/projects/selenium-test/src/main/resources/migrations").asJava)
      .dataSource(dbConfig, "postgres", null)
      .load()
    flyway.migrate()
  }
}




