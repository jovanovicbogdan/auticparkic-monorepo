import { useEffect, useState } from "react";
import Vehicle from "../models/VehicleModel.ts";
import api, { apiForm } from "../api/api.ts";
import Slider from "../components/Dashboard/Slider.tsx";
import Vehicles from "../components/Dashboard/Vehicles.tsx";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { IconProp } from "@fortawesome/fontawesome-svg-core";
import Employees from "../components/Dashboard/Employees.tsx";

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
  const [availableVehicles, setAvailableVehicles] = useState<Vehicle[]>([]);
  const [isSliderOpen, setIsSliderOpen] = useState(false);
  const [showVehicles, setShowVehicles] = useState(() => {
    const showVehicles = localStorage.getItem("showVehicles");
    return showVehicles ? (JSON.parse(showVehicles) as boolean) : true;
  });
  const [showEmployees, setShowEmployees] = useState(() => {
    const showEmployees = localStorage.getItem("showEmployees");
    return showEmployees ? (JSON.parse(showEmployees) as boolean) : false;
  });
  const [vehicleName, setVehicleName] = useState("");
  const [isVehicleReady, setIsVehicleReady] = useState("on");
  const [vehicleImage, setVehicleImage] = useState<File>();

  function getAndSetAvailableVehicles(): AbortController {
    const controller = new AbortController();
    api("/v1/vehicles/available", "get", undefined, controller.signal)
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Could not fetch available vehicles");
        return res.data as Vehicle[];
      })
      .then((vehicles) => setAvailableVehicles(vehicles))
      .catch(() => {
        // handle error
      });

    return controller;
  }

  function addVehicle() {
    if (!vehicleName || !vehicleImage) return;
    const payload = {
      vehicleName,
      isActive: isVehicleReady === "on",
    };
    api("/v1/vehicles", "post", payload)
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Dodavanje vozila nije uspelo");

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
        if (res.status !== "ok") throw new Error("Dodavanje slike nije uspelo");
        getAndSetAvailableVehicles();
      })
      .catch((error: Error) => {
        // handle error
        alert(error.message);
      });
  }

  useEffect(() => {
    const controller = getAndSetAvailableVehicles();
    return () => controller.abort();
  }, []);

  useEffect(() => {
    localStorage.setItem("showVehicles", JSON.stringify(showVehicles));
    localStorage.setItem("showEmployees", JSON.stringify(showEmployees));
  }, [showVehicles, showEmployees]);

  return (
    <div className="dashboard-root">
      <div className="dashboard-navbar">
        <h1>Autić Parkić</h1>
        <nav>
          <ul>
            <NavItem
              text="Vozila"
              icon={"fa-car-side"}
              setShowVehicles={setShowVehicles}
              setShowEmployees={setShowEmployees}
              isActive={showVehicles}
            />
            <NavItem
              text="Zaposleni"
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
          vehicles={availableVehicles}
          setIsSliderOpen={setIsSliderOpen}
        />
      )}
      {showVehicles && (
        <div className={`backdrop ${isSliderOpen ? "active" : ""}`}>
          <Slider isSidebarOpen={isSliderOpen}>
            <div
              className="slider-header mt-2 ml-2"
              onClick={() => setIsSliderOpen(false)}
            >
              <FontAwesomeIcon
                className="btn-arrow-left font-lg"
                icon="arrow-left"
              />
              <h3>Dodaj Novo Vozilo</h3>
            </div>
            <div className="slider-data">
              <div>
                <label>Ime Vozila</label>
                <input
                  type="text"
                  onChange={(e) => setVehicleName(e.target.value)}
                />
              </div>
              <div>
                <label>Da li je vozilo spremno za vožnju?</label>
                <input
                  type="checkbox"
                  defaultChecked={true}
                  onChange={(e) => setIsVehicleReady(e.target.value)}
                />
              </div>
              <div>
                {/* <label htmlFor="vehicle-image">Dodaj sliku</label> */}
                <input
                  type="file"
                  onChange={(e) => {
                    if (e.target.files) {
                      setVehicleImage(e.target.files[0]);
                    }
                  }}
                />
              </div>
              <div>
                <button
                  className="btn-black text-white font-md"
                  onClick={() => addVehicle()}
                >
                  Dodaj Vozilo
                </button>
              </div>
            </div>
          </Slider>
        </div>
      )}

      {showEmployees && <Employees setIsSliderOpen={setIsSliderOpen} />}
      {showEmployees && (
        <div className={`backdrop ${isSliderOpen ? "active" : ""}`}>
          <Slider isSidebarOpen={isSliderOpen}>
            <div
              className="slider-header mt-2 ml-2"
              onClick={() => setIsSliderOpen(false)}
            >
              <FontAwesomeIcon
                className="btn-arrow-left font-lg"
                icon="arrow-left"
              />
              <h3>Dodaj Novog Zaposlenog</h3>
            </div>
            <div className="slider-data">
              <div>
                <label>Ime</label>
                <input type="text" />
              </div>
              <div>
                <label>Prezime</label>
                <input type="text" />
              </div>
              <div>
                <label>Username</label>
                <input type="text" />
              </div>
              <div>
                <label>Broj Telefona</label>
                <input type="text" />
              </div>
              <div>
                <label>E-Mail</label>
                <input type="email" />
              </div>
              <div>
                {/* <label htmlFor="vehicle-image">Dodaj sliku</label> */}
                <input
                  type="file"
                  onChange={(e) => {
                    if (e.target.files) {
                      setVehicleImage(e.target.files[0]);
                    }
                  }}
                />
              </div>
              <div>
                <button
                  className="btn-black text-white font-md"
                  onClick={() => addVehicle()}
                >
                  Dodaj Zaposlenog
                </button>
              </div>
            </div>
          </Slider>
        </div>
      )}
    </div>
  );
}
