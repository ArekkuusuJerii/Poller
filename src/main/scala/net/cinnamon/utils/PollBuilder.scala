package net.cinnamon.utils

import net.cinnamon.entity
import net.cinnamon.entity.Question.Kind
import net.cinnamon.entity._

object PollBuilder {
  type Poll = PollBuilder.type
  type Question = QuestionBuilder
  private var poll: entity.Poll = _

  def +(title: String): Poll = {
    poll = new entity.Poll(title)
    this
  }

  def ?(question: String): Question = {
    new QuestionBuilder(this, question, Kind.SINGLE)
  }

  def ?+(question: String): Question = {
    new QuestionBuilder(this, question, Kind.MULTIPLE)
  }

  def ?-(question: String): Poll = {
    poll.questions.add(new entity.Question(question, Kind.OPEN))
    this
  }

  def ! : entity.Poll = {
    poll
  }

  class QuestionBuilder(builder: Poll, text: String, kind: Kind) {
    val question = new entity.Question(text, kind)
    builder.poll.questions.add(question)

    def -(answer: String): Question = {
      question.answers.add(new Answer(answer))
      this
    }

    def > : Poll = {
      builder
    }

    def ! : entity.Poll = {
      builder.poll
    }
  }
}
