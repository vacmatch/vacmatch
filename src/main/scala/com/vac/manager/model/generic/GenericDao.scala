package com.vac.manager.model.generic

import scala.collection.mutable.Buffer

/**
  * Data Access Object (DAO) interface.
  */
trait GenericDao[T, K] {

  /**
    * Find all items from DB table
    */
  def findAll(): Seq[T]

  /**
    * Insert a new row or update it if it has been already persisted
    * @param obj The entity object to be persisted or updated
    */
  def save(obj: T): T

  /**
    * Delete row in DB table
    * @param obj The entity object to be deleted
    */
  def remove(obj: T): Unit

  /**
    * Find item by id
    * @param id The id from the object to be found
    * @param query The name of the query to be executed
    */
  def findById(id: K): Option[T]

}
