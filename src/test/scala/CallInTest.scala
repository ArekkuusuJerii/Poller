import net.cinnamon.helper.SequenceHelper

object CallInTest extends App {
  SequenceHelper.call(Map("some" -> "Hello World"), Map())("{call test_in(?)}", {
    map => print(map.getOrElse("value", ""))
  })
}
