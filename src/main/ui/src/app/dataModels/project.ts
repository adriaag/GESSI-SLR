export interface Project {
    id: number;
    name: string;
    idDuplicateCriteria: number;
    topic: string;
    researchQuestion: string;
    protocol: string;
    comments: string;
    involvedUsers: {
        idProject: number;
	    username: string;
	    involveInfo: string;
    }[];
    digitalLibraries: {
        idProject: number;
	    idDL: number;
	    searchString: string;
	    numSearchResults: number;
    }[];
}