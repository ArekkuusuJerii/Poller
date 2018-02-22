package net.cinnamon.utils

import net.cinnamon.entity.Question.Kind
import net.cinnamon.entity.{Answer, Poll, Question}


object PollBuilder {
  type P = PollBuilder.type
  type Q = QuestionBuilder
  private var poll: Poll = _

  def +(title: String): P = {
    poll = new Poll
    poll.title = title
    this
  }

  def ==(term: String): P = {
    poll.term = term
    this
  }

  def ?(text: String): Q = {
    new QuestionBuilder(this, text, Kind.SINGLE)
  }

  def ?+(text: String): Q = {
    new QuestionBuilder(this, text, Kind.MULTIPLE)
  }

  def ?-(text: String): P = {
    new QuestionBuilder(this, text, Kind.OPEN)!
  }

  def !! : Poll = {
    poll
  }

  class QuestionBuilder(builder: P, text: String, kind: Kind) {
    val question = new Question
    question.text = text
    question.kind = kind
    builder.poll.questions.add(question)

    def -(text: String): Q = {
      val answer = new Answer
      answer.text = text
      question.answers.add(answer)
      this
    }

    def ! : P = {
      builder
    }

    def !! : Poll = {
      builder.poll
    }
  }
}
