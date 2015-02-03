package com.vac.manager.model.generic.exceptions

class IllegalArgumentException (
  key: Any,
  className: String
)
    extends ArgumentException("Illegal argument", key, className)

