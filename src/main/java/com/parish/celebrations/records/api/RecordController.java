package com.parish.celebrations.records.api;

import com.parish.celebrations.records.api.dto.RecordResponse;
import com.parish.celebrations.records.application.RecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService svc;

    public RecordController(RecordService svc) { this.svc = svc; }

    @GetMapping("/{id}")
    public RecordResponse findById(@PathVariable UUID id) {
        return RecordResponse.from(svc.findById(id));
    }

    @GetMapping("/by-celebration/{celebrationId}")
    public List<RecordResponse> findByCelebration(@PathVariable UUID celebrationId) {
        return svc.findByCelebration(celebrationId).stream().map(RecordResponse::from).toList();
    }

    @GetMapping
    public Page<RecordResponse> findAll(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("registeredOn").descending());
        if (type != null) return svc.findByType(type, pageable).map(RecordResponse::from);
        return svc.findAll(pageable).map(RecordResponse::from);
    }

    @GetMapping("/{id}/certificate")
    public ResponseEntity<byte[]> certificate(@PathVariable UUID id) {
        // Stub PDF — returns a minimal valid PDF
        String pdfContent = "%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n" +
                "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n" +
                "3 0 obj<</Type/Page/MediaBox[0 0 612 792]/Parent 2 0 R>>endobj\n" +
                "xref\n0 4\n0000000000 65535 f\n0000000009 00000 n\n0000000058 00000 n\n" +
                "0000000115 00000 n\ntrailer<</Size 4/Root 1 0 R>>\nstartxref\n190\n%%EOF";
        byte[] pdf = pdfContent.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
