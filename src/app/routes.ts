import { Routes } from "@angular/router";
import { HomeComponent } from "./home/home.component";
import { DetailsComponent } from "./details/details.component";

const routeConfig: Routes = [
    {
        path: '',
        component: HomeComponent,
        title: "HomePage"
    },
    {
        path: 'details/:id',
        component: DetailsComponent,
        title: "DetailsPage",
    }
];

export default routeConfig;
