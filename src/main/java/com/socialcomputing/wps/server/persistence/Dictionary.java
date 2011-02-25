package com.socialcomputing.wps.server.persistence;

import java.util.Date;
import java.util.List;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;

/**
 * Title: DictionaryLoader Description: Copyright: Copyright (c) 2000 Company:
 * VOYEZ VOUS
 * 
 * @author Franck Valetas
 * @version 1.0
 */

public interface Dictionary {
    public String getName();

    public WPSDictionary getDictionary();

    public void setDefinition(String definition);

    public String getDefinition();

    public void setFilteringdate(String filteringdate);

    public Date getNextFilteringDate();

    public Date computeNextFilteringDate();

    public List<Swatch> getSwatchs();
}