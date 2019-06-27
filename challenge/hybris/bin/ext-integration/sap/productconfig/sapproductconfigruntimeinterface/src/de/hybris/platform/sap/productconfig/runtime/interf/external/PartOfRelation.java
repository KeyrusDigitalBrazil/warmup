/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external;

/**
 * Represents instance/ sub instance relation, which is named 'PartOf'
 */
public interface PartOfRelation
{

	/**
	 * Sets author
	 * 
	 * @param author
	 */
	void setAuthor(String author);


	/**
	 * @return Author
	 */
	String getAuthor();

	/**
	 * Sets class type, one typical value is '300'
	 * 
	 * @param classType
	 */
	void setClassType(String classType);

	/**
	 * @return class type
	 */
	String getClassType();

	/**
	 * Sets object key, for material items this is the product ID.
	 * 
	 * @param objectKey
	 */
	void setObjectKey(String objectKey);

	/**
	 * @return Object key.
	 */
	String getObjectKey();

	/**
	 * Sets object type, product or an abstract product representative
	 * 
	 * @param objectType
	 */
	void setObjectType(String objectType);

	/**
	 * @return Object type
	 */
	String getObjectType();

	/**
	 * Sets position in the BOM
	 * 
	 * @param posNr
	 */
	void setPosNr(String posNr);

	/**
	 * @return Position number
	 */
	String getPosNr();

	/**
	 * Sets parent instance ID
	 * 
	 * @param parentInstId
	 */
	void setParentInstId(String parentInstId);

	/**
	 * @return Parent instance ID
	 */
	String getParentInstId();

	/**
	 * Sets child instance ID
	 * 
	 * @param instId
	 */
	void setInstId(String instId);

	/**
	 * @return Child instance ID
	 */
	String getInstId();
}
