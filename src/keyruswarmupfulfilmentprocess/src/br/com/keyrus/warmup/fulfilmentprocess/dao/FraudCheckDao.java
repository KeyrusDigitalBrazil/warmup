package br.com.keyrus.warmup.fulfilmentprocess.dao;

import br.com.keyrus.warmup.core.model.BlackListUsersModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.List;


/**
 * The {@link BlackListUsersModel} DAO.
 *
 * @spring.bean fraudCheckDao
 */
public interface FraudCheckDao extends GenericDao<BlackListUsersModel>
{
    /**
     * Returns {@link BlackListUsersModel} with the given email.
     *
     * @param email
     *           customer email
     * @return Matching {@link BlackListUsersModel}
     * @throws IllegalArgumentException
     *            if email was null.
     * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
     *            if no {@link BlackListUsersModel} with the given id could be found.
     */
    List<BlackListUsersModel> findBlockedCustomerByEmail(String email);

}