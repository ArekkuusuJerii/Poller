package net.cinnamon.repository

import java.util

import net.cinnamon.controller.MenuController
import net.cinnamon.entity.Question
import net.cinnamon.helper.SequenceHelper.call
import net.cinnamon.repository.PollImpl.Token
import net.cinnamon.repository.helper.->

object StatisticImpl {
  def getSelectionStatistics(term: String, token: Token, question: Question): java.util.List[(String, Integer)] = {
    val buf = new util.ArrayList[(String, Integer)]()
    val in = Map(
      "token" -> token,
      "question" -> question.id,
      "term" -> term
    )
    call(in, Map.empty)("{call getSelectionStatistics(?,?,?)}",
      map => {
        var answer: String = ""
        var amount: Integer = 0
        ->[String](map, "answer")(answer = _)
        ->[Integer](map, "amount")(amount = _)
        buf.add((answer, amount))
      }
    )
    buf
  }

  def getInputStatistics(term: String, token: Token, question: Question): java.util.List[String] = {
    val buf = new util.ArrayList[String]()
    val in = Map(
      "token" -> token,
      "question" -> question.id,
      "term" -> term
    )
    call(in, Map.empty)("{call getInputStatistics(?,?,?)}", ->[String](_, "answer")(answer => buf.add(answer)))
    buf
  }

  def getTerms(token: Token): java.util.List[String] = {
    val buf = new util.ArrayList[String]()
    call(Map("token" -> token), Map.empty)("{call getTerms(?)}", ->[String](_, "term")(term => buf.add(term)))
    buf
  }

  def getPollInfo(token: Token): (String, String, java.lang.Boolean) = {
    var tuple: (String, String, java.lang.Boolean) = ("", "", false)
    call(Map("token" -> token), Map.empty)("{call getPollInfo(?)}",
      map => {
        var title = ""
        var term = ""
        var active = false
        ->[String](map, "title")(title = _)
        ->[String](map, "term") (term = _)
        ->[java.lang.Boolean](map, "active") (active = _)
        tuple = (title, term, active)
      }
    )
    tuple
  }

  def getTokens: java.util.List[String] = {
    val buf = new util.ArrayList[String]()
    val owner = Int.box(MenuController.getId)
    call(Map("owner" -> owner), Map.empty)("{call getTokens(?)}", ->[String](_, "token")(token => buf.add(token)))
    buf
  }
}
