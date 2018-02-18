package net.cinnamon.utils

import net.cinnamon.entity.{Encuesta, Pregunta, Respuesta, Tipo}

object PollBuilder {
  type Poll = PollBuilder.type
  type Question = QuestionBuilder
  private var poll: Encuesta = _

  def +(title: String): Poll = {
    poll = new Encuesta(title)
    this
  }

  def ?(question: String): Question = {
    new QuestionBuilder(this, question, Tipo.SINGLE)
  }

  def ?+(question: String): Question = {
    new QuestionBuilder(this, question, Tipo.MULTIPLE)
  }

  def ?-(question: String): Poll = {
    poll.preguntas.add(new Pregunta(question, Tipo.OPEN))
    this
  }

  def :> : Encuesta = {
    poll
  }

  class QuestionBuilder(builder: Poll, text: String, kind: Tipo) {
    val question = new Pregunta(text, kind)
    builder.poll.preguntas.add(question)

    def -(answer: String): Question = {
      question.respuestas.add(new Respuesta(answer))
      this
    }

    def > : Poll = {
      builder
    }

    def :> : Encuesta = {
      builder.poll
    }
  }
}
