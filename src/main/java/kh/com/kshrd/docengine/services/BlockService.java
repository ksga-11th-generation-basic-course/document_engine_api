package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BlockService {
    Block createBlock(BlockRequest blockRequest);

    Block editBlock(UUID blockId, Map<String, Object> content);

    void deleteBlock(UUID blockId);

    List<Block> getBlockForEachDocument(UUID documentId);
}
