package com.socialapi.socialapi.controller;

import com.socialapi.socialapi.dto.PostCreateRequest;
import com.socialapi.socialapi.dto.PostDto;
import com.socialapi.socialapi.dto.PostPageDto;
import com.socialapi.socialapi.entity.User;
import com.socialapi.socialapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "Gestion des publications (paginées)")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Liste des posts (paginée)")
    @ApiResponse(responseCode = "200", description = "Page de résultats")
    @GetMapping
    public PostPageDto collection(
            @Parameter(description = "Numéro de page 'défaut 1)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Nombre de posts par page (défaut 10, max 50)") @RequestParam(defaultValue = "10") int limit
    ) {
        return postService.findPaginated(page, limit);
    }

    @Operation(summary = "Récupére un post par son id")
    @ApiResponse(responseCode = "200", description = "Détail du post")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @GetMapping("/{id}")
    public PostDto item(@Parameter(description = "Identifiant du post") @PathVariable Long id) {
        return postService.findById(id);
    }

    @Operation(summary = "Crée un nouveau post")
    @ApiResponse(responseCode = "201", description = "POst créé")
    @ApiResponse(responseCode = "400", description = "Champ \"content\" manquant ou vide")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<PostDto> create(
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal User author
            ) {
        PostDto created = postService.create(request, author);
        return ResponseEntity.created(URI.create("/posts/" + created.id())).body(created);
    }

    @Operation(summary = "Modifie le contenu d'un posts (auteur uniquement)")
    @ApiResponse(responseCode = "204", description = "POst modifié avec succès")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas l'auteur du post")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @Parameter(description = "Identifiant du post") @PathVariable Long id,
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        postService.update(id, request, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Supprime un post (auteur uniquement)")
    @ApiResponse(responseCode = "204", description = "POst supprimé avec succès")
    @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas l'auteur du post")
    @ApiResponse(responseCode = "404", description = "Post introuvable")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant du post") @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        postService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
