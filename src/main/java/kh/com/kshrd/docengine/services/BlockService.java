package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;

import java.util.List;
import java.util.UUID;

public interface BlockService {
    Block createBlock(BlockRequest blockRequest);

    List<Block> getBlockData();

    Block editBlock(UUID blockId, String content);

    void deleteBlock(UUID blockId);

    Block getBlockForEachDocument(UUID documentId);
}
