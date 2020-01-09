package tao

import java.util.Properties

import scala.collection.JavaConverters._

trait PropertiesImplicits {
  implicit class PimpedProperties(properties: Properties){
    def toMap: Map[String, String] = {
      properties.entrySet().asScala
        .map(x => (x.getKey.asInstanceOf[String], x.getValue.asInstanceOf[String]))
        .toMap
    }
  }

}
