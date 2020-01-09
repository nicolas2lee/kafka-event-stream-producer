package tao

import java.util.Properties

import com.typesafe.config.Config
import net.ceedubs.ficus.readers.ValueReader

trait FicusImplicits {
  implicit def propertiesValueReader =  new ValueReader[Properties] {
    override def read(config: Config, path: String): Properties = {
      val properties = new Properties()
      config.entrySet().forEach( entry => properties.setProperty(entry.getKey(), config.getString(entry.getKey)))
      properties
    }
  }
}

object FicusImplicits extends FicusImplicits