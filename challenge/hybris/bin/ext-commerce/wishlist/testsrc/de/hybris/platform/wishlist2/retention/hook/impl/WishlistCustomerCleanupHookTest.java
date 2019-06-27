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
package de.hybris.platform.wishlist2.retention.hook.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.directpersistence.audit.dao.WriteAuditRecordsDAO;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class WishlistCustomerCleanupHookTest
{
	@InjectMocks
	private final WishlistCustomerCleanupHook customerCleanupHook = new WishlistCustomerCleanupHook();

	@Mock
	private ModelService modelService;
	@Mock
	private WriteAuditRecordsDAO writeAuditRecordsDAO;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCleanupRelatedObjects()
	{
		final CustomerModel orderModel = mock(CustomerModel.class);
		final Wishlist2Model wishlist2Model = mock(Wishlist2Model.class);
		final List<Wishlist2Model> wishlists = Collections.singletonList(wishlist2Model);
		given(orderModel.getWishlist()).willReturn(wishlists);
		final PK wishlist2ModelPK = PK.parse("1111");
		given(wishlist2Model.getPk()).willReturn(wishlist2ModelPK);

		customerCleanupHook.cleanupRelatedObjects(orderModel);
		verify(modelService).remove(wishlist2Model);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(Wishlist2Model._TYPECODE, wishlist2ModelPK);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCleanupRelatedObjectsIfInputIsNull()
	{
		customerCleanupHook.cleanupRelatedObjects(null);
	}
}
