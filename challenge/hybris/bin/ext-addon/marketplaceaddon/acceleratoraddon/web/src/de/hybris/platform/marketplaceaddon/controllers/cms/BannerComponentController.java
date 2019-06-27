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
package de.hybris.platform.marketplaceaddon.controllers.cms;

import de.hybris.platform.acceleratorfacades.device.ResponsiveMediaFacade;
import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.marketplaceaddon.controllers.MarketplaceaddonControllerConstants;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS ComplexResponsiveBannerComponent
 */
@Controller("BannerComponentController")
@RequestMapping(value = MarketplaceaddonControllerConstants.Actions.Cms.BannerComponent)
public class BannerComponentController extends GenericMarketplaceCMSComponentController<BannerComponentModel>
{
	@Resource(name = "responsiveMediaFacade")
	private ResponsiveMediaFacade responsiveMediaFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final BannerComponentModel component)
	{
		super.fillModel(request, model, component);
		final MediaContainerModel mediaContainer = component.getMedia().getMediaContainer();
		final List<ImageData> images = responsiveMediaFacade.getImagesFromMediaContainer(mediaContainer);
		model.addAttribute("medias", images);
		model.addAttribute("urlLink", component.getUrlLink());
	}
}
