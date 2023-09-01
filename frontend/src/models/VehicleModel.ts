import { ApiConfig } from "../config/api.config.ts";

export function getVehicleImageUrl(vehicleId: number) {
  return `${ApiConfig.API_URL}/v1/vehicles/image/${vehicleId}/download`;
}

export default interface Vehicle {
  vehicleId: number;
  name: string;
  vehicleImageId: string | null;
  createdAt: string;
  isActive: boolean;
}
