package org.typelevel.jawn

import scala.collection.mutable

trait MutableFacade[J] extends Facade[J] {
  def jarray(vs: mutable.ArrayBuffer[J]): J
  def jobject(vs: mutable.Map[String, J]): J

  def singleContext() = new FContext[J] {
    var value: J = _
    def add(s: CharSequence): Unit = value = jstring(s)
    def add(v: J): Unit = value = v
    def finish(): J = value
    def isObj: Boolean = false
  }

  def arrayContext() = new FContext[J] {
    val vs = mutable.ArrayBuffer.empty[J]
    def add(s: CharSequence): Unit = vs += jstring(s)
    def add(v: J): Unit = vs += v
    def finish(): J = jarray(vs)
    def isObj: Boolean = false
  }

  def objectContext() = new FContext[J] {
    var key: String = null
    val vs = mutable.Map.empty[String, J]
    def add(s: CharSequence): Unit =
      if (key == null) {
        key = s.toString
      } else {
        vs(key) = jstring(s); key = null
      }
    def add(v: J): Unit = { vs(key) = v; key = null }
    def finish(): J = jobject(vs)
    def isObj: Boolean = true
  }
}
