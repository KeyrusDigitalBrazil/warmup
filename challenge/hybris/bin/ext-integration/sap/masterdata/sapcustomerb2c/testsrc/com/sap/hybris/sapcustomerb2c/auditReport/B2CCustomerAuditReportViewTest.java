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
package com.sap.hybris.sapcustomerb2c.auditReport;


import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableTest;
import de.hybris.platform.audit.TypeAuditReportConfig;
import de.hybris.platform.audit.demo.AuditTestConfigManager;
import de.hybris.platform.audit.internal.config.AtomicAttribute;
import de.hybris.platform.audit.internal.config.AuditReportConfig;
import de.hybris.platform.audit.internal.config.Type;
import de.hybris.platform.audit.view.AuditViewService;
import de.hybris.platform.audit.view.impl.ReportView;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.directpersistence.audit.internal.AuditEnablementService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class B2CCustomerAuditReportViewTest extends ServicelayerTransactionalBaseTest implements AuditableTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private AuditViewService auditViewService;

	private AuditTestConfigManager auditTestConfigManager;

	@Resource
	private AuditEnablementService auditEnablementService;

	@Before
	public void setUp() throws Exception
	{
		auditTestConfigManager = new AuditTestConfigManager(auditEnablementService);
		auditTestConfigManager.enableAuditingForTypes("User");
	}

	@Test
	public void b2CCustomerReportViewTest()
	{
		final CustomerModel customer = modelService.create(CustomerModel.class);
		customer.setUid("customeruid");
		customer.setCustomerID("customerid");
		customer.setName("customer");
		customer.setSapIsReplicated(true);
		customer.setSapContactID("customercontactid");
		customer.setSapConsumerID("consumerid");
		customer.setSapReplicationInfo("sapReplicationInfo");
		modelService.save(customer);

		final List<ReportView> reportViews = auditViewService
				.getViewOn(TypeAuditReportConfig.builder().withConfig(createCustomerConfig()).withRootTypePk(customer.getPk()).withFullReport().build())
				.collect(toList());

		assertThat(reportViews).hasSize(1);
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("User").extracting("sapReplicationInfo")
				.contains("sapReplicationInfo");
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("User").extracting("uid")
				.contains("customeruid");
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("User").extracting("customerID")
				.contains("customerid");
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("User").extracting("sapConsumerID").contains("consumerid");
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("User").extracting("sapIsReplicated").contains(true);
		assertThat(reportViews).extracting(ReportView::getPayload).extracting("User").extracting("sapContactID").contains("customercontactid");
	}

	private AuditReportConfig createCustomerConfig()
	{
		final Type customer = Type.builder().withCode("User") //
				.withAtomicAttributes( //
									   AtomicAttribute.builder().withQualifier("uid").build(), //
									   AtomicAttribute.builder().withQualifier("sapReplicationInfo").build() , //
									   AtomicAttribute.builder().withQualifier("sapContactID").build() , //
									   AtomicAttribute.builder().withQualifier("sapConsumerID").build() , //
									   AtomicAttribute.builder().withQualifier("customerID").build() , //
									   AtomicAttribute.builder().withQualifier("sapIsReplicated").build()  //

				) //
				.build();

		final AuditReportConfig reportConfig = AuditReportConfig.builder() //
				.withGivenRootType(customer) //
				.withName("PersonalDataReport") //
				.withTypes(customer) //
				.build();

		return reportConfig;
	}
}
