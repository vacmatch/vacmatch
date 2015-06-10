package com.vac.manager.model.generic.exceptions

abstract class ArgumentException(
  specificMessage: String,
  key: Any,
  className: String
)
    extends Exception(specificMessage +
      " (key = '" + key + "' - className = '" +
      className + "')")

