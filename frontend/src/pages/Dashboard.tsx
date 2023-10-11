import { useEffect, useState } from "react";
import {
  ColorModeScript,
  Button,
  ChakraProvider,
  Input,
  Spinner,
  useDisclosure,
} from "@chakra-ui/react";
import Vehicle, { getVehicleImageUrl } from "../models/VehicleModel.ts";
import api, { apiForm } from "../api/api.ts";
import {
  Drawer,
  DrawerBody,
  DrawerFooter,
  DrawerHeader,
  DrawerOverlay,
  DrawerContent,
  DrawerCloseButton,
} from "@chakra-ui/react";
import Vehicles from "../components/Dashboard/Vehicles.tsx";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { IconProp } from "@fortawesome/fontawesome-svg-core";
import { warningNotification } from "../services/notification.ts";
import theme from "../lib/theme.ts";

type NavItemProps = {
  text: string;
  icon: string;
  setShowVehicles: (show: boolean) => void;
  setShowEmployees: (show: boolean) => void;
  isActive?: boolean;
};

function NavItem({
  text,
  icon,
  setShowVehicles,
  setShowEmployees,
  isActive,
}: NavItemProps) {
  function switchActiveItem(text: string) {
    if (text === "Vozila") {
      setShowVehicles(true);
      setShowEmployees(false);
    } else if (text === "Zaposleni") {
      setShowVehicles(false);
      setShowEmployees(true);
    }
  }

  return (
    <li
      className={`nav-item ${isActive ? "active" : ""}`}
      onClick={() => switchActiveItem(text)}
    >
      <div className={`nav-item-icon ${isActive ? "active" : ""}`}>
        <FontAwesomeIcon className="icon" icon={icon as IconProp} />
      </div>
      <span className="font-sm">{text}</span>
    </li>
  );
}

export default function Dashboard() {
  const {
    isOpen: isOpenAddVehicleDrawer,
    onOpen: onOpenAddVehicleDrawer,
    onClose: onCloseAddVehicleDrawer,
  } = useDisclosure();
  const {
    isOpen: isOpenUpdateVehicleDrawer,
    onOpen: onOpenUpdateVehicleDrawer,
    onClose: onCloseUpdateVehicleDrawer,
  } = useDisclosure();
  const [availableVehicles, setAvailableVehicles] = useState<Vehicle[]>([]);
  const [isAddVehicleSliderOpen, setIsAddVehicleSliderOpen] = useState(false);
  const [isUpdateVehicleSliderOpen, setIsUpdateVehicleSliderOpen] =
    useState(false);
  const [showVehicles, setShowVehicles] = useState(() => {
    const showVehicles = localStorage.getItem("showVehicles");
    return showVehicles ? (JSON.parse(showVehicles) as boolean) : true;
  });
  const [showEmployees, setShowEmployees] = useState(() => {
    const showEmployees = localStorage.getItem("showEmployees");
    return showEmployees ? (JSON.parse(showEmployees) as boolean) : false;
  });
  const [vehicleName, setVehicleName] = useState("");
  const [isChecked, setIsChecked] = useState<boolean>(false);
  const [vehicleImage, setVehicleImage] = useState<File>();
  const [updateVehicle, setUpdateVehicle] = useState<Vehicle>();
  const [loading, setLoading] = useState<boolean>(false);

  function getAndSetAvailableVehicles(): AbortController {
    const controller = new AbortController();
    setLoading(true);
    api("/v1/vehicles", "get", undefined, controller.signal)
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Could not fetch available vehicles");
        return res.data as Vehicle[];
      })
      .then((vehicles) => setAvailableVehicles(vehicles))
      .catch(() => {
        // handle error
      })
      .finally(() => setLoading(false));

    return controller;
  }

  function addVehicle() {
    if (!vehicleName || !vehicleImage) {
      warningNotification(
        "Warning",
        "Check if vehicle name and image are entered"
      );
      return;
    }

    const payload = {
      vehicleName,
      isActive: true,
    };
    setLoading(true);
    api("/v1/vehicles", "post", payload)
      .then((res) => {
        if (res.status !== "ok") throw new Error("Vehicle creation failed");

        return res.data as number;
      })
      .then((vehicleId) => {
        const formData = new FormData();
        formData.append("image", vehicleImage);
        return apiForm(
          "post",
          `/v1/vehicles/image/${vehicleId.toString()}/upload`,
          formData
        );
      })
      .then((res) => {
        if (res.status !== "ok") throw new Error("Image upload failed");
        getAndSetAvailableVehicles();
        setIsAddVehicleSliderOpen(false);
        setVehicleName("");
        setIsChecked(true);
      })
      .catch((error: Error) => {
        // handle error
        // errorNotification("Greška", error.message);
      })
      .finally(() => setLoading(false));
  }

  function updateVehicleData(vehicleId: number, payload: any) {
    if (!payload.vehicleName) {
      warningNotification("Warning", "Check if vehicle name is entered");
      return;
    }

    setLoading(true);
    api(`/v1/vehicles/${vehicleId.toString()}`, "put", payload)
      .then((res) => {
        if (res.status !== "ok") {
          warningNotification(
            "Warning",
            "An error occurred, check if the vehicle is currently in use"
          );
          throw new Error("Update nije uspeo");
        }
        getAndSetAvailableVehicles();
        setIsUpdateVehicleSliderOpen(false);
      })
      .catch(() => {
        // handle error
      })
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    const controller = getAndSetAvailableVehicles();
    return () => controller.abort();
  }, []);

  useEffect(() => {
    localStorage.setItem("showVehicles", JSON.stringify(showVehicles));
    localStorage.setItem("showEmployees", JSON.stringify(showEmployees));
  }, [showVehicles, showEmployees]);

  useEffect(() => {
    setIsChecked(updateVehicle?.isActive || false);
    setVehicleName(updateVehicle?.name || "");
  }, [updateVehicle]);

  return (
    <ChakraProvider theme={theme}>
      <ColorModeScript initialColorMode={theme.config.initialColorMode} />
      <div className="dashboard-root">
        {loading && <Spinner />}
        <div className="dashboard-navbar">
          <h1>Autić Parkić</h1>
          <nav>
            <ul>
              <NavItem
                text="Vehicles"
                icon={"fa-car-side"}
                setShowVehicles={setShowVehicles}
                setShowEmployees={setShowEmployees}
                isActive={showVehicles}
              />
              <NavItem
                text="Employees"
                icon={"fa-user-group"}
                setShowVehicles={setShowVehicles}
                setShowEmployees={setShowEmployees}
                isActive={showEmployees}
              />
            </ul>
          </nav>
        </div>
        {showVehicles && (
          <Vehicles
            availableVehicles={availableVehicles}
            setAvailableVehicles={setAvailableVehicles}
            setIsUpdateVehicleSliderOpen={setIsUpdateVehicleSliderOpen}
            setUpdateVehicle={setUpdateVehicle}
            onOpenAddVehicleDrawer={onOpenAddVehicleDrawer}
            onOpenUpdateVehicleDrawer={onOpenUpdateVehicleDrawer}
          />
        )}
        {showVehicles && (
          <div className={`backdrop ${isAddVehicleSliderOpen ? "active" : ""}`}>
            <Drawer
              isOpen={isOpenAddVehicleDrawer}
              placement="right"
              onClose={onCloseAddVehicleDrawer}
              size="lg"
            >
              <DrawerOverlay />
              <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Add new vehicle</DrawerHeader>

                <DrawerBody>
                  <p className="font-sm mb-1">
                    <span className="text-red">*</span> &mdash; required fields
                  </p>
                  <div>
                    <label>
                      Vehicle name <span className="text-red">*</span>
                    </label>
                    <Input
                      type="text"
                      onChange={(e) => setVehicleName(e.target.value)}
                    />
                  </div>
                  <div className="mt-2">
                    <label htmlFor="upload-image">
                      Upload image <span className="text-red">*</span>
                      {vehicleImage && (
                        <span className="font-sm text-gray ml-1">
                          {vehicleImage.name}
                        </span>
                      )}
                    </label>
                    <Input
                      type="file"
                      className="mt-1"
                      onChange={(e) => {
                        if (e.target.files) {
                          setVehicleImage(e.target.files[0]);
                        }
                      }}
                    />
                  </div>
                </DrawerBody>

                <DrawerFooter>
                  <Button
                    variant="outline"
                    mr={3}
                    onClick={onCloseAddVehicleDrawer}
                  >
                    Cancel
                  </Button>
                  <Button
                    colorScheme="blue"
                    onClick={() => {
                      addVehicle();
                      onCloseAddVehicleDrawer();
                    }}
                  >
                    Add Vehicle
                  </Button>
                </DrawerFooter>
              </DrawerContent>
            </Drawer>
          </div>
        )}

        {showVehicles && (
          <Drawer
            isOpen={isOpenUpdateVehicleDrawer}
            placement="right"
            onClose={onCloseUpdateVehicleDrawer}
            size="lg"
          >
            <DrawerOverlay />
            <DrawerContent>
              <DrawerCloseButton />
              <DrawerHeader>Edit vehicle</DrawerHeader>

              <DrawerBody>
                <div>
                  <img
                    src={
                      updateVehicle === undefined
                        ? ""
                        : getVehicleImageUrl(updateVehicle.vehicleId)
                    }
                  />
                </div>
                <p className="font-sm mb-1">
                  <span className="text-red">*</span> &mdash; required fields
                </p>
                <div>
                  <label>
                    New vehicle name <span className="text-red">*</span>
                  </label>
                  <Input
                    type="text"
                    placeholder={
                      vehicleName[0] + vehicleName.slice(1).toLowerCase()
                    }
                    onChange={(e) => setVehicleName(e.target.value)}
                  />
                </div>
                <div>
                  <label className="checkbox-label">
                    Is vehicle available? <span className="text-red">*</span>
                    <input
                      className="ml-2"
                      type="checkbox"
                      checked={isChecked}
                      onChange={() => setIsChecked(!isChecked)}
                    />
                  </label>
                </div>
                <div></div>
              </DrawerBody>

              <DrawerFooter>
                <Button
                  variant="outline"
                  mr={3}
                  onClick={onCloseUpdateVehicleDrawer}
                >
                  Cancel
                </Button>
                <Button
                  colorScheme="blue"
                  onClick={() => {
                    if (updateVehicle)
                      updateVehicleData(updateVehicle.vehicleId, {
                        vehicleName,
                        isActive: isChecked,
                      });
                    setVehicleName("");
                    onCloseUpdateVehicleDrawer();
                  }}
                >
                  Update Vehicle
                </Button>
              </DrawerFooter>
            </DrawerContent>
          </Drawer>
        )}
      </div>
    </ChakraProvider>
  );
}
