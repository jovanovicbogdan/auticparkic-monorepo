package com.jovanovicbogdan.auticparkic.common;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {

  T create(T t);

  void update(T t);

  List<T> findAll();

  Optional<T> findById(final long id);

  void delete(final long id);
}
