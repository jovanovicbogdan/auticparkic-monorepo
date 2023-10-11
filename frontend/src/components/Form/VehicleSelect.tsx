import { useCallback, useEffect, useState } from "react";
import api from "../../api/api.ts";
import Vehicle, { getVehicleImageUrl } from "../../models/VehicleModel.ts";
// import Spinner from "../Spinner.tsx";
import { AnimatePresence, motion } from "framer-motion";
import { warningNotification } from "../../services/notification.ts";
import { Spinner } from "@chakra-ui/react";

type VehicleSelectProps = {
  setSelectedVehicleId: (vehicleId: number) => void;
  setShowForm: (showForm: boolean) => void;
};

type ImageContainerProps = {
  imageUrl: string;
  vehicleId: string;
  selectedImage: string;
  setSelectedImage: (imageId: string) => void;
  setSelectedVehicleId: (vehicleId: number) => void;
};

function ImageContainer({
  imageUrl,
  vehicleId,
  selectedImage,
  setSelectedImage,
  setSelectedVehicleId,
}: ImageContainerProps) {
  const handleClick = () => {
    if (selectedImage === vehicleId) {
      setSelectedImage("");
      setSelectedVehicleId(-1);
    } else {
      setSelectedImage(vehicleId);
      setSelectedVehicleId(parseInt(vehicleId));
    }
  };

  return (
    <div
      className={`vehicle-select-image-container ${
        selectedImage === vehicleId ? "active" : ""
      }`}
      onClick={handleClick}
    >
      <img src={imageUrl} />
      {selectedImage === vehicleId && (
        <div className="selected-vehicle-checkmark">✅</div>
      )}
    </div>
  );
}

export default function VehicleSelect({
  setSelectedVehicleId,
  setShowForm,
}: VehicleSelectProps) {
  const [loading, setLoading] = useState<boolean>(false);
  const [availableVehicles, setAvailableVehicles] = useState<Vehicle[]>([]);
  const [selectedImage, setSelectedImage] = useState<string>("");

  const getAndSetAvailableVehicles = useCallback(() => {
    const controller = new AbortController();

    setLoading(true);

    api("/v1/vehicles/available", "get", undefined, controller.signal)
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Could not fetch available vehicles");
        return res.data as Vehicle[];
      })
      .then((vehicles) => {
        const activeVehicles = vehicles.filter((vehicle) => vehicle.isActive);
        setAvailableVehicles(() => activeVehicles);
        if (activeVehicles.length === 0) {
          warningNotification(
            "Warning",
            "There are no available vehicles at the moment"
          );
          setShowForm(false);
        }
      })
      .catch(() => {
        // handle error
      })
      .finally(() => setLoading(false));

    return controller;
  }, [setShowForm]);

  useEffect(() => {
    const controller = getAndSetAvailableVehicles();
    return () => controller.abort();
  }, [getAndSetAvailableVehicles]);

  return (
    <AnimatePresence>
      <motion.div
        className="vehicle-select mt-3"
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
        {availableVehicles.map(
          (vehicle) =>
            vehicle.vehicleImageId && (
              <ImageContainer
                imageUrl={getVehicleImageUrl(vehicle.vehicleId)}
                vehicleId={vehicle.vehicleId.toString()}
                selectedImage={selectedImage}
                setSelectedImage={setSelectedImage}
                setSelectedVehicleId={setSelectedVehicleId}
                key={vehicle.vehicleId}
              />
            )
        )}
      </motion.div>
    </AnimatePresence>
  );
}
