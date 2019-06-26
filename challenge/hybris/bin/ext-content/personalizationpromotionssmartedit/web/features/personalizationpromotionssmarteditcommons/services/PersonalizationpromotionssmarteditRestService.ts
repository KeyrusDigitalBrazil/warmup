
import {SeInjectable} from 'smarteditcommons';

@SeInjectable()
export class PersonalizationpromotionssmarteditRestService {

	public static AVAILABLE_PROMOTIONS: string = "/personalizationwebservices/v1/query/cxpromotionsforcatalog";

	constructor(private restServiceFactory: any, private personalizationsmarteditUtils: any) {}

	getPromotions(catalogVersions: any): Promise<any> {
		const restService = this.restServiceFactory.get(PersonalizationpromotionssmarteditRestService.AVAILABLE_PROMOTIONS);
		const entries: any = [];

		catalogVersions = catalogVersions || [];

		catalogVersions.forEach((element: any, i: number) => {
			this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog" + i, element.catalog);
			this.personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "version" + i, element.catalogVersion);
		}
		);

		const requestParams = {
			params: {
				entry: entries
			}
		};

		return restService.save(requestParams);
	}

}
