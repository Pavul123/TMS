export type TripStatus = 'Draft' | 'Submitted' | 'Loaded' | 'Running' | 'Delivered';
export type Customer = { id:string; name:string; phone:string; address:string };
export type Vehicle = { registration:string; ownership:string; km:string; status:string };
export type Driver = { id:string; name:string; phone:string; availability:string };
export type Trip = { id:string; date:string; customer:Customer; vehicle:Vehicle; driver:Driver; material:string; quantity:number; unit:string; source:string; loadingLocation:string; deliveryLocation:string; status:TripStatus; progress:number; notes?:string };
