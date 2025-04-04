package com.trueinsurre.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trueinsurre.modal.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
	
	Page<Task> findByIsAssignAndIsDeleted(boolean isAssign, boolean isDeleted, Pageable pageable);
    Page<Task> findByIsAssignAndIsCompletedAndIsDeleted(boolean isAssign, boolean isCompleted, boolean isDeleted, Pageable pageable);
    Page<Task> findByUsers_IdAndIsAssignAndIsDeleted(Long userId, boolean isAssign, boolean isDeleted, Pageable pageable);
    Page<Task> findByUsers_IdAndIsAssignAndIsCompletedAndIsDeleted(Long userId, boolean isAssign, boolean isCompleted, boolean isDeleted, Pageable pageable);
	Page<Task> findByIsAssignAndIsCompletedAndIsDeletedAndStatus(boolean isAssign, boolean isCompleted,
			boolean isDeleted, String status, Pageable pageable);
	Page<Task> findByIsAssignAndIsCompletedAndIsDeletedAndDisposition(boolean isAssign, boolean isCompleted,
					boolean isDeleted, String status, Pageable pageable);
	Page<Task> findByIsAssignAndIsCompletedAndIsDeletedAndDispositionAndStatus(boolean isAssign, boolean isCompleted,
			boolean isDeleted,String disposition, String status, Pageable pageable);
	
	Page<Task> findByUsers_IdAndIsAssignAndIsCompletedAndIsDeletedAndStatus(Long userId, boolean isAssign, boolean isCompleted,
			boolean isDeleted, String status, Pageable pageable);
	Page<Task> findByUsers_IdAndIsAssignAndIsCompletedAndIsDeletedAndDisposition(Long userId, boolean isAssign, boolean isCompleted,
					boolean isDeleted, String status, Pageable pageable);
	Page<Task> findByUsers_IdAndIsAssignAndIsCompletedAndIsDeletedAndDispositionAndStatus(Long userId, boolean isAssign, boolean isCompleted,
			boolean isDeleted,String disposition, String status, Pageable pageable);
	boolean existsByVehicleNumber(String vehicleNumber);

}
