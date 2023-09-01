import { useCallback, useEffect, useState } from "react";
import Vehicle, { getVehicleImageUrl } from "../../models/VehicleModel.ts";
import api from "../../api/api.ts";

type VehicleChoicePreviewProps = {
  selectedVehicleId: number;
};

export default function VehicleChoicePreview({
  selectedVehicleId,
}: VehicleChoicePreviewProps) {
  const [vehicle, setVehicle] = useState<Vehicle | null>(null);

  const fetchVehicle = useCallback((vehicleId: number): AbortController => {
    const controller = new AbortController();

    api(`/v1/vehicles/${vehicleId}`, "get", undefined, controller.signal)
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Greška pri dobavljanju vozila");
        return res.data as Vehicle;
      })
      .then((vehicle) => {
        setVehicle(vehicle);
      })
      .catch(() => {
        // handle error message
      });

    return controller;
  }, []);

  useEffect(() => {
    let controller: AbortController | null = null;
    if (selectedVehicleId !== -1) {
      controller = fetchVehicle(selectedVehicleId);
    }

    return () => {
      if (controller !== null) controller.abort();
    };
  }, [selectedVehicleId, fetchVehicle]);

  return (
    <div className="vehicle-choice-preview mt-3" style={{ color: "#fff" }}>
      {vehicle && (
        <div>
          <img src={getVehicleImageUrl(vehicle.vehicleId)} />
          <div className="vehicle-choice-preview-info font-xxl">
            <div className="name text-beige">{vehicle.name}</div>
            <div className="price text-beige">40 RSD/min</div>
          </div>
        </div>
      )}
    </div>
  );
}
