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
/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.docu

/**
 * Aggregate information about web service request: resurce, httpMethod, accept(XML|JSON) and response
 */
class WSRequestSummary {
	def String resource;
	def String accept;
	def String httpMethod;
	def Object response;
}
