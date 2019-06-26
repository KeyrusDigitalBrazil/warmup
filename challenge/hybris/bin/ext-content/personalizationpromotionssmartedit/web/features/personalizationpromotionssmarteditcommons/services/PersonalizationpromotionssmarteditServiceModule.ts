
import {SeModule} from 'smarteditcommons';
import {PersonalizationpromotionssmarteditRestService} from './PersonalizationpromotionssmarteditRestService';

@SeModule({
	imports: [
		'smarteditServicesModule',
		'personalizationsmarteditCommons'
	],
	providers: [
		PersonalizationpromotionssmarteditRestService
	]
})
export class PersonalizationpromotionssmarteditServiceModule {}
