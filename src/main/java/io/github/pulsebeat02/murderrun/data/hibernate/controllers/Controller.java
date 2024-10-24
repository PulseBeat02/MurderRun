package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import com.google.common.reflect.TypeToken;
import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateSerializable;
import io.github.pulsebeat02.murderrun.data.yaml.ConfigurationManager;

public interface Controller<T extends HibernateSerializable> extends ConfigurationManager<T> {
  default Class<T> getGenericClass() {
    final TypeToken<T> token = new TypeToken<>(this.getClass()) {};
    return (Class<T>) token.getRawType();
  }
}
