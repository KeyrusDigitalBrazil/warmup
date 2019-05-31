package br.com.keyrus.warmup.storefront.controllers.cms;

import br.com.keyrus.warmup.storefront.controllers.ControllerConstants;
import de.hybris.platform.cms2lib.model.components.ProductShopWindowComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


@Controller("ProductShopWindowComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.ProductShopWindowComponent)
public class ProductShopWindowComponentController extends AbstractProductCarouselController<ProductShopWindowComponentModel>
{
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ProductShopWindowComponentModel component)
	{
		super.fillModel(request, model, component);

		model.addAttribute("amountPerRow", component.getAmountPerRow());
	}
}
