package com.tomaz.finance.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tomaz.finance.dto.CategoryCreateDTO;
import com.tomaz.finance.dto.CategoryResponseDTO;
import com.tomaz.finance.dto.CategoryUpdateDTO;
import com.tomaz.finance.entities.Category;
import com.tomaz.finance.entities.User;
import com.tomaz.finance.enums.TransactionType;
import com.tomaz.finance.mapper.CategoryMapper;
import com.tomaz.finance.repositories.CategoryRepository;
import com.tomaz.finance.repositories.UserRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CategoryMapper categoryMapper;

	public List<CategoryResponseDTO> findAll(String username) {
		
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		
		List<Category> categories = categoryRepository.findByUser(user);
		
		return categoryMapper.categoriesFromCategoriesDTO(categories);
	}
	
	public CategoryResponseDTO findById(Long id) {
		Optional<Category> obj = categoryRepository.findById(id);

		return categoryMapper.toResponse(obj.get());
	}

	public CategoryResponseDTO create(CategoryCreateDTO dto, String username) {

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

	    Category category = categoryMapper.toEntity(dto);
	    category.setUser(user);

	    categoryRepository.save(category);
	    return categoryMapper.toResponse(category);
	}

	public CategoryResponseDTO update(Long id, CategoryUpdateDTO dto, String username) {

	    Category category = categoryRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

	    if (!category.getUser().getId().equals(user.getId())) {
	        throw new RuntimeException("Essa categoria não pertence a você.");
	    }

	    categoryMapper.updateFromDto(dto, category);

	    categoryRepository.save(category);
	    return categoryMapper.toResponse(category);
	}

	public void delete(Long id, String username) {
		
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
		
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		
		if(!category.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Essa categoria não pertence a você.");
		}

		categoryRepository.deleteById(id);
	}

}
