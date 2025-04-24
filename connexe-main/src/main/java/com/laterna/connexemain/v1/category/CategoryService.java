package com.laterna.connexemain.v1.category;

import com.laterna.connexemain.v1.category.dto.CategoryDTO;
import com.laterna.connexemain.v1.category.dto.CreateCategoryDTO;
import com.laterna.connexemain.v1.category.dto.UpdateCategoryDTO;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final HubService hubService;
    private final PermissionService permissionService;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories(Long hubId) {
        return categoryRepository.findAllByHubIdOrderByPosition(hubId)
                .stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    private int getLastPosition(Long hubId) {
        return categoryRepository.findMaxPositionByHubId(hubId)
                .orElse(0) + 1;
    }

    @Transactional
    public CategoryDTO create(Long hubId, CreateCategoryDTO createCategoryDTO, User currentUser) {
        int lastPosition = getLastPosition(hubId);
        Hub hub = hubService.findHubById(hubId);

        permissionService.hasPermissionsThrow(currentUser.getId(), hubId, Permission.MANAGE_CATEGORIES);

        Category newCategory = Category.builder()
                .position(lastPosition)
                .name(createCategoryDTO.name())
                .hub(hub)
                .build();

        return categoryMapper.toDTO(categoryRepository.save(newCategory));
    }

    @Transactional
    public CategoryDTO update(Long hubId, Long categoryId, UpdateCategoryDTO updateCategoryDTO, User currentUser) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        permissionService.hasPermissionsThrow(currentUser.getId(), hubId, Permission.MANAGE_CATEGORIES);

        if (updateCategoryDTO.position() != null && !updateCategoryDTO.position().equals(category.getPosition())) {
            int lastPosition = getLastPosition(hubId);
            int newPosition = Math.min(Math.max(updateCategoryDTO.position(), 1), lastPosition - 1);

            if(newPosition > category.getPosition()) {
                shiftPositions(hubId, category.getPosition() + 1, newPosition, -1);
            } else if (newPosition < category.getPosition()) {
                shiftPositions(hubId, newPosition, category.getPosition(), 1);
            }

            category.setPosition(newPosition);
        }

        if (updateCategoryDTO.name() != null && !updateCategoryDTO.name().equals(category.getName())) {
            category.setName(updateCategoryDTO.name());
        }

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    private void shiftPositions(Long hubId, Integer startPosition, Integer endPosition, Integer delta) {
        categoryRepository.shiftPositions(hubId, startPosition, endPosition,delta);
    }

    @Transactional(readOnly = true)
    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Transactional(readOnly = true)
    public Category findCategoryByIdWithHub(Long categoryId) {
        return categoryRepository.findByIdWithHub(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Transactional
    public void delete(Long hubId, Long categoryId, User currentUser) {
        Category category = findCategoryById(categoryId);

        if(!category.getHub().getId().equals(hubId)) {
            throw new EntityNotFoundException("Category not found");
        }

        if(!category.getHub().getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        categoryRepository.deleteById(categoryId);
    }
}
