/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.warehousing.util.builder.RefundEntryModelBuilder;
import de.hybris.platform.warehousing.util.builder.ReturnRequestModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

public class ReturnRequests extends AbstractItems<ReturnRequestModel> {
	public static final String CODE_CAMERA_RETURN = "camera-return";
	public static final BigDecimal REFUND_AMOUNT = new BigDecimal(50.00);
	public static final Long REFUND_EXPECTED_QUANTITY = 1L;

	private Orders orders;
	private WarehousingDao<ConsignmentModel> warehousingConsignmentDao;
	private WarehousingDao<ReturnRequestModel> warehousingReturnRequestDao;

	public ReturnRequestModel Camera_OnlineReturn(final Long quantity) {
		return getOrSaveAndReturn(() -> getWarehousingReturnRequestDao().getByCode(CODE_CAMERA_RETURN), () -> {
			final RefundEntryModel entry = Camera(quantity, ReturnAction.HOLD);
			final ReturnRequestModel returnRequest = ReturnRequestModelBuilder.aModel().withCode(CODE_CAMERA_RETURN)
					.withReturnEntries((ReturnEntryModel) entry)
					.withOrder((OrderModel) (entry.getOrderEntry().getOrder())).build();
			entry.setReturnRequest(returnRequest);
			return returnRequest;
		});
	}

	public RefundEntryModel Camera(final Long quantity, final ReturnAction returnAction) {
		return RefundEntryModelBuilder.aModel().withOrderEntry(getOrders().Camera_Shipped(quantity).getEntries().get(0))
				.withReason(RefundReason.WRONGDESCRIPTION).withAmount(REFUND_AMOUNT)
				.withExpectedQTY(REFUND_EXPECTED_QUANTITY).withStatus(ReturnStatus.RECEIVED)
				.withAction(returnAction).build();
	}

	protected WarehousingDao<ConsignmentModel> getWarehousingConsignmentDao() {
		return warehousingConsignmentDao;
	}

	@Required
	public void setWarehousingConsignmentDao(final WarehousingDao<ConsignmentModel> warehousingConsignmentDao) {
		this.warehousingConsignmentDao = warehousingConsignmentDao;
	}

	protected WarehousingDao<ReturnRequestModel> getWarehousingReturnRequestDao() {
		return warehousingReturnRequestDao;
	}

	@Required
	public void setWarehousingReturnRequestDao(final WarehousingDao<ReturnRequestModel> warehousingReturnRequestDao) {
		this.warehousingReturnRequestDao = warehousingReturnRequestDao;
	}

	protected Orders getOrders() {
		return orders;
	}

	@Required
	public void setOrders(final Orders orders) {
		this.orders = orders;
	}

}
