import { Venue } from "./venue";
import { Company } from "./company";
import { Researcher } from "./researcher";

export interface Article {
    doi: String;
    type: String;
    citeKey: String;
    idVen: Number;
    title: String;
    keywords: String;
    number: String;
    numpages: Number;
    pages: String;
    volume: String;
    any: Number;
    abstractA: Text;
    ven: Venue;
    companies: Company[];
    researchers: Researcher[];
}