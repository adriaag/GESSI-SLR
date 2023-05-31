import { ConsensusCriteria} from "./consensusCriteria";
import { Article } from "./article";
import { DigitalLibrary } from "./digitalLibrary";
import { UserDesignation } from "./userDesignation";

export interface Reference {
    idRef: number;
    doi: string;
    idDL: number;
    idProject: number;
    state: string;
    idProjRef: number;
    exclusionDTOList: ConsensusCriteria;
    art: Article;
    dl: DigitalLibrary;
    usersCriteria1: UserDesignation;
    usersCriteria2: UserDesignation;
    consensusCriteriaProcessed: boolean;
    
}

