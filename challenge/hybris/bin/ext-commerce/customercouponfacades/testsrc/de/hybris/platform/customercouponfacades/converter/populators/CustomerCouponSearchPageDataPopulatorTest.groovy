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

package de.hybris.platform.customercouponfacades.converter.populators

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.servicelayer.data.PaginationData
import de.hybris.platform.core.servicelayer.data.SearchPageData
import de.hybris.platform.core.servicelayer.data.SortData
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponSearchPageData
import de.hybris.platform.customercouponservices.model.CustomerCouponModel
import de.hybris.platform.servicelayer.dto.converter.Converter

import org.junit.Test

import spock.lang.Specification

/**
 * Unit test for {@link CustomerCouponSearchPageDataPopulator}
 */
@UnitTest
class CustomerCouponSearchPageDataPopulatorTest extends Specification {

	CustomerCouponSearchPageDataPopulator populator

	Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter = Mock()
	SearchPageData<CustomerCouponModel> source = Mock()
	List<CustomerCouponModel> couponModels
	List<SortData> sorts = Mock()
	PaginationData pagination = Mock()
	CustomerCouponSearchPageData target
	CustomerCouponModel couponModel = Mock()
	CustomerCouponData couponData = Mock()

	def setup(){
		populator = new CustomerCouponSearchPageDataPopulator()
		populator.setCustomerCouponConverter(customerCouponConverter)

		target = new CustomerCouponSearchPageData()
		couponModels = Collections.singletonList(couponModel)
	}

	@Test
	def "test populate"(){
		setup:
		source.getResults() >> couponModels
		source.getSorts() >> sorts
		source.getPagination() >> pagination
		customerCouponConverter.convert(couponModel) >> couponData

		when:
		populator.populate(source, target)

		then:
		target.coupons[0] == couponData
		target.sorts == sorts
		target.pagination == pagination
	}
}
