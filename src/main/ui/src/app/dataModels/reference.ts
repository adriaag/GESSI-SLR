import { Exclusion } from "./exclusion";
import { Article } from "./article";
import { DigitalLibrary } from "./digitalLibrary";

export interface Reference {
    idRef: number;
    doi: string;
    idDL: number;
    idProject: number;
    state: string;
    idProjRef: number;
    exclusionDTOList: Exclusion[];
    art: Article;
    dl: DigitalLibrary;
    
}

