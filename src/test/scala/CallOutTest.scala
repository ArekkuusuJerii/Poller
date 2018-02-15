import java.sql.Types

import net.cinnamon.helper.SequenceHelper

object CallOutTest extends App {
  SequenceHelper.call(Map.empty, Map("value" -> Types.INTEGER))("{call test_out(?)}", {
    map => print(map.getOrElse("value", ""))
  })
}
