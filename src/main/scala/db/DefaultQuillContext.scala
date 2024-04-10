package db

import io.getquill.{PostgresAsyncContext, SnakeCase}

trait DefaultQuillContext {
  lazy val ctx = new PostgresAsyncContext(SnakeCase, "ctx")
}
