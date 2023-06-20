package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotEditorException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import kh.com.kshrd.docengine.repository.BlockRepository;
import kh.com.kshrd.docengine.repository.DocumentRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.BlockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BlockServiceImp implements BlockService {
    private final BlockRepository blockRepository;
    private final DocumentRepository documentRepository;
    private final UserAuthenticationService userAuthenticationService;

    @Override
    public Block createBlock(BlockRequest blockRequest) {
        if (blockRequest.getBlockType() == null) {
            throw new BadRequestException("Block type cannot be null");
        } else if (blockRequest.getDocumentId() == null) {
            throw new BadRequestException("Document id cannot be blank or empty");
        } else if (blockRequest.getDocumentId().toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        } else if (blockRequest.getBlockType().isBlank()) {
            throw new BadRequestException("Block type cannot be blank or empty");
        }

        Document document = documentRepository.getDocumentByDocumentId(blockRequest.getDocumentId());
        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {

            Boolean checkUserIsMemberOfTheDocument = documentRepository.checkUserIsMemberOfTheDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());

            if (!checkUserIsMemberOfTheDocument) {
                throw new NotFoundException("You are not member of this document");
            } else {
                String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());
                if (Objects.equals(checkAccessibility, "VIEWER") || Objects.equals(checkAccessibility, "NO_ACCESS")) {
                    throw new NotEditorException("Your accessibility cannot create block for this document");
                } else {
                    return blockRepository.createBlock(blockRequest, blockRepository.order(blockRequest.getDocumentId()));
                }
            }

        }
    }

    @Override
    public Block editBlock(UUID blockId, UUID documentId, Map<String, Object> content) {
        validateDocumentIdAndBlockId(blockId, documentId);

        Document document = documentRepository.getDocumentByDocumentId(documentId);

        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {

            Boolean checkUserIsMemberOfTheDocument = documentRepository.checkUserIsMemberOfTheDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());

            if (!checkUserIsMemberOfTheDocument) {
                throw new NotFoundException("You are not member of this document");
            } else {
                String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());

                if (Objects.equals(checkAccessibility, "VIEWER") || Objects.equals(checkAccessibility, "NO_ACCESS")) {
                    throw new NotEditorException("Your accessibility cannot edit block for this document");
                } else {
                    Block block = blockRepository.getBlockByBlockId(blockId);
                    if (block == null) {
                        throw new NotFoundException("Block doesn't exist");
                    } else {
                        return blockRepository.editBlock(blockId, documentId, content);
                    }
                }
            }
        }
    }

    @Override
    public void deleteBlock(UUID blockId, UUID documentId) {
        validateDocumentIdAndBlockId(blockId, documentId);

        Document document = documentRepository.getDocumentByDocumentId(documentId);

        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {

            Boolean checkUserIsMemberOfTheDocument = documentRepository.checkUserIsMemberOfTheDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());

            if (!checkUserIsMemberOfTheDocument) {
                throw new NotFoundException("You are not member of this document");
            } else {
                String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());

                if (Objects.equals(checkAccessibility, "VIEWER") || Objects.equals(checkAccessibility, "NO_ACCESS")) {
                    throw new NotEditorException("Your accessibility cannot delete block for this document");
                } else {
                    Block block = blockRepository.getBlockByBlockId(blockId);
                    if (block == null) {
                        throw new NotFoundException("Block doesn't exist");
                    } else {
                        blockRepository.deleteBlock(blockId, documentId);
                    }
                }
            }
        }
    }

    private void validateDocumentIdAndBlockId(UUID blockId, UUID documentId) {
        if (blockId == null) {
            throw new BadRequestException("Block id cannot be null");
        } else if (documentId == null) {
            throw new BadRequestException("Document id cannot be blank or empty");
        } else if (blockId.toString().isBlank()) {
            throw new BadRequestException("Block id cannot be blank or empty");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
    }

    @Override
    public List<Block> getBlockForEachDocument(UUID documentId) {
        if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        return blockRepository.getBlockForEachDocument(documentId);
    }

}
