package edu.scau.forestproject.controller;

import edu.scau.forestproject.client.RagClient;
import edu.scau.forestproject.dto.qa.QaAskRequest;
import edu.scau.forestproject.dto.qa.QaAskResponse;
import edu.scau.forestproject.dto.qa.RagDocumentItem;
import edu.scau.forestproject.dto.qa.RagIngestResponse;
import edu.scau.forestproject.pojo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qa")
@RequiredArgsConstructor
public class QaController {

    private final RagClient ragClient;

    @PostMapping("/ask")
    public Result ask(@Valid @RequestBody QaAskRequest request) {
        QaAskResponse response = ragClient.ask(request);
        return Result.success(response);
    }

    @PostMapping("/ingest")
    public Result ingest(@RequestParam("file") MultipartFile file) {
        RagIngestResponse response = ragClient.ingest(file);
        return Result.success(response);
    }

    @GetMapping("/documents")
    public Result listDocuments() {
        List<RagDocumentItem> list = ragClient.listDocuments();
        return Result.success(list);
    }

    @DeleteMapping("/documents/{id}")
    public Result deleteDocument(@PathVariable("id") String documentId) {
        ragClient.deleteDocument(documentId);
        return Result.success();
    }
}
