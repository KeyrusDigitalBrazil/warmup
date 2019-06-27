package de.hybris.platform.sap.productconfig.facades.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.sap.productconfig.facades.UiType
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class UITypeFinderTest extends Specification {

    @Test
    @Unroll
    def "CsticModel should result in UiType #expected"() {
        given:
        UiTypeFinderImpl uiTypeFinder = new UiTypeFinderImpl()

        when:
        def csticModel = new CsticModelImpl()
        csticModel.setAllowsAdditionalValues(allowedAdditionalValues)
        csticModel.setAssignableValues(listAssignableValues)
        csticModel.setEntryFieldMask(entryMask)
        csticModel.setMultivalued(multiValued)
        csticModel.setReadonly(readOnly)
        csticModel.setValueType(valueType)
        csticModel.setConstrained(constrained)

        then:
        uiTypeFinder.findUiTypeForCstic(csticModel) == expected

        where:
        expected               | allowedAdditionalValues | listAssignableValues         | entryMask | multiValued | readOnly | valueType                 | constrained
        UiType.STRING          | false                   | createAssignableValueList(0) | null      | false       | false    | CsticModel.TYPE_STRING    | false
        UiType.READ_ONLY       | false                   | createAssignableValueList(0) | null      | false       | true     | CsticModel.TYPE_STRING    | false
        UiType.NOT_IMPLEMENTED | false                   | createAssignableValueList(0) | null      | false       | false    | CsticModel.TYPE_BOOLEAN   | false
        UiType.NOT_IMPLEMENTED | false                   | createAssignableValueList(0) | null      | false       | false    | CsticModel.TYPE_CLASS     | false
        UiType.NOT_IMPLEMENTED | false                   | createAssignableValueList(0) | null      | false       | false    | CsticModel.TYPE_CURRENCY  | false
        UiType.NOT_IMPLEMENTED | false                   | createAssignableValueList(0) | null      | false       | false    | CsticModel.TYPE_DATE      | false
        UiType.NOT_IMPLEMENTED | false                   | createAssignableValueList(0) | null      | false       | false    | CsticModel.TYPE_UNDEFINED | false

    }

    List<CsticValueModel> createAssignableValueList(final int size) {
        final List<CsticValueModel> values = new ArrayList<>(size);
        for (int ii = 0; ii < size; ii++) {
            final CsticValueModel value = new CsticValueModelImpl();
            values.add(value);
        }
        return values;
    }
}
