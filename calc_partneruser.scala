import scala.io.Source
import scala.util.parsing.json.JSON
import java.util.Date
import java.util.Calendar


val file = Source.fromFile("/Users/watanabe_yusaku/Dropbox/Private/development/scala/partner_user_debug.json")
var resultMap = Map.empty[String, Map[String, Long]]

try {
  val c = Calendar.getInstance()

  for (line <- file.getLines) {
    JSON.globalNumberParser = {(_:String).toLong}

    val json : Option[Any] = JSON.parseFull(line)
    val map : Map[String, Option[Any]] = json.get.asInstanceOf[Map[String, Option[Any]]]
    val d = new Date(map.get("registerDate").get.asInstanceOf[Long])
    val w = new Date(map.get("withdrawalDate").get.asInstanceOf[Long])

    
    //登録日カウント
    c.setTime(d)
    var regiDaylyKey =  c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH)

    var regi : Map[String, Long] = resultMap.get(regiDaylyKey) match {
      case Some(v) => v.asInstanceOf[Map[String, Long]]
      case _ => Map.empty[String, Long]
    }
    val register : Long = regi.get("register") match {
      case Some(v) => v.asInstanceOf[Long]
      case _ => 0
    }
    val registerCnt : Long = register + 1
    regi += ("register" -> registerCnt)
    resultMap += (regiDaylyKey -> regi)


    //退会日カウント
    c.setTime(w)
    var withDaylyKey =  c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH)

    var wi : Map[String, Long] = resultMap.get(withDaylyKey) match {
      case Some(v) => v.asInstanceOf[Map[String, Long]]
      case _ => Map.empty[String, Long]
    }
    val withdraw : Long = wi.get("withdraw") match {
      case Some(v) => v.asInstanceOf[Long]
      case _ => 0
    }
    val withdrawCnt : Long = if (w == 0) withdraw else withdraw + 1
    wi += ("withdraw" -> withdrawCnt)
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
