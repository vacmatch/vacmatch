package com.vac.manager.controllers.utils

import org.scalatest.FlatSpec

class UrlGrabberTest extends FlatSpec {

  "An empty params list" should "render an empty map" in {
    assert(UrlGrabber.getParams(List()).size() == 0)
  }

  "A fixed two-element params list" should "render a one element map" in {
    val jMap = UrlGrabber.getParams(List("hello" -> "world", "hi" -> "there"))

    println(jMap)

    assert(jMap.size() == 2)
    assert(jMap.get("hello") == "world")
    assert(jMap.get("hi") == "there")
  }

  "A two-elem param list with the same param" should "return a list" in {
    val jMap = UrlGrabber.getParams(List(
      "hello" -> "world",
      "hello" -> "Mr. Reese"
    ))

    println(jMap)

    assert(jMap.size() == 1)
    assert(jMap.get("hello").asInstanceOf[java.util.List[_]].size() == 2)
  }

  "A two-elem param list already baked" should "be left intact" in {
    val l = new java.util.ArrayList[String]()
    l.add("world")
    l.add("someone")

    val jMap = UrlGrabber.getParams(List("hello" -> l))

    assert(jMap.size() == 1)
    assert(jMap.get("hello").asInstanceOf[java.util.List[_]].size == 2)

  }

  "A two-elem param list which is already a scala type" should "be transformed into a java List" in {
    val l: List[_] = List("world", "someone")

    val jMap = UrlGrabber.getParams(List("hello" -> l))

    assert(jMap.size() == 1)
    assert(jMap.get("hello").asInstanceOf[java.util.List[_]].size == 2)
    println("MY TWO_ELEM IS " + jMap.get("hello"))
  }

}
