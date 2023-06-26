package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotDuplicateException;
import kh.com.kshrd.docengine.exceptions.NotEditorException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.entity.History;
import kh.com.kshrd.docengine.model.entity.Tag;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import kh.com.kshrd.docengine.repository.*;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.BlockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final HistoryRepository historyRepository;
    private final BlockHistoryRepository blockHistoryRepository;
    private final TagRepository tagRepository;

    @Override
    public Block createBlock(Block block) {
        if (block.getBlockType() == null) {
            throw new BadRequestException("Block type cannot be null");
        } else if (block.getDocumentId() == null) {
            throw new BadRequestException("Document id cannot be blank or empty");
        } else if (block.getDocumentId().toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        } else if (block.getBlockType().isBlank()) {
            throw new BadRequestException("Block type cannot be blank or empty");
        }

        Document document = documentRepository.getDocumentByDocumentId(block.getDocumentId());
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
                    return blockRepository.createBlock(block);
                }
            }

        }
    }

    @Override
    public Block editBlock(UUID blockId, UUID documentId, Map<String, Object> content, Integer order) {
        validateDocumentIdAndBlockId(blockId, documentId);

        Document documentData = documentRepository.getDocumentByDocumentId(documentId);

        if (documentData == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {

            Boolean checkUserIsMemberOfTheDocument = documentRepository.checkUserIsMemberOfTheDocument(userAuthenticationService.getUserIdOfCurrentUser(), documentData.getDocumentId());

            if (!checkUserIsMemberOfTheDocument) {
                throw new NotFoundException("You are not member of this document");
            } else {
                String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), documentData.getDocumentId());

                if (Objects.equals(checkAccessibility, "VIEWER") || Objects.equals(checkAccessibility, "NO_ACCESS")) {
                    throw new NotEditorException("Your accessibility cannot edit block for this document");
                } else {
                    Block blockData = blockRepository.getBlockByBlockId(blockId);
                    if (blockData == null) {
                        throw new NotFoundException("Block doesn't exist");
                    } else {
                        History history = historyRepository.backUpDocument(documentData.getTitle(), LocalDateTime.now(), documentData.getStatus(), userAuthenticationService.getUserIdOfCurrentUser(), documentData.getDocumentId(), documentData.getWorkspaceId());
                        List<Document> documents = documentRepository.getDocumentIdByPageId(documentData.getDocumentId());
                        for (Document document : documents) {
                            historyRepository.insertHistoryIdAndPageIdToHistoryPage(history.getHistoryId(), document.getDocumentId());
                        }
                        List<Block> blocks = blockRepository.getBlockByDocumentId(documentData.getDocumentId());
                        for (Block block : blocks) {
                            blockHistoryRepository.backUpBlock(block.getBlockType(), block.getContent(), block.getOrder(), history.getHistoryId());
                        }
                        List<Tag> tags = tagRepository.getTagByDocument(documentData.getDocumentId());
                        for (Tag tag : tags){
                            tagRepository.backUpTag(tag.getTagId(), history.getHistoryId());
                        }
                        return blockRepository.editBlock(blockId, documentId, content, order);
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
