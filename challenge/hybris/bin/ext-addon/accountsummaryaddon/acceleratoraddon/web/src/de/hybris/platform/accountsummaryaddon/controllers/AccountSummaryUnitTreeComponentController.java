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
package de.hybris.platform.accountsummaryaddon.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.cms.AbstractCMSComponentController;
import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.model.AccountSummaryUnitTreeComponentModel;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller to display the AccountSummaryUnitTree component
 *
 */
@Controller("AccountSummaryUnitTreeComponentController")
@RequestMapping(value = "/view/AccountSummaryUnitTreeComponentController")
public class AccountSummaryUnitTreeComponentController extends
		AbstractCMSComponentController<AccountSummaryUnitTreeComponentModel>
{
	@Resource(name = "b2bUnitFacade")
	protected B2BUnitFacade b2bUnitFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AccountSummaryUnitTreeComponentModel component)
	{
		final B2BUnitNodeData rootNode = b2bUnitFacade.getParentUnitNode();
		model.addAttribute("rootNode", rootNode);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
	}

	@Override
	protected String getView(final AccountSummaryUnitTreeComponentModel component)
	{
		return AccountsummaryaddonConstants.ACCOUNT_SUMMARY_UNIT_TREE_PAGE;
	}
}