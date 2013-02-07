import scala.io.Source
import scala.util.parsing.json.JSON
import java.util.Date
import java.util.Calendar


val file = Source.fromFile("/Users/watanabe_yusaku/Dropbox/Private/development/scala/partner_user_debug.json")
var resultMap = Map.empty[String, Map[String, Long]]

try {
  val c = Calendar.getInstance()
  
  def getCountKey(d: Date): String = {
    c.setTime(d)
    c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH)
  }

  for (line <- file.getLines) {
    JSON.globalNumberParser = {(_:String).toLong}

    val json : Option[Any] = JSON.parseFull(line)
    val map : Map[String, Option[Any]] = json.get.asInstanceOf[Map[String, Option[Any]]]
    
    //登録日カウント
    val regiDaylyKey = getCountKey(new Date(map.get("registerDate").get.asInstanceOf[Long]))
    val regi : Map[String, Long] = resultMap.get(regiDaylyKey) match {
      case Some(v) => 
        val register : Long = v.get("register") match {
          case Some(v) => v.asInstanceOf[Long]
          case _ => 0
        }
	v + ("register" -> (register + 1))
      case _ => 
        Map("register" -> 1)
    }
    resultMap += (regiDaylyKey -> regi)


    //退会日カウント
    val withDaylyKey = getCountKey(new Date(map.get("withdrawalDate").get.asInstanceOf[Long]))

    val wi : Map[String, Long] = resultMap.get(withDaylyKey) match {
      case Some(v) => 
        val withdraw : Long = v.get("withdraw") match {
          case Some(v) => v.asInstanceOf[Long]
          case _ => 0
        }
	v + ("withdraw" -> (withdraw + 1))
      case _ => 
        Map("withdraw" -> 1)
    }
    resultMap += (withDaylyKey -> wi)


    print(".")
  }
} finally {
  file.close
}

for(key <- resultMap.keys) {
    var r = resultMap(key)
    val regiCnt : Long = r.get("register") match {
      case Some(v) => v.asInstanceOf[Long]
      case _ => 0
    }
    val withCnt : Long = r.get("withdraw") match {
      case Some(v) => v.asInstanceOf[Long]
      case _ => 0
    }

    println(key + ":" + regiCnt + ":" + withCnt)
}
