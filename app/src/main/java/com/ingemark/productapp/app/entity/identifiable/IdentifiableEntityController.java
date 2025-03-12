package com.ingemark.productapp.app.entity.identifiable;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ingemark.productapp.app.util.Optionals;
import com.ingemark.productapp.app.util.dto.DtoConverter;
import jakarta.validation.Valid;

@RequiredArgsConstructor
public abstract class IdentifiableEntityController<E extends IdentifiableEntity, D>
{
    private final IdentifiableEntityService<E> service;

    private final DtoConverter<E, D> dtoConverter;
    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    @GetMapping
    public List<D> list()
    {
        return service.findAll()
            .stream()
            .map(entity -> dtoConverter.fromPojo(entity, dtoClass))
            .toList();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public D create(@Valid @RequestBody D dto)
    {
        return dtoConverter.fromPojo(service.create(dtoConverter.toPojo(dto, entityClass)), dtoClass);
    }

    @GetMapping("{id}")
    public D read(@PathVariable Integer id)
    {
        return dtoConverter.fromPojo(Optionals.unwrapUserRequestedObject(service.findById(id), entityClass), dtoClass);
    }

    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public D update(@PathVariable Integer id, @Valid @RequestBody D dto)
    {
        E entity = dtoConverter.toPojo(dto, entityClass);
        EntityVerifier.verifyIdsMatch(id, entity);
        return dtoConverter.fromPojo(service.update(entity), dtoClass);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id)
    {
        service.deleteById(id);
    }
}