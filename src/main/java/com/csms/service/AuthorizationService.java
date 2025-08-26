package com.csms.service;

import com.csms.model.IdTag;
import com.csms.repository.IdTagRepository;
import com.csms.dto.ocpp.StartTransactionResponse.IdTagInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final IdTagRepository idTagRepository;

    public IdTagInfo authorize(String idTag) {
        Optional<IdTag> tagOpt = idTagRepository.findByIdTag(idTag);

        if (tagOpt.isEmpty()) {
            log.warn("Unknown ID tag: {}", idTag);
            return new IdTagInfo("Invalid", null, null);
        }

        IdTag tag = tagOpt.get();

        // Check if tag is blocked
        if ("Blocked".equals(tag.getStatus())) {
            log.warn("Blocked ID tag: {}", idTag);
            return new IdTagInfo("Blocked", tag.getParentIdTag(), tag.getExpiryDate());
        }

        // Check if tag is expired
        if (tag.getExpiryDate() != null && tag.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Expired ID tag: {}", idTag);
            return new IdTagInfo("Expired", tag.getParentIdTag(), tag.getExpiryDate());
        }

        log.info("Authorized ID tag: {}", idTag);
        return new IdTagInfo("Accepted", tag.getParentIdTag(), tag.getExpiryDate());
    }
}