package com.socialcomputing.wps.server.persistence;

import java.util.Collection;

public interface SwatchManager {

    public Collection<Swatch> findAll();

    public Swatch findByName(String name);

    public Swatch create(String name, String definition, Dictionary dictionary);

    public void update(Swatch swatch);

    public void remove(String name);
}
