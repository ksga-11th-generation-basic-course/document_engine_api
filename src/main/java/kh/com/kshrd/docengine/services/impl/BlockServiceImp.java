package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.model.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import kh.com.kshrd.docengine.repository.BlockRepository;
import kh.com.kshrd.docengine.services.BlockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BlockServiceImp implements BlockService {
    private final BlockRepository blockRepository;

    @Override
    public Block createBlock(BlockRequest blockRequest) {
        return blockRepository.createBlock(blockRequest);
    }

    @Override
    public Block editBlock(UUID blockId, Map<String, Object> content) {
        if(blockId == null){
            throw new BadRequestException("Block id cannot be null");
        } else if (blockId.toString().isBlank()) {
            throw new BadRequestException("Block id cannot be blank or empty");
        }
        return blockRepository.editBlock(blockId, content);
    }

    @Override
    public void deleteBlock(UUID blockId) {
        if(blockId == null){
            throw new BadRequestException("Block id cannot be null");
        } else if (blockId.toString().isBlank()) {
            throw new BadRequestException("Block id cannot be blank or empty");
        }
        blockRepository.deleteBlock(blockId);
    }

    @Override
    public List<Block> getBlockForEachDocument(UUID documentId) {
        if(documentId == null){
            throw new BadRequestException("Document id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        return blockRepository.getBlockForEachDocument(documentId);
    }
}
