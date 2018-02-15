import net.cinnamon.helper.SequenceHelper

object CallInTest extends App {
  SequenceHelper.call(Map("some" -> "Hello World!"), Map.empty)("{call test_in(?)}", {
    map => print(map.getOrElse("value", ""))
  })
}
