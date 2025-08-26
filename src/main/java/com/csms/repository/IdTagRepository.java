package com.csms.repository;

import com.csms.model.IdTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdTagRepository extends JpaRepository<IdTag, UUID> {
    Optional<IdTag> findByIdTag(String idTag);
}