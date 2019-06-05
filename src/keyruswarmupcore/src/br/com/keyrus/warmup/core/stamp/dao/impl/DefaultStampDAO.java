package br.com.keyrus.warmup.core.stamp.dao.impl;

import br.com.keyrus.warmup.core.model.StampModel;
import br.com.keyrus.warmup.core.stamp.dao.StampDAO;
import de.hybris.platform.category.constants.CategoryConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultStampDAO extends AbstractItemDao implements StampDAO {

    @Override
    public List<StampModel> findProductStamps(final ProductModel product) {

        final Map params = new HashMap();
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SELECT DISTINCT {c:").append(CategoryModel.STAMP).append("} ");
        stringBuilder.append("FROM {").append(CategoryModel._TYPECODE).append(" AS c ");
        stringBuilder.append("JOIN ").append(CategoryConstants.Relations.CATEGORYPRODUCTRELATION).append(" AS cpr ");
        stringBuilder.append("ON {cpr:").append(LinkModel.TARGET).append("}=?product } WHERE {c:")
                .append(CategoryModel.STAMP).append("} IS NOT NULL");

        params.put("product", product);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(stringBuilder.toString());
        query.addQueryParameters(params);

        return new ArrayList<>(getFlexibleSearchService().<StampModel> search(query).getResult());
    }
}
