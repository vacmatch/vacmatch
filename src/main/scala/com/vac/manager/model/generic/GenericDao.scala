package com.vac.manager.model.generic

import scala.collection.mutable.Buffer
import java.io.Serializable

/**
  * Data Access Object (DAO) interface.
  */
trait GenericDao[T, K <: Serializable] {

  /**
    * Find all items from DB table
    */
  def findAll(): List[T]

  /**
    * Insert a new row or update it if it has been already persisted
    * @param obj The entity object to be persisted or updated
    */
  def save(obj: T): Unit

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
  def findById(id: K): T

}
