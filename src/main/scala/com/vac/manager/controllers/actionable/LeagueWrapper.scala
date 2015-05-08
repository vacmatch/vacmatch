package com.vac.manager.controllers.actionable

import com.vac.manager.model.competition.League
import scala.beans.BeanProperty
import com.vac.manager.controllers.utils.Hyperlink
import com.vac.manager.model.competition.LeagueSeason
import scala.util.Try
import scala.collection.JavaConverters._
import com.vacmatch.util.i18n.I18n
import com.vac.manager.controllers.utils.UrlGrabber
import org.springframework.beans.factory.annotation.Autowired

class LeagueWrapper(l: League, seasons: Seq[ActionableLeagueSeason], isAuthorized: Boolean)
    extends CrudLeague(l)
    with UrlGrabber {

  @Autowired
  var i: I18n = _

  val latest_season = Try(java.util.Collections.max(seasons.asJava, new java.util.Comparator[LeagueSeason] {
    override def compare(a: LeagueSeason, b: LeagueSeason): Int = {
      a.startTime.compareTo(b.startTime)
    }
  }))

  @BeanProperty
  val nonEmpty: Boolean = seasons.nonEmpty

  @BeanProperty
  val title: String = l.leagueName + " " +
    latest_season.map(_.id.seasonSlug)
    .getOrElse("(Not yet available)")

  @BeanProperty
  val mainLink = latest_season.map { season =>
    val text = "Classification"
    val href = "#classification" // TODO: Put classification here
    Hyperlink(text, href, "")
  }.getOrElse(Hyperlink("No action available", "#", "disabled"))

  @BeanProperty
  val links: java.util.List[Hyperlink] = latest_season.map { season =>
    Map(
      "Classification" -> "#classification",
      "Calendar" -> getUrl("GameController.list", "slug" -> l.slug, "year" -> season.id.seasonSlug),
      "Last matches played" -> "#matches",
      "Current match day" -> "#matchday"
    )
  }.toOption.toSeq.flatten.map {
    case (text, href) =>
      Hyperlink(text, href, "class")
  }.asJava

  @BeanProperty
  val authorizedLinks: java.util.List[Hyperlink] =
    if (isAuthorized)
      List(Hyperlink("Edit league", getEditLink, "btn-default"),
        Hyperlink("Delete league", getDeleteLink, "btn-default"),
        Hyperlink("Manage seasons", getSeasonAdminLink, "btn-default")).asJava
    else List().asJava

}
