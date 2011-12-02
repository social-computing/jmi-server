package com.socialcomputing.wps.server.persistence;

import java.util.Collection;

public interface DictionaryManager {

    Collection<Dictionary> findAll();

    Dictionary findByName(String name);

    Dictionary create(String name, String definition);

    void update(Dictionary dictionary);

    void remove(String name);
}
