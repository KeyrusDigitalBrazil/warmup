package br.com.keyrus.warmup.core.stamp.dao;

import br.com.keyrus.warmup.core.model.StampModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

public interface StampDAO {

    List<StampModel> findProductStamps(final ProductModel product);

}
