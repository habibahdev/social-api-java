package com.socialapi.socialapi.controller;

import com.socialapi.socialapi.dto.CommentCreateRequest;
import com.socialapi.socialapi.dto.CommentDto;
import com.socialapi.socialapi.entity.User;
import com.socialapi.socialapi.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Tag(name = "Comments", description = "Gestion des commentaires")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Liste des commentaires d'un post")
    @ApiResponse(responseCode = "200", description = "Liste des commentaires")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> collection(
            @Parameter(description = "Identifiant du post parent") @PathVariable Long postId) {
        return commentService.findByPost(postId);
    }

    @Operation(summary = "Récupère un commentaire par son id")
    @ApiResponse(responseCode = "200", description = "Détail du commentaire")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @GetMapping("/comments/{id}")
    public CommentDto item(
            @Parameter(description = "Identifiant du post parent") @PathVariable Long id) {
        return commentService.findById(id);
    }

    @Operation(summary = "Ajoute un commentaire à un post")
    @ApiResponse(responseCode = "201", description = "Commentaire crée")
    @ApiResponse(responseCode = "400", description = "Champ \"message\" manquant ou vide")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @ApiResponse(responseCode = "404", description = "Post introuvabe")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> create(
            @Parameter(description = "Identifiant du post parent") @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal User author
            ) {
        CommentDto created = commentService.create(postId, request, author);
        return ResponseEntity.created(URI.create("/comments/" + created.id())).body(created);
    }

    @Operation(summary = "Modifie un commentaire (auteur uniquement")
    @ApiResponse(responseCode = "201", description = "Commentaire modifié avec succès")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas l'auteur du commentaire")
    @ApiResponse(responseCode = "404", description = "Commentaire introuvabe")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/comments/{id}")
    public ResponseEntity<Void> update(
            @Parameter(description = "Identifiant du commentaire") @PathVariable Long id,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        commentService.update(id, request, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Supprime un commentaire (auteur uniquement")
    @ApiResponse(responseCode = "201", description = "Commentaire supprimé avec succès")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas l'auteur du commentaire")
    @ApiResponse(responseCode = "404", description = "Commentaire introuvabe")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant du commentaire") @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        commentService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
