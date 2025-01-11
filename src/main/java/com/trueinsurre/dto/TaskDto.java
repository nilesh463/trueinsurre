package com.trueinsurre.dto;

public class TaskDto {

	private Long id;
	private String vehicleNumber;
	private String partnerNumber;
	private String agentName;
	private String driverName;
	private String city;
	private String lastYearPolicyIssuedBy;
	private String partnerRate;
	private String newExpiryDate;
	private String message;
	private String messageLink;
	private String policyIssuedDate;
	private String messageStatus;
	private String disposition;
	private String nextFollowUpDate;
	private String comments;

	private boolean isAssign;
	private boolean isDeleted;
	private boolean isCompleted;
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getPartnerNumber() {
		return partnerNumber;
	}

	public void setPartnerNumber(String partnerNumber) {
		this.partnerNumber = partnerNumber;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLastYearPolicyIssuedBy() {
		return lastYearPolicyIssuedBy;
	}

	public void setLastYearPolicyIssuedBy(String lastYearPolicyIssuedBy) {
		this.lastYearPolicyIssuedBy = lastYearPolicyIssuedBy;
	}

	public String getPartnerRate() {
		return partnerRate;
	}

	public void setPartnerRate(String partnerRate) {
		this.partnerRate = partnerRate;
	}

	public String getNewExpiryDate() {
		return newExpiryDate;
	}

	public void setNewExpiryDate(String newExpiryDate) {
		this.newExpiryDate = newExpiryDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageLink() {
		return messageLink;
	}

	public void setMessageLink(String messageLink) {
		this.messageLink = messageLink;
	}

	public String getPolicyIssuedDate() {
		return policyIssuedDate;
	}

	public void setPolicyIssuedDate(String policyIssuedDate) {
		this.policyIssuedDate = policyIssuedDate;
	}

	public String getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public String getNextFollowUpDate() {
		return nextFollowUpDate;
	}

	public void setNextFollowUpDate(String nextFollowUpDate) {
		this.nextFollowUpDate = nextFollowUpDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isAssign() {
		return isAssign;
	}

	public void setAssign(boolean isAssign) {
		this.isAssign = isAssign;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "TaskDto [id=" + id + ", vehicleNumber=" + vehicleNumber + ", partnerNumber=" + partnerNumber
				+ ", agentName=" + agentName + ", driverName=" + driverName + ", city=" + city
				+ ", lastYearPolicyIssuedBy=" + lastYearPolicyIssuedBy + ", partnerRate=" + partnerRate
				+ ", newExpiryDate=" + newExpiryDate + ", message=" + message + ", messageLink=" + messageLink
				+ ", policyIssuedDate=" + policyIssuedDate + ", messageStatus=" + messageStatus + ", disposition="
				+ disposition + ", nextFollowUpDate=" + nextFollowUpDate + ", comments=" + comments + ", isAssign="
				+ isAssign + ", isDeleted=" + isDeleted + ", isCompleted=" + isCompleted + ", status=" + status + "]";
	}

}
