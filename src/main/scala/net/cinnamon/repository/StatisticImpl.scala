package net.cinnamon.repository

import net.cinnamon.entity.Question
import net.cinnamon.helper.SequenceHelper.call
import net.cinnamon.repository.PollImpl.Token
import net.cinnamon.repository.helper.->

import scala.collection.mutable.ArrayBuffer

object StatisticImpl {
  def getSelectionStatistics(token: Token, question: Question): ArrayBuffer[(String, Int)] = {
    val buf: ArrayBuffer[(String, Int)] = ArrayBuffer()
    val in = Map(
      "token" -> token,
      "question" -> question.id
    )
    call(in, Map.empty)("{call getSelectionStatistics(?,?)}",
      _ => {
        var answer: String = ""
        var amount: Int = 0
        ->(_, "answer")(answer = _)
        ->(_, "amount")(amount = _)
        buf +=((answer, amount))
      }
    )
    buf
  }

  def getInputStatistics(token: Token, question: Question): ArrayBuffer[String] = {
    val buf: ArrayBuffer[String] = ArrayBuffer()
    val in = Map(
      "token" -> token,
      "question" -> question.id
    )
    call(in, Map.empty)("{call getInputStatistics(?,?)}", ->(_, "answer")(_ => buf += _))
    buf
  }
}
