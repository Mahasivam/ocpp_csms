package com.csms.controller;

import com.csms.model.IdTag;
import com.csms.repository.IdTagRepository;
import com.csms.dto.ocpp.StartTransactionResponse.IdTagInfo;
import com.csms.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/id-tags")
@RequiredArgsConstructor
public class IdTagController {

    private final IdTagRepository idTagRepository;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<List<IdTag>> getAllIdTags() {
        List<IdTag> idTags = idTagRepository.findAll();
        return ResponseEntity.ok(idTags);
    }

    @PostMapping
    public ResponseEntity<IdTag> createIdTag(@RequestBody IdTag idTag) {
        IdTag savedTag = idTagRepository.save(idTag);
        return ResponseEntity.ok(savedTag);
    }

    @PutMapping("/{idTag}")
    public ResponseEntity<IdTag> updateIdTag(@PathVariable String idTag, @RequestBody IdTag updatedTag) {
        return idTagRepository.findByIdTag(idTag)
                .map(existingTag -> {
                    existingTag.setStatus(updatedTag.getStatus());
                    existingTag.setExpiryDate(updatedTag.getExpiryDate());
                    existingTag.setParentIdTag(updatedTag.getParentIdTag());
                    return ResponseEntity.ok(idTagRepository.save(existingTag));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{idTag}/authorize")
    public ResponseEntity<IdTagInfo> authorizeIdTag(@PathVariable String idTag) {
        IdTagInfo authorization = authorizationService.authorize(idTag);
        return ResponseEntity.ok(authorization);
    }

    @DeleteMapping("/{idTag}")
    public ResponseEntity<Void> deleteIdTag(@PathVariable String idTag) {
        return idTagRepository.findByIdTag(idTag)
                .map(tag -> {
                    idTagRepository.delete(tag);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

