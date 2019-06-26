/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * A service around {@link B2BPermissionResultModel}. This interface evaluates the permissions(credit limits or budget
 * thresholds) on an order to determine if the order needs to be sent for approval. If the order needs to be approved,
 * the associated approver is applied to the permission so that an Approve or Reject decision on the order can be made.
 *
 * @param <T>
 *           the user (subtype of {@link UserModel})
 * @param <P>
 *           the permission result (subtype of {@link B2BPermissionResultModel})
 * @spring.bean b2bPermissionService
 */
public interface B2BPermissionService<T extends UserModel, P extends B2BPermissionResultModel>
{

	/**
	 * Evaluate permissions of an order to determine if a violation has occurred and that an approver needs to intervene
	 * and approve/reject an order.
	 *
	 * @param order
	 *           the order to be evaluated
	 * @param employee
	 *           the person who placed the order
	 * @param permissionTypes
	 *           the permission types that will be checked
	 * @return the set of {@link B2BPermissionResultModel}
	 */
	abstract Set<P> evaluatePermissions(final AbstractOrderModel order, final T employee,
			final List<Class<? extends B2BPermissionModel>> permissionTypes);

	/**
	 * @deprecated As of hybris 4.4, replaced by
	 *             {@link #getApproversForOpenPermissions(AbstractOrderModel, UserModel, Collection)}
	 * @param order
	 *           the order
	 * @param customer
	 *           the customer
	 * @param openPermissions
	 *           permissions that need approval
	 * @return the set of {@link B2BPermissionResultModel}
	 */
	@Deprecated
	abstract Set<P> findApproversForOpenPermissions(final AbstractOrderModel order, final T customer,
			final Collection<P> openPermissions);

	/**
	 * Get the approvers for an order's associated permissions with a status of OPEN. Approvers are assigned to a
	 * customer or it's unit and this evaluates/gathers the approvers for all units up the hierarchy.
	 *
	 * @param order
	 *           the order
	 * @param customer
	 *           the customer whom placed the order
	 * @param openPermissions
	 *           the permissions to be checked for status of OPEN
	 * @return B2BPermissionResult the set of approvers
	 */
	abstract Set<P> getApproversForOpenPermissions(final AbstractOrderModel order, final T customer,
			final Collection<P> openPermissions);

	/**
	 * Gets permissions with OPEN status.
	 *
	 * @param order
	 *           the order
	 * @return the open permissions
	 */
	abstract List<P> getOpenPermissions(final AbstractOrderModel order);


	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getB2BPermissionForCode(String)}
	 *
	 * @param code
	 *           the permission code
	 * @return the permission
	 */
	@Deprecated
	abstract B2BPermissionModel findB2BPermissionByCode(final String code);

	/**
	 * Gets the b2b permission for the code provided.
	 *
	 * @param code
	 *           the code
	 * @return the b2b permission model
	 */
	abstract B2BPermissionModel getB2BPermissionForCode(final String code);

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getAllB2BPermissions()}
	 *
	 * @return the set of {@link B2BPermissionModel}
	 */
	@Deprecated
	abstract Set<B2BPermissionModel> findAllB2BPermissions();

	/**
	 * Get all b2b permissions.
	 *
	 * @return the set of B2BPermissionModel
	 */
	abstract Set<B2BPermissionModel> getAllB2BPermissions();


	/**
	 * Determine if a b2b permission exists based on a code.
	 *
	 * @param code
	 *           the code
	 * @return True if permission exists
	 */
	boolean permissionExists(String code);

	/**
	 * Checks if the order requires approval by someone other than customer who placed the order.
	 *
	 * @param order
	 *           A b2b order.
	 * @return True if order needs approval.
	 */
	abstract boolean needsApproval(AbstractOrderModel order);


	/**
	 * Get all approvers who have permissions to approve the order.
	 *
	 * @param order
	 *           A b2b order
	 * @return approvers who are eligible to approve the order.
	 */
	abstract Map<T, P> getEligableApprovers(final OrderModel order);

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getAllB2BPermissionTypes()}
	 *
	 * @return the {@link List} of permission types
	 */
	@Deprecated
	List<String> findAllB2BPermissionTypes();

	/**
	 * Get all permission types
	 *
	 * @return permission types list
	 */
	List<String> getAllB2BPermissionTypes();
}
