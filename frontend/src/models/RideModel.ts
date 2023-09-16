export const enum Status {
  CREATED = "CREATED",
  RUNNING = "RUNNING",
  PAUSED = "PAUSED",
  STOPPED = "STOPPED",
  FINISHED = "FINISHED",
}

export default interface Ride {
  rideId: number;
  vehicleId: number;
  status: Status;
  elapsedTime: number;
  price: number;
  createdAt: string;
}
