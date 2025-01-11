package com.trueinsurre.dto;

public class EmployeeSelectDto {

	private Long id;
	private String emp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmp() {
		return emp;
	}

	public void setEmp(String emp) {
		this.emp = emp;
	}

	@Override
	public String toString() {
		return "EmployeeSelectDto [id=" + id + ", emp=" + emp + "]";
	}

}
