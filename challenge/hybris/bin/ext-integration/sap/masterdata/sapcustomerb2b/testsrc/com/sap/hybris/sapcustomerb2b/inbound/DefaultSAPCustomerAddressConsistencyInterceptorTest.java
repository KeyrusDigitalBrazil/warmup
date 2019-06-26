/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.sapcustomerb2b.inbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;

import org.junit.Assert;
import org.junit.Test;

@UnitTest
public class DefaultSAPCustomerAddressConsistencyInterceptorTest
{
	@Test
	public void compareAddressDiffStreetName()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetname(VALUE1);
		target.setStreetname(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullStreetName()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetname(null);
		target.setStreetname(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameStreetName()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetname(VALUE1);
		target.setStreetname(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffStreetNumber()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetnumber(VALUE1);
		target.setStreetnumber(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullStreetNumber()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetnumber(null);
		target.setStreetnumber(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameStreetNumber()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetnumber(VALUE1);
		target.setStreetnumber(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffPostalCode()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPostalcode(VALUE1);
		target.setPostalcode(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullPostalCode()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPostalcode(null);
		target.setPostalcode(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSamePostalCode()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPostalcode(VALUE1);
		target.setPostalcode(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffTown()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setTown(VALUE1);
		target.setTown(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullTown()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setTown(null);
		target.setTown(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameTown()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setTown(VALUE1);
		target.setTown(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffCountry()
	{
		CountryModel c1 = new CountryModel();
		CountryModel c2 = new CountryModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setCountry(c1);
		target.setCountry(c2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullCountry()
	{
		CountryModel c2 = new CountryModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setCountry(null);
		target.setCountry(c2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameCountry()
	{
		CountryModel c1 = new CountryModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setCountry(c1);
		target.setCountry(c1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffRegion()
	{
		RegionModel r1 = new RegionModel();
		RegionModel r2 = new RegionModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setRegion(r1);
		target.setRegion(r2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullRegion()
	{
		RegionModel r2 = new RegionModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setRegion(null);
		target.setRegion(r2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameRegion()
	{
		RegionModel r1 = new RegionModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setRegion(r1);
		target.setRegion(r1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffPhone1()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPhone1(VALUE1);
		target.setPhone1(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullPhone1()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPhone1(null);
		target.setPhone1(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSamePhone1()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPhone1(VALUE2);
		target.setPhone1(VALUE2);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffFax()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setFax(VALUE1);
		target.setFax(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullFax()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setFax(null);
		target.setFax(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameFax()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setFax(VALUE1);
		target.setFax(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffPOBox()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPobox(VALUE1);
		target.setPobox(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullPOBox()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPobox(null);
		target.setPobox(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSamePOBox()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setPobox(VALUE1);
		target.setPobox(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffCellphone()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setCellphone(VALUE1);
		target.setCellphone(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullCellphone()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setCellphone(null);
		target.setCellphone(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameCellphone()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setCellphone(VALUE1);
		target.setCellphone(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiffDistrict()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setDistrict(VALUE1);
		target.setDistrict(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressNullDistrict()
	{
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setDistrict(null);
		target.setDistrict(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressSameDistrict()
	{
		final String VALUE1 = "value1";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setDistrict(VALUE1);
		target.setDistrict(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressDiff()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";
		RegionModel r1 = new RegionModel();
		RegionModel r2 = new RegionModel();
		CountryModel c1 = new CountryModel();
		CountryModel c2 = new CountryModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetname(VALUE1);
		target.setStreetname(VALUE2);
		source.setStreetnumber(VALUE1);
		target.setStreetnumber(VALUE2);
		source.setPostalcode(VALUE1);
		target.setPostalcode(VALUE2);
		source.setTown(VALUE1);
		target.setTown(VALUE2);
		source.setCountry(c1);
		target.setCountry(c2);
		source.setRegion(r1);
		target.setRegion(r2);
		source.setPhone1(VALUE1);
		target.setPhone1(VALUE2);
		source.setFax(VALUE1);
		target.setFax(VALUE2);
		source.setPobox(VALUE1);
		target.setPobox(VALUE2);
		source.setCellphone(VALUE1);
		target.setCellphone(VALUE2);
		source.setDistrict(VALUE1);
		target.setDistrict(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));
	}

	@Test
	public void compareAddressMatch()
	{
		final String VALUE1 = "value1";
		RegionModel r1 = new RegionModel();
		CountryModel c1 = new CountryModel();

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		source.setStreetname(VALUE1);
		target.setStreetname(VALUE1);
		source.setStreetnumber(VALUE1);
		target.setStreetnumber(VALUE1);
		source.setPostalcode(VALUE1);
		target.setPostalcode(VALUE1);
		source.setTown(VALUE1);
		target.setTown(VALUE1);
		source.setCountry(c1);
		target.setCountry(c1);
		source.setRegion(r1);
		target.setRegion(r1);
		source.setPhone1(VALUE1);
		target.setPhone1(VALUE1);
		source.setFax(VALUE1);
		target.setFax(VALUE1);
		source.setPobox(VALUE1);
		target.setPobox(VALUE1);
		source.setCellphone(VALUE1);
		target.setCellphone(VALUE1);
		source.setDistrict(VALUE1);
		target.setDistrict(VALUE1);

		Assert.assertFalse(interceptor.compareAddress(source, target));
	}
}
