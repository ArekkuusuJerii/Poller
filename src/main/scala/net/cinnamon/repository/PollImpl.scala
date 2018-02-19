package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.controller.Menu
import net.cinnamon.entity.Question.Kind
import net.cinnamon.entity.{Answer, Poll, Question}
import net.cinnamon.helper.SequenceHelper
import net.cinnamon.repository.helper.->

import scala.collection.mutable

object PollImpl {
  type Token = String

  def getIsPollActive(token: Token): Boolean = {
    var out = false
    SequenceHelper.call(Map("token" -> token), Map("active" -> Types.BOOLEAN))("{call getIsPollActive(?,?)}",
      ->[Boolean](_, "active")(out = _)
    )
    out
  }

  def getIsPollOwner(token: Token): Boolean = {
    var out = false
    val owner = Int.box(Menu.getId)
    SequenceHelper.call(Map("owner" -> owner, "token" -> token), Map("isOwner" -> Types.BOOLEAN))("{call getIsPollOwner(?,?,?)}",
      ->[Boolean](_, "isOwner")(out = _)
    )
    out
  }

  def createPoll(title: String, active: Boolean)(token: Token): Token = {
    var token = ""
    val owner = Int.box(Menu.getId)
    val in = mutable.Map(
      "title" -> title,
      "owner" -> owner,
      "active" -> active
    )
    if(token != null && token.nonEmpty) in += "token" -> token
    SequenceHelper.call(in.toMap, Map("token" -> Types.VARCHAR))("{call createPoll(?,?,?,?)}",
      ->[String](_, "token")(token = _)
    )
    token
  }

  def createQuestion(text: String, kind: Kind, token: Token)(id: Int): Int = {
    var id = -1
    val in = mutable.Map(
      "text" -> text,
      "token" -> token,
      "kind" -> kind.id
    )
    if(id > 0) in += "id" -> id
    SequenceHelper.call(in.toMap, Map("id" -> Types.INTEGER))("{call createQuestion(?,?,?,?)}",
      ->[Int](_, "token")(id = _)
    )
    id
  }

  def createAnswer(text: String, question: Int)(id: Int): Int = {
    var id = -1
    val in = mutable.Map(
      "answer" -> text,
      "question" -> question
    )
    if(id > 0) in += "id" -> id
    SequenceHelper.call(in.toMap, Map("id" -> Types.INTEGER))("{call createAnswer(?,?,?)}",
      ->[Int](_, "id")(id = _)
    )
    id
  }

  def readPoll(token: Token): Poll = {
    def readAnswer(question: Question): Unit = {
      SequenceHelper.call(Map("question" -> question.id), Map.empty)("{call getAnswer(?)}",
        map => {
          val answer: Answer = new Answer
          ->[Int](map, "id")(answer.id = _)
          ->[String](map, "respuesta")(answer.text = _)
          question.answers.add(answer)
        }
      )
    }

    def readQuestion(poll: Poll): Unit = {
      SequenceHelper.call(Map("token" -> poll.token), Map.empty)("{call getQuestion(?)}",
        map => {
          val question: Question = new Question
          ->[Int](map, "id")(question.id = _)
          ->[String](map, "texto")(question.text = _)
          ->[Int](map, "tipo")(kind => {
            question.kind = Kind.get(kind)
          })
          readAnswer(question)
          poll.questions.add(question)
        }
      )
    }

    var poll: Poll = null
    SequenceHelper.call(Map("token" -> token), Map("title" -> Types.VARCHAR, "active" -> Types.BOOLEAN))("{call getPoll(?, ?, ?)}",
      map => {
        poll = new Poll
        ->[String](map, "title")(poll.title = _)
        ->[Boolean](map, "active")(poll.active = _)
        poll.token = token
        readQuestion(poll)
      }
    )
    poll
  }
}
