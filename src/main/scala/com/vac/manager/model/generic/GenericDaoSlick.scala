package com.vac.manager.model.generic

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.lifted._

trait GenericDaoSlick[E, K] extends GenericDao[E, K] {
  type T = AbstractTable[E]
  type F = AbstractTable[E]#TableElementType

  protected val driver: slick.driver.JdbcProfile
  import driver.api._

  protected val db: Database

  def synchronously[X](f: Future[X]): X = {
    scala.concurrent.Await.result(f, scala.concurrent.duration.Duration(1, "second"))
  }

  // /**
  //   * Find all objects from EntityClass table
  //   */
  // override def findAll(): Seq[E] = {
  //   synchronously(db.run(entities.to[Seq].result))
  // }

  // /**
  //   * Save or update entity
  //   */
  // override def save(entity: F): F = {
  //   entities += entity
  //   entity
  // }

  // override def remove(entity: F): Unit
}
