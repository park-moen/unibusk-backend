package team.unibusk.backend.domain.member.presentation.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import team.unibusk.backend.domain.member.application.dto.response.MemberNameUpdateResponse;
import team.unibusk.backend.domain.member.presentation.request.MemberNameUpdateRequest;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.exception.ExceptionResponse;

@Tag(name = "Member", description = "회원 관련 API")
@RequestMapping("/members")
public interface MemberCommandDocsController {

    @Operation(summary = "회원 이름 변경", description = "로그인된 회원의 이름을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이름 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PatchMapping("/me")
    ResponseEntity<MemberNameUpdateResponse> updateMemberName(
            @MemberId Long memberId,
            @Valid @RequestBody MemberNameUpdateRequest request
    );

}
