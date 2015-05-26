package com.vac.manager.model.generic.exceptions

class InstanceNotFoundException(
  key: Any,
  className: String
)
    extends InstanceException("Instance not found", key, className)

