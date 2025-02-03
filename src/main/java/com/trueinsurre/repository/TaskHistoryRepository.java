package com.trueinsurre.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trueinsurre.modal.TaskHistory;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long>{

	@Query("SELECT h FROM TaskHistory h WHERE h.task.id = :taskId ORDER BY h.updatedAt ASC")
	List<TaskHistory> findByTaskId(@Param("taskId") Long taskId);

}
