package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.controller.MenuController
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

  def getCanAnswerPoll(token: Token): Boolean = {
    var out = false
    val respondent = Int.box(MenuController.getId)
    val in = Map(
      "token" -> token,
      "respondent" -> respondent
    )
    SequenceHelper.call(in, Map("confirmation" -> Types.BOOLEAN))("{call getCanAnswerPoll(?,?,?)}",
      ->[Boolean](_, "confirmation")(out = _)
    )
    out
  }

  def getIsPollOwner(token: Token): Boolean = {
    var out = false
    val owner = Int.box(MenuController.getId)
    SequenceHelper.call(Map("owner" -> owner, "token" -> token), Map("isOwner" -> Types.BOOLEAN))("{call getIsPollOwner(?,?,?)}",
      ->[Boolean](_, "isOwner")(out = _)
    )
    out
  }

  def createPoll(title: String, active: Boolean, term: String)(token: Token): Token = {
    var token = ""
    val owner = Int.box(MenuController.getId)
    val in = mutable.Map(
      "title" -> title,
      "owner" -> owner,
      "active" -> active,
      "term" -> term
    )
    if(token != null && token.nonEmpty) in += "token" -> token
    SequenceHelper.call(in.toMap, Map("token" -> Types.VARCHAR))("{call createPoll(?,?,?,?,?)}",
      ->[String](_, "token")(token = _)
    )
    token
  }

  def createQuestion(text: String, kind: Kind, token: Token)(id: Int): Int = {
    var id = -1
    val in = mutable.Map(
      "text" -> text,
      "kind" -> kind.id,
      "token" -> token
    )
    if(id > 0) in += "id" -> id
    SequenceHelper.call(in.toMap, Map("id" -> Types.INTEGER))("{call createQuestion(?,?,?,?)}",
      ->[Int](_, "id")(id = _)
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
    SequenceHelper.call(Map("token" -> token), Map("title" -> Types.VARCHAR, "active" -> Types.BOOLEAN, "term" -> Types.VARCHAR))("{call getPoll(?,?,?,?)}",
      map => {
        poll = new Poll
        ->[String](map, "title")(poll.title = _)
        ->[Boolean](map, "active")(poll.active = _)
        ->[String](map, "term")(poll.term = _)
        poll.token = token
        readQuestion(poll)
      }
    )
    poll
  }

  def savePoll(poll: Poll): Int = {
    var id = -1
    val respondent = Int.box(MenuController.getId)
    val in = Map(
      "respondent" -> respondent,
      "token" -> poll.token,
      "term" -> poll.term
    )
    SequenceHelper.call(in, Map("id" -> Types.INTEGER))("{call savePoll(?,?,?,?)}",
      ->[Int](_, "id")(id = _)
    )
    id
  }

  def saveAnswerSelection(token: Token, application: Int, question: Question, answer: Answer): Unit = {
    val in = Map(
      "application" -> application,
      "token" -> token,
      "question" -> question,
      "answer" -> answer.id
    )
    SequenceHelper.call(in, Map.empty)("{call saveAnswerSelection(?,?,?,?)}", _ => Unit)
  }

  def saveAnswerInput(token: Token, application: Int, question: Question, answer: String): Unit = {
    val in = Map(
      "application" -> application,
      "token" -> token,
      "question" -> question,
      "answer" -> answer
    )
    SequenceHelper.call(in, Map.empty)("{call saveAnswerInput(?,?,?,?)}", _ => Unit)
  }
}
