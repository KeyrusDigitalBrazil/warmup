package br.com.keyrus.warmup.fulfilmentprocess.dao.impl;

import br.com.keyrus.warmup.core.model.BlackListUsersModel;
import br.com.keyrus.warmup.fulfilmentprocess.dao.FraudCheckDao;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collections;
import java.util.List;

public class DefaultFraudCheckDao extends DefaultGenericDao<BlackListUsersModel> implements FraudCheckDao {

    public DefaultFraudCheckDao() {
        super(BlackListUsersModel._TYPECODE);
    }

    @Override
    public List<BlackListUsersModel> findBlockedCustomerByEmail(String email) {

        ServicesUtil.validateParameterNotNull(email, "email cannot be null");

        return this.find(Collections.singletonMap("email", email));

    }
}