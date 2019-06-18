package br.com.keyrus.warmup.core.dao.impl;

import br.com.keyrus.warmup.core.dao.StampDAO;
import br.com.keyrus.warmup.core.model.StampModel;
import de.hybris.platform.category.constants.CategoryConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.log4j.Logger;

import java.util.List;

public class StampDAOImpl  extends AbstractItemDao implements StampDAO {

    private static final String QUERY_LIST_STAMP_BY_PRODUCT = new StringBuilder("SELECT * from {")
            .append(StampModel._TYPECODE)
            .append(" as s JOIN ")
            .append(CategoryModel._TYPECODE)
            .append(" as c on {c:")
            .append(CategoryModel.STAMP)
            .append("} = {s:")
            .append(StampModel.PK)
            .append("} JOIN ")
            .append(CategoryConstants.Relations.CATEGORYPRODUCTRELATION)
            .append(" as r on {r:")
            .append(LinkModel.SOURCE)
            .append("} = {c:")
            .append(CategoryModel.PK)
            .append("}  } where {r:")
            .append(LinkModel.TARGET)
            .append("} = ?product")
            .toString();
    private static final Logger LOGGER = Logger.getLogger(StampDAOImpl.class);

    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<StampModel> listStampByProduct(ProductModel product) {
        LOGGER.info("retrieving stamp by product " + product);
        FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_LIST_STAMP_BY_PRODUCT);
        query.addQueryParameter("product", product);
        SearchResult<StampModel> result = flexibleSearchService.search(query);
        return result.getResult();
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
