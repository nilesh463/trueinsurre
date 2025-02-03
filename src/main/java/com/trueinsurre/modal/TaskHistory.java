package com.trueinsurre.modal;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TaskHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "task_id", nullable = false)
	private Task task;

	private String fieldName;
	private String oldValue;
	private String newValue;
	private LocalDateTime updatedAt;

	public TaskHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TaskHistory(Long id, Task task, String fieldName, String oldValue, String newValue,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.task = task;
		this.fieldName = fieldName;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "TaskHistory [id=" + id + ", task=" + task + ", fieldName=" + fieldName + ", oldValue=" + oldValue
				+ ", newValue=" + newValue + ", updatedAt=" + updatedAt + "]";
	}

}
