import net.cinnamon.helper.SequenceHelper

object CallTest extends App {
  SequenceHelper.call(Map(), Map())("{call test}", {
    map => print(map.getOrElse("hi", ""))
  })
}
