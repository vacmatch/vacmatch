package com.vac.manager.controllers.actionable

import com.vac.manager.model.competition.Competition
import scala.beans.BeanProperty
import com.vac.manager.controllers.utils.Hyperlink
import com.vac.manager.model.competition.CompetitionSeason
import scala.util.Try
import scala.collection.JavaConverters._
import com.vacmatch.util.i18n.I18n
import com.vac.manager.controllers.utils.UrlGrabber
import org.springframework.beans.factory.annotation.Autowired

class CompetitionWrapper(l: Competition, seasons: Seq[ActionableCompetitionSeason], isAuthorized: Boolean)
    extends CrudCompetition(l)
    with UrlGrabber {

  @Autowired
  var i: I18n = _

  val latest_season = Try(java.util.Collections.max(seasons.asJava, new java.util.Comparator[CompetitionSeason] {
    override def compare(a: CompetitionSeason, b: CompetitionSeason): Int = {
      a.startTime.compareTo(b.startTime)
    }
  }))

  @BeanProperty
  val nonEmpty: Boolean = seasons.nonEmpty

  @BeanProperty
  val title: String = l.competitionName + " " +
    latest_season.map(_.id.seasonSlug)
    .getOrElse("(Not yet available)")

  @BeanProperty
  val mainLink = latest_season.map { season =>
    val text = "Classification"
    val href = getUrl("GameController.showClassification", "slug" -> l.slug, "year" -> season.id.seasonSlug) // TODO: Put classification here
    Hyperlink(text, href, "")
  }.getOrElse(Hyperlink("No action available", "#", "disabled"))

  @BeanProperty
  val links: java.util.List[Hyperlink] = latest_season.map { season =>
    Map(
      "Classification" -> getUrl("GameController.showClassification", "slug" -> l.slug, "year" -> season.id.seasonSlug),
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
      List(
        Hyperlink("Edit competition", getEditLink, "btn-default"),
        Hyperlink("Delete competition", getDeleteLink, "btn-default"),
        Hyperlink("Manage seasons", getSeasonAdminLink, "btn-default")
      ).asJava
    else List().asJava

}
