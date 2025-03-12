package com.ingemark.productapp.app.entity.identifiable;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public abstract class IdentifiableEntityService<E extends IdentifiableEntity>
{
    private final IdentifiableEntityRepository<E, Integer> repository;

    public List<E> findAll()
    {
        return repository.findAll();
    }

    @Transactional
    public E create(E entity)
    {
        EntityVerifier.verifyIdNotSet(entity);
        return repository.save(entity);
    }

    @Transactional
    public E update(E entity)
    {
        return repository.save(entity);
    }

    public Optional<E> findById(Integer id)
    {
        return repository.findById(id);
    }

    @Transactional
    public void deleteById(Integer id)
    {
        repository.deleteById(id);
    }
}