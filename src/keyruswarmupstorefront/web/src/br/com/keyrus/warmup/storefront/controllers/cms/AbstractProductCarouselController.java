package br.com.keyrus.warmup.storefront.controllers.cms;

import de.hybris.platform.acceleratorfacades.productcarousel.ProductCarouselFacade;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbstractProductCarouselController<T extends ProductCarouselComponentModel> extends AbstractAcceleratorCMSComponentController<T>{

    @Resource(name = "productSearchFacade")
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Resource(name = "productCarouselFacade")
    private ProductCarouselFacade productCarouselFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final T component)
    {
        final List<ProductData> products = new ArrayList<>();

        products.addAll(collectLinkedProducts(component));
        products.addAll(collectSearchProducts(component));

        model.addAttribute("title", component.getTitle());
        model.addAttribute("productData", products);
    }

    protected List<ProductData> collectLinkedProducts(final ProductCarouselComponentModel component)
    {
        return productCarouselFacade.collectProducts(component);
    }

    protected List<ProductData> collectSearchProducts(final ProductCarouselComponentModel component)
    {
        final SearchQueryData searchQueryData = new SearchQueryData();
        searchQueryData.setValue(component.getSearchQuery());
        final String categoryCode = component.getCategoryCode();

        if (searchQueryData.getValue() != null && categoryCode != null)
        {
            final SearchStateData searchState = new SearchStateData();
            searchState.setQuery(searchQueryData);

            final PageableData pageableData = new PageableData();
            pageableData.setPageSize(100); // Limit to 100 matching results

            return productSearchFacade.categorySearch(categoryCode, searchState, pageableData).getResults();
        }

        return Collections.emptyList();
    }
}
