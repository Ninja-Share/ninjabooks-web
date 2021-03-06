package com.ninjabooks.dao.db;

import com.ninjabooks.dao.BookDao;
import com.ninjabooks.domain.Book;
import com.ninjabooks.util.db.SpecifiedElementFinder;

import java.util.Optional;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class DBBookDao implements BookDao
{
    private enum DBColumnName {TITLE, AUTHOR, ISBN}

    private final SessionFactory sessionFactory;
    private final SpecifiedElementFinder specifiedElementFinder;

    @Autowired
    public DBBookDao(SessionFactory sessionFactory,
                     @Qualifier(value = "streamFinder") SpecifiedElementFinder specifiedElementFinder) {
        this.sessionFactory = sessionFactory;
        this.specifiedElementFinder = specifiedElementFinder;
    }

    @Override
    public Stream<Book> getAll() {
        Session currentSession = sessionFactory.getCurrentSession();
        return currentSession.createQuery("select book from com.ninjabooks.domain.Book book", Book.class).stream();
    }

    @Override
    public Optional<Book> getById(Long id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Book book = currentSession.get(Book.class, id);
        return Optional.ofNullable(book);
    }

    @Override
    public Stream<Book> getByTitle(String title) {
        return specifiedElementFinder.findSpecifiedElementInDB(title, DBColumnName.TITLE, Book.class);
    }

    @Override
    public Stream<Book> getByAuthor(String author) {
        return specifiedElementFinder.findSpecifiedElementInDB(author, DBColumnName.AUTHOR, Book.class);
    }

    @Override
    public Stream<Book> getByISBN(String isbn) {
        return specifiedElementFinder.findSpecifiedElementInDB(isbn, DBColumnName.ISBN, Book.class);
    }

    @Override
    public void add(Book book) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(book);
    }

    @Override
    public void update(Book book) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.update(book);
    }

    @Override
    public void delete(Book book) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.delete(book);
    }

    @Override
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

}
