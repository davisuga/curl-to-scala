package com.davisuga.curltoscala

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

// def curlToSttp = (s: String) => s

object CurlToScalaApp {

  // Var for the cURL command
  private val curlVar = Var("")

  // Var for the Scala code
  private val scalaVar = Var("")

  // Observer for the cURL command
  private val curlObserver = Observer[String] { curlCommand =>
    try {
      val scalaCode = curlToSttp(curlCommand)
      scalaVar.set(scalaCode)
    } catch {
      case e: Exception => scalaVar.set(e.getMessage)
    }
  }

  // The main view
  // The main view
  lazy val node: HtmlElement = {
    div(
      cls("container mx-auto p-4"),
      h1(cls("text-2xl mb-4 text-center font-bold"), "cURL to Scala Converter"),
      div(
        cls("grid grid-cols-1 md:grid-cols-2 gap-4"),
        div(
          cls("flex flex-col p-4 border rounded shadow"),
          label(cls("mb-2 font-semibold"), "cURL Command"),
          textArea(
            cls("border p-2 flex-grow resize-none"),
            rows := 10,
            inContext(thisNode =>
              onInput.mapTo(thisNode.ref.value) --> curlObserver
            )
          )
        ),
        div(
          cls("flex flex-col p-4 border rounded shadow"),
          label(cls("mb-2 font-semibold"), "Scala Code"),
          textArea(
            cls("border p-2 flex-grow resize-none"),
            rows := 10,
            readOnly(true),
            child.text <-- scalaVar.signal
          )
        )
      )
    )
  }
}

@main def run(): Unit =
  render(dom.document.getElementById("app"), CurlToScalaApp.node)
