package com.socialapi.socialapi.controller;

import com.socialapi.socialapi.dto.UserDto;
import com.socialapi.socialapi.entity.User;
import com.socialapi.socialapi.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Likes", description = "Gestion des likes")
@RestController
@RequestMapping("/posts/{postId}/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @Operation(summary = "Liste les utilisateurs ayant liké le post")
    @ApiResponse(responseCode = "200", description = "likes des utilisateurs")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @GetMapping
    public List<UserDto> collection(
            @Parameter(description = "Identifiant du post") @PathVariable Long postId) {
        return likeService.findLikers(postId);
    }

    @Operation(summary = "Like le post (utilisateut euthantifié)")
    @ApiResponse(responseCode = "204", description = "likes ajouté avec succès")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Void> like(
            @Parameter(description = "Identifiant du post") @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        likeService.like(postId, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retire le like du post (utilisateut euthantifié)")
    @ApiResponse(responseCode = "204", description = "likes retiré avec succès")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<Void> unlike(
            @Parameter(description = "Identifian du post") @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        likeService.unlike(postId, user);
        return ResponseEntity.noContent().build();
    }
}
