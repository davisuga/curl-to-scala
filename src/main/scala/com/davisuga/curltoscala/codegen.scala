package com.davisuga.curltoscala

import fastparse._, SingleLineWhitespace._

sealed trait CurlPart
case class Method(value: String) extends CurlPart
case class Url(value: String) extends CurlPart
case class Header(name: String, value: String) extends CurlPart
case class Data(value: String) extends CurlPart

def methodP[$: P]: P[Method] = P(
  StringIn("-X", "--request").! ~ AnyChar.! ~ StringIn(
    "GET",
    "POST",
    "PUT",
    "DELETE"
  ).!
).map(x => Method(x._3))

def urlP[$: P]: P[Url] = P(CharsWhile(_ != ' ').!).map(Url(_))

def headerP[$: P]: P[Header] = P(
  StringIn("-H", "--header").! ~ AnyChar.! ~ "\"" ~ (!"\"" ~ AnyChar).!.map(
    _.split(": ", 2)
  ) ~ "\""
).map { case header =>
  Header(header(0), header(1))
}
def dataP[$: P]: P[Data] = P(
  StringIn("-d", "--data").! ~ AnyChar.! ~ "\"" ~ (!"\"" ~ AnyChar).! ~ "\""
).map(x => Data(x._2))

def curlP[$: P]: P[Seq[CurlPart]] =
  P(methodP.? ~ urlP ~ headerP.rep ~ dataP.?).map {
    case (methodOpt, url, headers, dataOpt) =>
      methodOpt.toSeq ++ Seq(url) ++ headers ++ dataOpt.toSeq
  }

def curlToSttp(curlCommand: String): String = {
  parse(curlCommand, curlP(_)) match {
    case Parsed.Success(parts, _) =>
      val method = parts.collectFirst { case Method(m) => m }.getOrElse("GET")
      val url = parts.collectFirst { case Url(u) => u }.getOrElse("")
      val headers = parts.collect { case Header(name, value) =>
        s""".header("$name", "$value")"""
      }.mkString
      val data = parts
        .collectFirst { case Data(d) => d }
        .map(d => s""".body("$d")""")
        .getOrElse("")

      s"""basicRequest.$method(uri"$url")$headers$data.send()"""
    case Parsed.Failure(m1, pos, loc) =>
      throw new IllegalArgumentException(
        s"Invalid cURL command: ${m1} at ${pos}"
      )
  }
}
