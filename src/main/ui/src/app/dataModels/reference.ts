import { Exclusion } from "./exclusion";
import { Article } from "./article";
import { DigitalLibrary } from "./digitalLibrary";

export interface Reference {
    idRef: Number;
    doi: String;
    idDL: Number;
    idProject: Number;
    state: String;
    exclusionDTOList: Exclusion[];
    articleDTO: Article;
    digitalLibraryDTO: DigitalLibrary;
    
}

