package com.vac.manager.model.federation.daoslick

import com.vac.manager.model.federation._
import com.vac.manager.model.generic.GenericDaoSlick
import javax.inject.Named
import org.springframework.context.annotation.Primary

import scala.collection.JavaConverters._
import scala.concurrent.Future
import slick.driver._

class FederationDaoSlick(val driver: slick.driver.JdbcProfile, val db: slick.jdbc.JdbcBackend.DatabaseDef)
    extends GenericDaoSlick[Federation, Long]
    with FederationDao {
  import driver.api._

  class Federations(tag: Tag) extends Table[Federation](tag, "federation") {
    def fedId = column[Long]("fedid", O.PrimaryKey, O.AutoInc)
    def fedName = column[String]("fed_name")

    def * = (fedId.?, fedName) <> (Federation.tupled, Federation.unapply)

    def fedIdx = index("uk_name", fedName, unique = true)
  }
  protected val federations = TableQuery[Federations]

  class FederationDomainNames(tag: Tag) extends Table[FederationDomainName](tag, "federation_domain_name") {
    def dns = column[String]("dns", O.PrimaryKey, O.AutoInc)
    def fedId = column[Long]("fedid")

    def * = (dns, fedId) <> (FederationDomainName.tupled, FederationDomainName.unapply)

    def fed = foreignKey("FEDDNS", fedId, federations)(_.fedId)
  }
  protected val federationDomainNames = TableQuery[FederationDomainNames]

  def findByName(fedName: String): Option[Federation] = {
    val fed = federations.filter(_.fedName === fedName)
    val f: Future[Seq[Federation]] = db.run(fed.result)
    synchronously(f).headOption
  }

  def findByDomainName(dns: String): Option[Federation] = {
    val fed = for {
      (d, f) <- federationDomainNames.filter(_.dns === dns) join federations on (_.fedId === _.fedId)
    } yield f
    val f = db.run(fed.result)
    synchronously(f).headOption
  }

  def findDomainNames(fedId: Long): Seq[String] = {
    synchronously(db.run(
      federationDomainNames.filter(_.fedId === fedId).map(_.dns).result
    ))
  }

  def findDomainNamesAsEntity(fedId: Long): Seq[FederationDomainName] = {
    synchronously(db.run(
      federationDomainNames.filter(_.fedId === fedId).result
    ))
  }

  def saveDomainName(entity: FederationDomainName) = {
    synchronously(db.run(
      federationDomainNames += entity
    ))
  }

  def removeDomainName(entity: FederationDomainName) = {
    synchronously(db.run(
      federationDomainNames.filter(_.dns === entity.dns).delete
    ))
  }

  override def findAll(): Seq[Federation] = {
    synchronously(db.run(
      federations.result
    ))
  }

  override def findById(id: Long): Option[Federation] = {
    synchronously(db.run(
      federations.filter(_.fedId === id).result
    )).headOption
  }

  override def save(entity: Federation) = {
    synchronously(db.run(
      (federations += entity)
    ))

    entity
  }

  override def remove(entity: Federation) = {
    synchronously(db.run(
      federations.filter(_.fedId === entity.fedId).delete
    ))
  }
}

