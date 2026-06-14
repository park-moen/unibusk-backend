package team.unibusk.backend.domain.performance.presentation.command;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceDetailResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceUpdateRequest;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.annotation.SwaggerBody;
import team.unibusk.backend.global.exception.ExceptionResponse;

@Tag(name = "Performance", description = "공연 관련 API")
@RequestMapping("/performances")
public interface PerformanceCommandDocsController {

    @Operation(
            summary = "공연 등록",
            description = "새로운 공연 정보를 등록합니다. 이미지 파일과 공연 정보를 함께 전송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공연 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패 등 서버 오류",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @SwaggerBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @RequestPart("request") @Valid PerformanceRegisterRequest request,
            @Parameter(description = "공연 관련 이미지")
            @RequestPart(value = "image", required = false) MultipartFile image,
            @MemberId Long memberId
    );

    @Operation(summary = "공연 수정", description = "공연 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "공연 수정 권한 없음",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @SwaggerBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PatchMapping(value = "/{performanceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PerformanceDetailResponse> updatePerformance(
            @PathVariable Long performanceId,
            @RequestPart("request") @Valid PerformanceUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @MemberId Long memberId
    );

    @Operation(summary = "공연 삭제", description = "공연을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "공연 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "공연 삭제 권한 없음",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @DeleteMapping("/{performanceId}")
    ResponseEntity<Void> deletePerformance(
            @PathVariable Long performanceId,
            @MemberId Long memberId
    );

}
