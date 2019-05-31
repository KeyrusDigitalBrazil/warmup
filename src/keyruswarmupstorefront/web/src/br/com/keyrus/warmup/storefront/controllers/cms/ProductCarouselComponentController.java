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
package br.com.keyrus.warmup.storefront.controllers.cms;

import br.com.keyrus.warmup.storefront.controllers.ControllerConstants;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * Controller for CMS ProductReferencesComponent.
 */
@Controller("ProductCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.ProductCarouselComponent)
public class ProductCarouselComponentController extends AbstractProductCarouselController<ProductCarouselComponentModel>
{
	@Override
	protected void fillModel(HttpServletRequest request, Model model, ProductCarouselComponentModel component) {
		super.fillModel(request, model, component);
	}
}
