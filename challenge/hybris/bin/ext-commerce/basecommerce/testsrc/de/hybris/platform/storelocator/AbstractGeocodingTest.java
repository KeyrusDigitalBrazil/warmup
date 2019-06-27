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
package de.hybris.platform.storelocator;

import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.cronjob.constants.CronJobConstants;
import de.hybris.platform.cronjob.jalo.CronJobManager;
import de.hybris.platform.cronjob.jalo.Job;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.jalo.GeocodeAddressesCronJob;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.LocationService;
import de.hybris.platform.storelocator.location.impl.LocationDTO;
import de.hybris.platform.storelocator.location.impl.LocationDtoWrapper;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.route.RouteService;
import de.hybris.platform.testframework.Transactional;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;


/**
 *
 *
 */
@Transactional
public abstract class AbstractGeocodingTest extends BaseCommerceBaseTest
{

	@Resource
	private LocationService locationService;

	@Resource
	private GeocodingJob geocodeAddressesJob;

	@Resource
	private CronJobService cronJobService;

	@Resource
	private ModelService modelService;

	@Resource
	private I18NService i18nService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private PointOfServiceDao pointOfServiceDao;

	@Resource
	private MapService mapService;
	@Resource
	private RouteService routeService;

	@Resource
	private GeoWebServiceWrapper geoServiceWrapper;



	protected void createTestCronJob(final Integer batchSize, final Integer internalDelay)
			throws JaloGenericCreationException, JaloAbstractTypeException
	{
		final CronJobManager cronjobManager = (CronJobManager) JaloSession.getCurrentSession().getExtensionManager()
				.getExtension(CronJobConstants.EXTENSIONNAME);
		final Job job = cronjobManager.createBatchJob("job");

		final ComposedType geocodeAddressesCronJob = jaloSession.getTypeManager().getComposedType(GeocodeAddressesCronJob.class);

		final Map<String, Object> values = new HashMap<String, Object>();
		values.put(GeocodeAddressesCronJob.CODE, "testCronJob");
		values.put(GeocodeAddressesCronJob.BATCHSIZE, batchSize);
		values.put(GeocodeAddressesCronJob.INTERNALDELAY, internalDelay);
		values.put(GeocodeAddressesCronJob.ACTIVE, Boolean.FALSE);
		values.put(GeocodeAddressesCronJob.JOB, job);
		geocodeAddressesCronJob.newInstance(values);
	}

	protected void createTestCronJob() throws JaloGenericCreationException, JaloAbstractTypeException
	{
		createTestCronJob(Integer.valueOf(100), Integer.valueOf(1));
	}

	protected void createTestPosEntries() throws Exception
	{
		importCsv("/import/test/PointOfServiceLocationsImport.csv", "UTF-8");
	}

	protected Location createAndStoreTestLocation(final String name, final String street, final String buildingNo,
			final String postalCode, final String city, final String countryIsoCode) throws LocationServiceException
	{
		getLocationService().saveOrUpdateLocation(createTestLocation(name, street, buildingNo, postalCode, city, countryIsoCode));
		return getLocationService().getLocationByName(name);
	}

	protected Location createTestLocation(final String name, final String street, final String buildingNo, final String postalCode,
			final String city, final String countryIsoCode) throws LocationServiceException
	{
		final LocationDTO dto = new LocationDTO();
		dto.setName(name);
		dto.setType(LocationDTO.LOCATION_TYPE_STORE);
		dto.setStreet(street);
		dto.setBuildingNo(buildingNo);
		dto.setPostalCode(postalCode);
		dto.setCity(city);
		dto.setCountryIsoCode(countryIsoCode);
		return new LocationDtoWrapper(dto);
	}

	public GeocodingJob getGeocodeAddressesJob()
	{
		return geocodeAddressesJob;
	}

	public void setGeocodeAddressesJob(final GeocodingJob geocodeAddressesJob)
	{
		this.geocodeAddressesJob = geocodeAddressesJob;
	}

	public CronJobService getCronJobService()
	{
		return cronJobService;
	}

	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	public GeoWebServiceWrapper getGeoServiceWrapper()
	{
		return geoServiceWrapper;
	}

	public void setGeoServiceWrapper(final GeoWebServiceWrapper geoServiceWrapper)
	{
		this.geoServiceWrapper = geoServiceWrapper;
	}

	/**
	 * @deprecated since 6.4 use {@link #getCommonI18NService()} instead
	 */
	@Deprecated
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @deprecated since 6.4 use {@link #setCommonI18NService(CommonI18NService)} instead
	 */
	@Deprecated
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public LocationService getLocationService()
	{
		return locationService;
	}

	public void setLocationService(final LocationService locationService)
	{
		this.locationService = locationService;
	}

	public MapService getMapService()
	{
		return mapService;
	}

	public void setMapService(final MapService mapService)
	{
		this.mapService = mapService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public PointOfServiceDao getPointOfServiceDao()
	{
		return pointOfServiceDao;
	}

	public void setPointOfServiceDao(final PointOfServiceDao pointOfServiceDao)
	{
		this.pointOfServiceDao = pointOfServiceDao;
	}

	public RouteService getRouteService()
	{
		return routeService;
	}

	public void setRouteService(final RouteService routeService)
	{
		this.routeService = routeService;
	}


}
