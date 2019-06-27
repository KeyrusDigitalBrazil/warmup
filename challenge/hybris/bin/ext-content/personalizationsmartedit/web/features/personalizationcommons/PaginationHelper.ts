import {SeInjectable} from 'smarteditcommons';

@SeInjectable()
export class PaginationHelper {

	private count: number;
	private page: number;
	private totalCount: number;
	private totalPages: number;

	constructor(initialData: any) {
		initialData = initialData || {};

		this.count = initialData.count || 0;
		this.page = initialData.page || 0;
		this.totalCount = initialData.totalCount || 0;
		this.totalPages = initialData.totalPages || 0;
	}

	reset(): void {
		this.count = 50;
		this.page = -1;
		this.totalPages = 1;
		this.totalCount = 0;
	}

	getCount(): number {
		return this.count;
	}

	getPage(): number {
		return this.page;
	}

	getTotalCount(): number {
		return this.totalCount;
	}

	getTotalPages(): number {
		return this.totalPages;
	}
}