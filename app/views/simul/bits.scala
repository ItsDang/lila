package views.html.simul

import controllers.routes
import play.api.i18n.Lang

import lila.app.templating.Environment.{*, given}
import lila.app.ui.ScalatagsTemplate.{ *, given }

object bits:

  def link(simulId: lila.simul.SimulId): Frag =
    a(href := routes.Simul.show(simulId))("Simultaneous exhibition")

  def jsI18n()(using Lang) = i18nJsObject(baseTranslations)

  def notFound()(using PageContext) =
    views.html.base.layout(title = trans.noSimulFound.txt()):
      main(cls := "page-small box box-pad")(
        h1(cls := "box__top")(trans.noSimulFound()),
        p(trans.noSimulExplanation()),
        p(a(href := routes.Simul.home)(trans.returnToSimulHomepage()))
      )

  def homepageSpotlight(s: lila.simul.Simul)(using Context) =
    a(href := routes.Simul.show(s.id), cls := "tour-spotlight little")(
      img(cls := "img icon", src := assetUrl("images/fire-silhouette.svg")),
      span(cls := "content")(
        span(cls := "name")(s.name, " simul"),
        span(cls := "more")(
          trans.nbPlayers.plural(s.applicants.size, s.applicants.size.localize),
          " • ",
          trans.join()
        )
      )
    )

  def allCreated(simuls: Seq[lila.simul.Simul], withName: Boolean = true)(using Lang) =
    table(cls := "slist"):
      simuls.map: simul =>
        val url = routes.Simul.show(simul.id)
        tr(
          withName.option(td(cls := "name")(a(href := url)(simul.fullName))),
          td:
            if withName then userIdLink(simul.hostId.some)
            else a(href := url)(userIdSpanMini(simul.hostId, true))
          ,
          td(cls := "text", dataIcon := licon.Clock)(simul.clock.config.show),
          td(cls := "text", dataIcon := licon.User)(simul.applicants.size)
        )

  private[simul] def setup(sim: lila.simul.Simul) =
    span(cls := List("setup" -> true, "rich" -> sim.variantRich))(
      sim.clock.config.show,
      " • ",
      sim.variants.map(_.name).mkString(", ")
    )

  private val baseTranslations = Vector(
    trans.finished,
    trans.withdraw,
    trans.join,
    trans.cancel,
    trans.joinTheGame,
    trans.nbPlaying,
    trans.nbWins,
    trans.nbDraws,
    trans.nbLosses,
    trans.by,
    trans.signIn,
    trans.mustBeInTeam
  )
