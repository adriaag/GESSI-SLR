export interface AddReference {
    doi: string,
    type: string | null,
    nameVen: string | null,
    title: string | null,
    keywords: string | null,
    number: string | null,
    numpages: number,
    pages: string | null,
    volume: string | null,
    any: number,
    abstract: string | null,
    authorNames: string[] | null,
    affiliationNames: string[] | null
}
