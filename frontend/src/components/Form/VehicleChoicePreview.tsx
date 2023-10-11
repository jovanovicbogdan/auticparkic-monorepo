import { useCallback, useEffect, useState } from "react";
import Vehicle, { getVehicleImageUrl } from "../../models/VehicleModel.ts";
import api from "../../api/api.ts";
import { AnimatePresence, motion } from "framer-motion";
import { Spinner } from "@chakra-ui/react";

type VehicleChoicePreviewProps = {
  selectedVehicleId: number;
};

export default function VehicleChoicePreview({
  selectedVehicleId,
}: VehicleChoicePreviewProps) {
  const [vehicle, setVehicle] = useState<Vehicle | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const fetchVehicle = useCallback((vehicleId: number): AbortController => {
    const controller = new AbortController();

    setLoading(true);
    api(`/v1/vehicles/${vehicleId}`, "get", undefined, controller.signal)
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Error while fetching vehicle");
        return res.data as Vehicle;
      })
      .then((vehicle) => {
        setVehicle(vehicle);
      })
      .catch(() => {
        // handle error message
      })
      .finally(() => setLoading(false));

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
    <AnimatePresence>
      <motion.div
        className="vehicle-choice-preview mt-3"
        style={{ color: "#fff" }}
        initial={{ x: 300, opacity: 0 }}
        animate={{ x: 0, opacity: 1 }}
        exit={{ x: 300, opacity: 0 }}
        transition={{
          type: "spring",
          stiffness: 260,
          damping: 40,
        }}
      >
        {loading && <Spinner />}
        {vehicle && (
          <div>
            <img src={getVehicleImageUrl(vehicle.vehicleId)} />
            <div className="vehicle-choice-preview-info font-xxl">
              <div className="name text-beige">{vehicle.name}</div>
              <div className="price text-beige">40 RSD/min</div>
            </div>
          </div>
        )}
      </motion.div>
    </AnimatePresence>
  );
}
