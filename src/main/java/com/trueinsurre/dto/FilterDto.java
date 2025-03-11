package com.trueinsurre.dto;

public class FilterDto {

	private Long userId;
	private String vehicleNumber;
	private String partnerNumber;
	private String agentName;
	private String driverName;
	private String city;
	private String lastYearPolicyIssuedBy;
	private String partnerRate;
	private String newExpiryDate;
	private String policyIssuedDate;
	private String messageStatus;
	private String disposition;
	private String nextFollowUpDate;
	private String status;
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long id) {
		this.userId = id;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "FilterDto [user_id=" + userId + ", vehicleNumber=" + vehicleNumber + ", partnerNumber=" + partnerNumber
				+ ", agentName=" + agentName + ", driverName=" + driverName + ", city=" + city
				+ ", lastYearPolicyIssuedBy=" + lastYearPolicyIssuedBy + ", partnerRate=" + partnerRate
				+ ", newExpiryDate=" + newExpiryDate + ", policyIssuedDate=" + policyIssuedDate + ", messageStatus="
				+ messageStatus + ", disposition=" + disposition + ", nextFollowUpDate=" + nextFollowUpDate
				+ ", status=" + status + "]";
	}
	

}
