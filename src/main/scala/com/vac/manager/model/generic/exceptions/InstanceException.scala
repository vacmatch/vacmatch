package com.vac.manager.model.generic.exceptions

abstract class InstanceException(
  specificMessage: String,
  key: Any,
  className: String
)
    extends Exception(specificMessage +
      " (key = '" + key + "' - className = '" +
      className + "')")

