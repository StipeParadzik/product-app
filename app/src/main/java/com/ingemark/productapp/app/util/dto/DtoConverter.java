package com.ingemark.productapp.app.util.dto;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoConverter<P, D>
{
    private final ModelMapper modelMapper;

    public P toPojo(D dto, Class<P> pojoClass)
    {
        if (dto == null)
        {
            return null;
        }
        return modelMapper.map(dto, pojoClass);
    }

    public D fromPojo(P pojo, Class<D> dtoClass)
    {
        if (pojo == null)
        {
            return null;
        }
        return modelMapper.map(pojo, dtoClass);
    }
}
