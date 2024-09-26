package com.staccato.member.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import com.staccato.config.auth.LoginMember;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;
import com.staccato.member.service.dto.response.MemberProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Member", description = "Member API")
public interface MyPageControllerDocs {
    @Operation(summary = "사용자 프로필 사진 변경", description = "사용자의 프로필 사진을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "프로필 변경 성공", responseCode = "200"),
            @ApiResponse(description = """
                    <발생 가능한 케이스>
                                        
                    (1) 사용자 정보를 찾을 수 없을 때
                                        
                    (2) 파일이 누락되었을 때
                                        
                    (3) 요청 형식이 잘못 되었을 때
                                        
                    (4) 파일이 손상되었거나, 지원되지 않을 때
                    """,
                    responseCode = "400")
    })
    ResponseEntity<MemberProfileImageResponse> changeProfileImage(
            @RequestPart(value = "imageFile") MultipartFile image,
            @Parameter(hidden = true) @LoginMember Member member
    );

    @Operation(summary = "사용자 프로필 조회", description = "프로필 상 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "사용자 정보 조회 성공", responseCode = "200"),
            @ApiResponse(description = "요청 형식이 잘못 되었을 때", responseCode = "400")
    })
    public ResponseEntity<MemberProfileResponse> readMyPage(@Parameter(hidden = true) @LoginMember Member member);
}
