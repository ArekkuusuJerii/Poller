package net.cinnamon.repository

import net.cinnamon.entity.Question
import net.cinnamon.helper.SequenceHelper.call
import net.cinnamon.repository.PollImpl.Token
import net.cinnamon.repository.helper.->

import scala.collection.mutable.ArrayBuffer

object StatisticImpl {
  def getQuestionStatistics(token: Token, question: Question): ArrayBuffer[(String, Int)] = {
    val buf: ArrayBuffer[(String, Int)] = ArrayBuffer()
    val in = Map(
      "token" -> token,
      "question" -> question.id
    )
    call(in, Map.empty)("{call getQuestionStatistics(?,?)}",
      _ => {
        var answer: String = _
        var amount: Int = _
        ->(_, "answer")(answer = _)
        ->(_, "amount")(amount = _)
        buf +=((answer, amount))
      }
    )
    buf
  }
}
