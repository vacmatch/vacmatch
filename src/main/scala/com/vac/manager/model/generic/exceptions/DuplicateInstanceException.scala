package com.vac.manager.model.generic.exceptions

class DuplicateInstanceException(
  key: Any,
  className: String
)
    extends InstanceException("Duplicate instance", key, className) {
  def getKey = key
  def getClassName = className
}

