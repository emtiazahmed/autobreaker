package com.unstablebuild.autobreaker

import java.lang.reflect.Proxy

import akka.actor.Scheduler

import scala.concurrent.ExecutionContext

object AutoBreaker {

  val defaultSettings = CircuitBreakerSettings()

  def proxy[T](obj: T, settings: Settings = defaultSettings)(implicit ec: ExecutionContext, scheduler: Scheduler): T = {
    val proxy = Proxy.newProxyInstance(
      obj.getClass.getClassLoader,
      interfaces(obj.getClass),
      new CircuitBreakerHandler(obj, settings))

    proxy.asInstanceOf[T]
  }

  private def interfaces(clazz: Class[_]): Array[java.lang.Class[_]] =
    Stream.iterate[Class[_]](clazz)(_.getSuperclass)
      .takeWhile(_ != null)
      .flatMap(_.getInterfaces)
      .toArray

}
