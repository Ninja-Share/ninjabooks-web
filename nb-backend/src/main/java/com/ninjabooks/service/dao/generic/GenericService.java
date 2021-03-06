package com.ninjabooks.service.dao.generic;

import java.util.Optional;
import java.util.stream.Stream;

import org.hibernate.Session;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
public interface GenericService<E, K>
{
    void add(E entity);

    void remove(E entity);

    void update(E entity);

    Stream<E> getAll();

    Optional<E> getById(K id);

    Session getSession();
}
