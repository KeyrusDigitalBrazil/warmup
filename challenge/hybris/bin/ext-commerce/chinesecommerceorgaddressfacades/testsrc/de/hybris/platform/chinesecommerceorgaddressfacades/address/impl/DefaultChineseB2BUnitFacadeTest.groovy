package de.hybris.platform.chinesecommerceorgaddressfacades.address.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.addressservices.model.CityModel
import de.hybris.platform.addressservices.model.DistrictModel
import de.hybris.platform.b2b.company.B2BCommerceUnitService
import de.hybris.platform.b2b.model.B2BUnitModel
import de.hybris.platform.b2b.services.B2BUnitService
import de.hybris.platform.commercefacades.user.data.AddressData
import de.hybris.platform.core.model.c2l.RegionModel
import de.hybris.platform.core.model.user.AddressModel
import de.hybris.platform.servicelayer.dto.converter.Converter

import org.junit.Test

import spock.lang.Specification

@UnitTest
class DefaultChineseB2BUnitFacadeTest extends Specification {

	def private B2BUnitService b2bUnitService
	def private B2BCommerceUnitService b2bCommerceUnitService
	def private Converter<AddressData, AddressModel> addressReverseConverter
	def private B2BUnitModel unit
	def private AddressModel address
	def private RegionModel region
	def private CityModel city
	def private DistrictModel cityDistrict
	def private AddressData addressData
	def private DefaultChineseB2BUnitFacade chineseB2BUnitFacade
	def private String unitUid = "unit1"

	def setup() {
		b2bUnitService = Mock()
		b2bCommerceUnitService = Mock()
		addressReverseConverter = Mock()

		unit = new B2BUnitModel()
		unit.setUid(unitUid)

		region = new RegionModel();
		region.setIsocode("sichuan")

		city = new CityModel();
		city.setIsocode("chengdu")

		cityDistrict = new DistrictModel()
		cityDistrict.setIsocode("wujie")

		address = new AddressModel()
		address.setRegion(region)
		address.setCity(city)
		address.setCityDistrict(cityDistrict)

		addressData = new AddressData();
		addressData.setId("address1")

		chineseB2BUnitFacade = new DefaultChineseB2BUnitFacade()
		chineseB2BUnitFacade.setB2BUnitService(b2bUnitService)
		chineseB2BUnitFacade.setB2BCommerceUnitService(b2bCommerceUnitService)
		chineseB2BUnitFacade.setAddressReverseConverter(addressReverseConverter)
	}

	@Test
	def "testSuccessEditAddressOfUnit"() {
		when:
		b2bUnitService.getUnitForUid(unitUid) >> unit
		b2bCommerceUnitService.getAddressForCode(unit, "address1") >> address
		b2bCommerceUnitService.editAddressEntry >> null

		then:
		chineseB2BUnitFacade.editAddressOfUnit(addressData, unitUid)
		address.getRegion() == null
		address.getCity() == null
		address.getCityDistrict() == null
	}

	@Test
	def "testNullParameterEditAddressOfUnit"(){
		when:
		chineseB2BUnitFacade.editAddressOfUnit(null, unitUid)
		then:
		thrown(IllegalArgumentException)

		when:
		chineseB2BUnitFacade.editAddressOfUnit(addressData, null)
		then:
		thrown(IllegalArgumentException)
	}
}
