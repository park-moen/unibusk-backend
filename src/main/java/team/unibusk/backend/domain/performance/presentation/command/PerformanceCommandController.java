package team.unibusk.backend.domain.performance.presentation.command;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.command.PerformanceCommandService;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceDetailResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceUpdateRequest;
import team.unibusk.backend.global.annotation.MemberId;

@RequiredArgsConstructor
@RequestMapping("/performances")
@RestController
public class PerformanceCommandController implements PerformanceCommandDocsController {

    private final PerformanceCommandService performanceCommandService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @RequestPart("request") @Valid PerformanceRegisterRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @MemberId Long memberId
    ) {
        PerformanceRegisterResponse response =
                performanceCommandService.register(request.toServiceRequest(memberId, image));

        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping(value = "/{performanceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerformanceDetailResponse> updatePerformance(
            @PathVariable Long performanceId,
            @RequestPart("request") @Valid PerformanceUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @MemberId Long memberId
    ) {
        PerformanceDetailResponse response = performanceCommandService.updatePerformance(request.toServiceRequest(performanceId, memberId, image));

        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{performanceId}")
    public ResponseEntity<Void> deletePerformance(
            @PathVariable Long performanceId,
            @MemberId Long memberId
    ) {
        performanceCommandService.deletePerformance(performanceId, memberId);

        return ResponseEntity.status(204).build();
    }

}
