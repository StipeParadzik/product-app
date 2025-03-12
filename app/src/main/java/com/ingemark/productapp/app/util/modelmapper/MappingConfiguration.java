package com.ingemark.productapp.app.util.modelmapper;

import org.modelmapper.ExpressionMap;

public interface MappingConfiguration<S, D>
{
    Class<S> getSourceType();

    Class<D> getDestinationType();

    default ExpressionMap<D, S> getDestinationToSourceMapper()
    {
        return null;
    }

    default ExpressionMap<S, D> getSourceToDestinationMapper()
    {
        return null;
    }
}