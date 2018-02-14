package net.cinnamon.utils

import scala.collection.mutable.ListBuffer

case class ConditionBuilder(sequence: String) {
  val conditions: ListBuffer[Condition[_]] = ListBuffer[Condition[_]]()
  implicit val format: Format = new Format(" %1$s %3$s %2$s ?")
  var hasNext: Boolean = false

  def where[A](toMatch: String): SimpleCondition[A] = {
    val condition = new SimpleCondition[A](this, toMatch, Prepend.Where)
    conditions += condition
    condition
  }

  def &&[A](toMatch: String): SimpleCondition[A] = {
    val condition = new SimpleCondition[A](this, toMatch, Prepend.And)
    conditions += condition
    condition
  }

  def ||[A](toMatch: String): SimpleCondition[A] = {
    val condition = new SimpleCondition[A](this, toMatch, Prepend.Or)
    conditions += condition
    condition
  }

  def <(prepend: Prepend.Value): WrappedCondition[ConditionBuilder] = {
    var condition = new WrappedCondition[ConditionBuilder](this, prepend)
    conditions += condition
    condition
  }

  def build(implicit format: Format): String = {
    val builder = new StringBuilder(sequence)
    for (condition <- conditions) {
      if (condition.canAppend) {
        builder append condition.getAppend
        hasNext = true
      }
    }
    builder.toString()
  }

  trait Condition[Value] {
	  var value: Value = _
	  def canAppend: Boolean = value != null
	  def getAppend(implicit format: Format): String
  }

  class Format(string: String) {
    override def toString: String = string
  }

  case class SimpleCondition[Value](builder: ConditionBuilder, toMatch: String, var prepend: Prepend.Value) extends Condition[Value] {

    def ->(value: Value)(implicit f: Value => _): ConditionBuilder = {
      this.value = value
      if (canAppend) f.apply(value)
      builder
    }

    override def getAppend(implicit format: Format): String = {
      prepend = if (builder.hasNext && builder.sequence.contains("WHERE")) prepend else Prepend.Where
      val equality = value match {
        case s: String if s.contains("%") => "like"
        case _ => "="
      }
      String.format(format.toString, prepend, equality, toMatch)
    }
  }

  case class WrappedCondition[A](container: A, var prepend: Prepend.Value) extends Condition[Unit] {
    type Self = this.type
    val conditions: ListBuffer[Condition[_]] = ListBuffer[Condition[_]]()
    var hasNext = false

    def &&[Value](toMatch: String): Self = {
      val wrap = new Wrap[Value, Self](this, toMatch, Prepend.And)
      conditions += wrap
      this
    }

    def ||[Value](toMatch: String): Self = {
      val wrap = new Wrap[Value, Self](this, toMatch, Prepend.Or)
      conditions += wrap
      this
    }

    def <(prepend: Prepend.Value): WrappedCondition[Self] = {
      var condition = new WrappedCondition[Self](this, prepend)
      conditions += condition
      condition
    }

    def >(): A = container

    override def canAppend: Boolean = conditions.exists(_.canAppend)

    override def getAppend(implicit format: Format): String = {
      val builder = new StringBuilder(' ') append (container match {
        case m: WrappedCondition[_] if !m.hasNext => Prepend.Nil
        case _ => prepend
      })
      builder append '('
      for (condition <- conditions) {
        if (condition.canAppend) builder append condition.getAppend
      }
      builder append ')' toString()
    }

    case class Wrap[Value, Wrapped <: WrappedCondition[_]](condition: Wrapped, toMatch: String, var prepend: Prepend.Value) extends Condition[Value] {

      def ->(value: Value)(implicit f: Value => _): Wrapped = {
        this.value = value
        if (canAppend) f.apply(value)
        condition
      }

      override def getAppend(implicit format: Format): String = {
        prepend = if (condition.hasNext) prepend else Prepend.Nil
        val equality = value match {
          case s: String if s.contains("%") => "like"
          case _ => "="
        }
        String.format(format.toString, prepend, equality, toMatch)
      }
    }

  }

  object Prepend extends Enumeration {
    type Prepend = Value
    val Where: Prepend = Value("WHERE")
    val And: Prepend = Value("&&")
    val Or: Prepend = Value("||")
    val Nil: Prepend = Value("")
  }

}
