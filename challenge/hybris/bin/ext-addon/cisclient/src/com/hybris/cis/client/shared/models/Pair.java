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
package com.hybris.cis.client.shared.models;

/**
 * This class can be used to return a combination of generic types.
 * 
 */
public class Pair<K, V>
{
	private K key;
	private V value;

	public Pair()
	{
		this.key = null; //NOPMD
		this.value = null; //NOPMD
	}

	/**
	 * Instantiates a new Pair.
	 * 
	 * @param key a generic type to be used as key
	 * @param value a generic type to be used as value
	 */
	public Pair(final K key, final V value)
	{
		this.key = key;
		this.value = value;
	}

	public K getKey()
	{
		return this.key;
	}

	public void setKey(final K key)
	{
		this.key = key;
	}

	public V getValue()
	{
		return this.value;
	}

	public void setValue(final V value)
	{
		this.value = value;
	}

	/**
	 * Returns a new pair of the given generic type combination.
	 * 
	 * @param key the key generic object
	 * @param value the value generic object
	 * @return a new pair of key and value
	 */
	public static <K, V> Pair<K, V> newInstance(final K key, final V value)
	{
		return new Pair<K, V>(key, value);
	}
}
