package com.ryabaya.cheese.service;

import com.ryabaya.cheese.dto.request.CategoryRequestDto;
import com.ryabaya.cheese.dto.response.CategoryResponseDto;
import com.ryabaya.cheese.entity.Category;
import com.ryabaya.cheese.entity.Cheese;
import com.ryabaya.cheese.exception.ResourceNotFoundException;
import com.ryabaya.cheese.mapper.CategoryMapper;
import com.ryabaya.cheese.repository.CategoryRepository;
import com.ryabaya.cheese.repository.CheeseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CheeseRepository cheeseRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Cheese cheese;
    private Category category;
    private CategoryRequestDto categoryRequestDto;
    private CategoryResponseDto categoryResponseDto;

    @BeforeEach
    void setUp() {
        cheese = new Cheese();
        cheese.setId(1L);
        cheese.setName("ÐŸÐ°Ñ€Ð¼ÐµÐ·Ð°Ð½");
        cheese.setCategories(new HashSet<>());

        category = new Category();
        category.setId(1L);
        category.setName("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");
        category.setDescription("Ð¡Ñ‹Ñ€Ñ‹ Ñ Ð½Ð¸Ð·ÐºÐ¸Ð¼ ÑÐ¾Ð´ÐµÑ€Ð¶Ð°Ð½Ð¸ÐµÐ¼ Ð²Ð»Ð°Ð³Ð¸");
        category.setCheeses(new HashSet<>());

        categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");
        categoryRequestDto.setDescription("Ð¡Ñ‹Ñ€Ñ‹ Ñ Ð½Ð¸Ð·ÐºÐ¸Ð¼ ÑÐ¾Ð´ÐµÑ€Ð¶Ð°Ð½Ð¸ÐµÐ¼ Ð²Ð»Ð°Ð³Ð¸");

        categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(1L);
        categoryResponseDto.setName("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");
        categoryResponseDto.setDescription("Ð¡Ñ‹Ñ€Ñ‹ Ñ Ð½Ð¸Ð·ÐºÐ¸Ð¼ ÑÐ¾Ð´ÐµÑ€Ð¶Ð°Ð½Ð¸ÐµÐ¼ Ð²Ð»Ð°Ð³Ð¸");
    }

    @Test
    void createCategory_ShouldReturnCategoryResponseDto_WhenCheeseExists() {
        when(cheeseRepository.findById(1L)).thenReturn(Optional.of(cheese));
        when(categoryMapper.toEntity(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.createCategory(1L, categoryRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");
        verify(cheeseRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
        assertThat(cheese.getCategories()).contains(category);
        assertThat(category.getCheeses()).contains(cheese);
    }

    @Test
    void createCategory_ShouldThrowException_WhenCheeseNotFound() {
        when(cheeseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.createCategory(99L, categoryRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cheese not found with id: 99");

        verify(cheeseRepository).findById(99L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getCategoryById_ShouldReturnCategoryResponseDto_WhenCategoryExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.getCategoryById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id : 99");

        verify(categoryRepository).findById(99L);
    }

    @Test
    void getCategoryByName_ShouldReturnCategoryResponseDto_WhenCategoryExists() {
        when(categoryRepository.findByName("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹")).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.getCategoryByName("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");
        verify(categoryRepository).findByName("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");
    }

    @Test
    void getCategoryByName_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findByName("ÐÐµÑÑƒÑ‰ÐµÑÑ‚Ð²ÑƒÑŽÑ‰Ð°Ñ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryByName("ÐÐµÑÑƒÑ‰ÐµÑÑ‚Ð²ÑƒÑŽÑ‰Ð°Ñ"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id : ÐÐµÑÑƒÑ‰ÐµÑÑ‚Ð²ÑƒÑŽÑ‰Ð°Ñ");

        verify(categoryRepository).findByName("ÐÐµÑÑƒÑ‰ÐµÑÑ‚Ð²ÑƒÑŽÑ‰Ð°Ñ");
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        List<Category> categories = List.of(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertThat(result).hasSize(1);
        verify(categoryRepository).findAll();
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategoryResponseDto_WhenCategoryExists() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Ð¡Ñ‚Ð°Ñ€Ð°Ñ ÐºÐ°Ñ‚ÐµÐ³Ð¾Ñ€Ð¸Ñ");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        doAnswer(invocation -> {
            existingCategory.setName(categoryRequestDto.getName());
            existingCategory.setDescription(categoryRequestDto.getDescription());
            return null;
        }).when(categoryMapper).updateEntityFromDto(existingCategory, categoryRequestDto);
        when(categoryRepository.save(existingCategory)).thenReturn(category);
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        CategoryResponseDto result = categoryService.updateCategory(1L, categoryRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Ð¢Ð²ÐµÑ€Ð´Ñ‹Ðµ ÑÑ‹Ñ€Ñ‹");
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).updateEntityFromDto(existingCategory, categoryRequestDto);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(99L, categoryRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id : 99");

        verify(categoryRepository).findById(99L);
        verify(categoryMapper, never()).updateEntityFromDto(any(), any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_ShouldRemoveCategory_WhenCategoryExists() {
        Category categoryToDelete = new Category();
        categoryToDelete.setId(1L);
        categoryToDelete.setCheeses(new HashSet<>());
        categoryToDelete.getCheeses().add(cheese);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryToDelete));
        doNothing().when(categoryRepository).delete(categoryToDelete);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(categoryToDelete);
        assertThat(cheese.getCategories()).doesNotContain(categoryToDelete);
        assertThat(categoryToDelete.getCheeses()).isEmpty();
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id : 99");

        verify(categoryRepository).findById(99L);
        verify(categoryRepository, never()).delete(any());
    }
}
