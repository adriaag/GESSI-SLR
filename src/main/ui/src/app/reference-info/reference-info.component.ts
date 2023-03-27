import { Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Reference } from '../dataModels/reference';
import { DataService } from '../data.service';

@Component({
  selector: 'app-reference-info',
  templateUrl: './reference-info.component.html',
  styleUrls: ['./reference-info.component.css']
})
export class ReferenceInfoComponent implements OnInit{

  constructor(private route: ActivatedRoute, private dataService: DataService) {}

  reference!: Reference;
  idRef: number = NaN

  ngOnInit(): void {
    this.getReferenceId();
    this.getReference();
  }

  getReferenceId(): void {
    this.route.params.subscribe(params => {
      this.idRef = params['id'];
    })
  }

  getReference(): void {
    this.dataService.getReference(this.idRef).subscribe((resposta)=> {
      this.reference = resposta
    })
  }

  getCriteriaString(): String {
    var text = ""
    for (let criteria of this.reference.exclusionDTOList) {
      text += criteria.nameICEC+", "
    }
    return text

  }


}
