package com.ingemark.productapp.app.entity.identifiable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface IdentifiableEntityRepository<T extends IdentifiableEntity, ID> extends Repository<T, ID>
{
    List<T> findAll();

    T save(T entity);

    Optional<T> findById(ID id);

    void deleteById(ID id);
}