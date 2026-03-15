package edu.scau.forestproject.client;

import edu.scau.forestproject.dto.qa.*;
import edu.scau.forestproject.exception.BusinessException;
import edu.scau.forestproject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RagClientImpl implements RagClient {

    private final RestClient ragRestClient;

    @Override
    public QaAskResponse ask(QaAskRequest request) {
        RagQaRequest body = new RagQaRequest(request.getQuestion(), request.getTopK());
        try {
            return ragRestClient
                    .post()
                    .uri("/qa")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(QaAskResponse.class);
        } catch (Exception e) {
            log.warn("RAG ask failed: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "知识问答服务暂不可用，请稍后重试");
        }
    }

    @Override
    public RagIngestResponse ingest(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请选择要上传的文档");
        }
        try {
            MultiValueMap<String, Object> multipart = new LinkedMultiValueMap<>();
            multipart.add("file", file.getResource());
            return ragRestClient
                    .post()
                    .uri("/ingest")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipart)
                    .retrieve()
                    .body(RagIngestResponse.class);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("RAG ingest failed: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "知识问答服务暂不可用，请稍后重试");
        }
    }

    @Override
    public List<RagDocumentItem> listDocuments() {
        try {
            List<RagDocumentItem> list = ragRestClient
                    .get()
                    .uri("/ingest/documents")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RagDocumentItem>>() {});
            return list != null ? list : Collections.emptyList();
        } catch (Exception e) {
            log.warn("RAG listDocuments failed: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "知识问答服务暂不可用，请稍后重试");
        }
    }

    @Override
    public void deleteDocument(String documentId) {
        try {
            ragRestClient
                    .delete()
                    .uri("/ingest/documents/{id}", documentId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("RAG deleteDocument failed: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "知识问答服务暂不可用，请稍后重试");
        }
    }
}
