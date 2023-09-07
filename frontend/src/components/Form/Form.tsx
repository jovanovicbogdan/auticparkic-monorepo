import { useState } from "react";
import VehicleSelect from "./VehicleSelect.tsx";
import VehicleChoicePreview from "./VehicleChoicePreview.tsx";
import api from "../../api/api.ts";
import Ride from "../../models/RideModel.ts";
import { motion } from "framer-motion";

type PageDisplayProps = {
  step: number;
  selectedVehicleId: number;
  setSelectedVehicleId: (vehicleId: number) => void;
};

type FormProps = {
  unfinishedRides: Ride[];
  setUnfinishedRides: (rides: Ride[]) => void;
  setShowForm: (showForm: boolean) => void;
};

function PageDisplay(props: PageDisplayProps) {
  switch (props.step) {
    case 0:
      return (
        <VehicleSelect setSelectedVehicleId={props.setSelectedVehicleId} />
      );
    case 1:
      return (
        <VehicleChoicePreview selectedVehicleId={props.selectedVehicleId} />
      );
  }
}

export default function Form(props: FormProps) {
  const [step, setStep] = useState(0);
  const [selectedVehicleId, setSelectedVehicleId] = useState<number>(-1);

  const stepPageTitles = ["Izaberi Autić", "Pregled Izbora"];

  function createNewRide(vehicleId: number) {
    const params = new URLSearchParams();
    params.set("vehicleId", vehicleId.toString());

    api(`/v1/rides/create?${params.toString()}`, "post")
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Nije uspelo kreiranje vožnje");
        return res.data as Ride;
      })
      .then((ride) => {
        // ride.elapsedTime = 0;
        const updatedRides = [...props.unfinishedRides, ride];
        props.setUnfinishedRides(updatedRides);
      })
      .catch(() => {
        // handle error message
      });
  }

  function nextStep() {
    if (selectedVehicleId === -1) {
      alert("Molimo izaberite autić");
      return;
    }

    if (step === stepPageTitles.length - 1) {
      createNewRide(selectedVehicleId);
      props.setShowForm(false);
    }

    setStep((step) => step + 1);
  }

  function prevStep() {
    setStep((step) => step - 1);
  }

  function close() {
    props.setShowForm(false);
  }

  return (
    <motion.div
      className="multi-step-form"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      <div className="header">
        <h1>{stepPageTitles[step]}</h1>
        <div>
          <button
            className="btn-outlined-beige close-btn text-beige font-lg font-xl ml-3"
            onClick={() => close()}
          >
            x
          </button>
        </div>
      </div>
      <div className="body">
        <PageDisplay
          step={step}
          selectedVehicleId={selectedVehicleId}
          setSelectedVehicleId={setSelectedVehicleId}
        />
        <div className="mt-2">
          <button className="btn-beige font-md mr-1" onClick={() => nextStep()}>
            {step === stepPageTitles.length - 1 ? "Završi" : "Dalje"}
          </button>
          <button
            disabled={step === 0}
            className="btn-outlined-beige back-btn text-beige font-md"
            onClick={() => prevStep()}
          >
            Nazad
          </button>
        </div>
      </div>
    </motion.div>
  );
}
