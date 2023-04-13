import { ImportError } from "./importError";

export interface ReferenceFromFileResponse {
    newDL: number;
    newName: string;
    errors: ImportError[];
    refsImp: number;
    DLnew: string;
    errorFile: string;
    importBool: boolean;
}