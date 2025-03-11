package com.trueinsurre.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.trueinsurre.dto.FilterDto;
import com.trueinsurre.modal.Task;

public interface TaskRepositoryCustom {
	Page<Task> findTasksByFilter(FilterDto filterDto, Pageable pageable);
}
