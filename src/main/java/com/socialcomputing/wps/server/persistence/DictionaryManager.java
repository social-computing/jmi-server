package com.socialcomputing.wps.server.persistence;

import java.util.Collection;

public interface DictionaryManager {

    public Collection<Dictionary> findAll();

    public Dictionary findByName(String name);

    public Dictionary create(String name, String definition);

    public void update(Dictionary dictionary);

    public void remove(String name);
}
