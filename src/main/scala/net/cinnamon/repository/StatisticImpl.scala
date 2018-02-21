package net.cinnamon.repository

import java.util

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
}
