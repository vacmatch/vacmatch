package com.vac.manager.util

object Sugar {
  def splitEither[A, B](el: Seq[Either[A, B]]) = {
    val (l, r) = el.partition(_.isLeft)

    (l.map(_.left.get), r.map(_.right.get))
  }
}
