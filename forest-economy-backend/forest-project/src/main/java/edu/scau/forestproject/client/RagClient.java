package edu.scau.forestproject.client;

import edu.scau.forestproject.dto.qa.*;

import java.util.List;

/**
 * RAG 服务 HTTP 客户端，接口约定见 docs/RAG-Python-API-Contract.md。
 */
public interface RagClient {

    QaAskResponse ask(QaAskRequest request);

    RagIngestResponse ingest(org.springframework.web.multipart.MultipartFile file);

    List<RagDocumentItem> listDocuments();

    void deleteDocument(String documentId);
}
