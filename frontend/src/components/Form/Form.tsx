import { useState } from "react";
import VehicleSelect from "./VehicleSelect.tsx";
import VehicleChoicePreview from "./VehicleChoicePreview.tsx";
import Ride from "../../models/RideModel.ts";
import { motion } from "framer-motion";
import { Client } from "@stomp/stompjs";

type PageDisplayProps = {
  step: number;
  selectedVehicleId: number;
  setSelectedVehicleId: (vehicleId: number) => void;
  setShowForm: (showForm: boolean) => void;
};

type FormProps = {
  unfinishedRides: Ride[];
  setUnfinishedRides: (rides: Ride[]) => void;
  setShowForm: (showForm: boolean) => void;
  stompClient: Client;
};

function PageDisplay(props: PageDisplayProps) {
  switch (props.step) {
    case 0:
      return (
        <VehicleSelect
          setSelectedVehicleId={props.setSelectedVehicleId}
          setShowForm={props.setShowForm}
        />
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
    props.stompClient.publish({
      destination: "/app/rides.create",
      body: JSON.stringify({ vehicleId }),
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
    setSelectedVehicleId(-1);
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
          setShowForm={props.setShowForm}
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
