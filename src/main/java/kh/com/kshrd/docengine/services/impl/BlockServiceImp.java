package kh.com.kshrd.docengine.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.com.kshrd.docengine.model.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import kh.com.kshrd.docengine.repository.BlockRepository;
import kh.com.kshrd.docengine.services.BlockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Block> getBlockData() {
        return blockRepository.getBlockData();
    }

    @Override
    public Block editBlock(UUID blockId, String content) {
        return blockRepository.editBlock(blockId, content);
    }

    @Override
    public void deleteBlock(UUID blockId) {
        blockRepository.deleteBlock(blockId);
    }
}
