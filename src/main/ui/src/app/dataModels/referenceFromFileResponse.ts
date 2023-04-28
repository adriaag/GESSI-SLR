import { ImportError } from "./importError";

export interface ReferenceFromFileResponse {
    newDL: number;
    newName: string;
    errors: ImportError[];
    refsImp: number;
    refsDupl: number;
    DLnew: string;
    errorFile: string;
    importBool: boolean;
}