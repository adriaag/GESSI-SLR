import { Criteria } from "./dataModels/criteria";

export interface CriteriaResponse {
    inclusionCriteria: Criteria[];
    exclusionCriteria: Criteria[];
}