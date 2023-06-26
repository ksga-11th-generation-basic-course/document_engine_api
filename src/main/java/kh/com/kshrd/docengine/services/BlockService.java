package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BlockService {
    Block createBlock(Block block);

    Block editBlock(UUID blockId, UUID documentId, Map<String, Object> content, Integer order);

    void deleteBlock(UUID blockId, UUID documentId);

    List<Block> getBlockForEachDocument(UUID documentId);
}
