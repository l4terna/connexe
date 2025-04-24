package com.laterna.connexemain.v1.category;

import com.laterna.connexemain.v1.category.dto.CategoryDTO;
import com.laterna.connexemain.v1.category.dto.CreateCategoryDTO;
import com.laterna.connexemain.v1.category.dto.UpdateCategoryDTO;
import com.laterna.connexemain.v1.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hubs/{hubId}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @PathVariable Long hubId,
            @Valid @RequestBody CreateCategoryDTO createCategoryDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(categoryService.create(hubId, createCategoryDTO, user));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long hubId,
            @PathVariable Long categoryId,
            @RequestBody UpdateCategoryDTO updateCategoryDTO,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(categoryService.update(hubId, categoryId, updateCategoryDTO, user));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long hubId,
            @PathVariable Long categoryId,
            @AuthenticationPrincipal User user
    ) {
        categoryService.delete(hubId, categoryId, user);
        return ResponseEntity.noContent().build();
    }
}
