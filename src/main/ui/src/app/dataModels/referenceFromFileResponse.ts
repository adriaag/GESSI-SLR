import { ImportError } from "./importError";

export interface ReferenceFromFileResponse {
    newDL: string;
    newName: string;
    errors: ImportError[];
    refsImp: number;
    refsDupl: number;
    DLnew: string;
    errorFile: string;
    importBool: boolean;
}