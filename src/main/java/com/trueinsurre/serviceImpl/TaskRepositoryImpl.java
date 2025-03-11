package com.trueinsurre.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.trueinsurre.dto.FilterDto;
import com.trueinsurre.modal.Task;
import com.trueinsurre.modal.User;
import com.trueinsurre.repository.TaskRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

@Repository
public class TaskRepositoryImpl implements TaskRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<Task> findTasksByFilter(FilterDto filterDto, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Task> query = cb.createQuery(Task.class);
		Root<Task> root = query.from(Task.class);

		List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

		if (filterDto.getVehicleNumber() != null && filterDto.getVehicleNumber() != "") {
			predicates.add(cb.equal(root.get("vehicleNumber"), filterDto.getVehicleNumber()));
		}
		if (filterDto.getPartnerNumber() != null && filterDto.getPartnerNumber() != "") {
			predicates.add(cb.equal(root.get("partnerNumber"), filterDto.getPartnerNumber()));
		}
		if (filterDto.getAgentName() != null && filterDto.getAgentName() != "") {
			predicates.add(cb.equal(root.get("agentName"), filterDto.getAgentName()));
		}
		if (filterDto.getDriverName() != null && filterDto.getDriverName() != "") {
			predicates.add(cb.equal(root.get("driverName"), filterDto.getDriverName()));
		}
		if (filterDto.getCity() != null && filterDto.getCity() != "") {
			predicates.add(cb.equal(root.get("city"), filterDto.getCity()));
		}
		if (filterDto.getLastYearPolicyIssuedBy() != null && filterDto.getLastYearPolicyIssuedBy() != "") {
			predicates.add(cb.equal(root.get("lastYearPolicyIssuedBy"), filterDto.getLastYearPolicyIssuedBy()));
		}
		if (filterDto.getPartnerRate() != null && filterDto.getPartnerRate() != "") {
			predicates.add(cb.equal(root.get("partnerRate"), filterDto.getPartnerRate()));
		}
		if (filterDto.getNewExpiryDate() != null && filterDto.getNewExpiryDate() != "") {
			predicates.add(cb.equal(root.get("newExpiryDate"), filterDto.getNewExpiryDate()));
		}
		if (filterDto.getPolicyIssuedDate() != null && filterDto.getPolicyIssuedDate() != "") {
			predicates.add(cb.equal(root.get("policyIssuedDate"), filterDto.getPolicyIssuedDate()));
		}
		if (filterDto.getMessageStatus() != null && filterDto.getMessageStatus() != "") {
			predicates.add(cb.equal(root.get("messageStatus"), filterDto.getMessageStatus()));
		}
		if (filterDto.getDisposition() != null && filterDto.getDisposition() != "") {
			predicates.add(cb.equal(root.get("disposition"), filterDto.getDisposition()));
		}
		if (filterDto.getNextFollowUpDate() != null && filterDto.getNextFollowUpDate() != "") {
			predicates.add(cb.equal(root.get("nextFollowUpDate"), filterDto.getNextFollowUpDate()));
		}
		if (filterDto.getStatus() != null && filterDto.getStatus() != "") {
			predicates.add(cb.equal(root.get("status"), filterDto.getStatus()));
		}

		if (filterDto.getUserId() != null && filterDto.getUserId() != 0) {
		    Join<Task, User> userJoin = root.join("users"); 
		    predicates.add(cb.equal(userJoin.get("id"), filterDto.getUserId()));
		}


//        query.where(predicates.toArray(new Predicate[0]));
		query.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));

		TypedQuery<Task> typedQuery = entityManager.createQuery(query);

		// Pagination
		int totalRows = typedQuery.getResultList().size();
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		List<Task> taskList = typedQuery.getResultList();
		return new PageImpl<>(taskList, pageable, totalRows);
	}
}
