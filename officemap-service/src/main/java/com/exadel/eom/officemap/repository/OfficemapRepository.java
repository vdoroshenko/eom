package com.exadel.eom.officemap.repository;

import com.exadel.eom.officemap.domain.Officemap;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficemapRepository extends CrudRepository<Officemap, String> {

	Officemap findByName(String name);

}
