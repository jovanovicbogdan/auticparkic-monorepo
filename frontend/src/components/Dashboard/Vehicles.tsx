import api from "../../api/api.ts";
import Vehicle, { getVehicleImageUrl } from "../../models/VehicleModel.ts";

type VehiclesProps = {
  availableVehicles: Vehicle[];
  setAvailableVehicles: (vehicles: Vehicle[]) => void;
  setIsSliderOpen: (isSidebarOpen: boolean) => void;
  setIsUpdateVehicleSliderOpen: (isSidebarOpen: boolean) => void;
  setUpdateVehicle: (vehicle: Vehicle) => void;
};

export default function Vehicles({
  availableVehicles,
  setAvailableVehicles,
  setIsSliderOpen,
  setIsUpdateVehicleSliderOpen,
  setUpdateVehicle,
}: VehiclesProps) {

  function deleteVehicle(vehicleId: number) {
    api(`/v1/vehicles/${vehicleId}`, "delete")
      .then((res) => {
        if (res.status !== "ok") throw new Error("Brisanje vozila nije uspelo");
        const newVehicles = availableVehicles.filter(
          (vehicle) => vehicle.vehicleId !== vehicleId
        );
        setAvailableVehicles(newVehicles);
      })
      .catch(() => {
        // handle error
      });
  }

  function fetchVehicle(vehicleId: number) {
    api(`/v1/vehicles/${vehicleId}`, "get")
      .then((res) => {
        if (res.status !== "ok")
          throw new Error("Greška pri dobavljanju vozila");
        return res.data as Vehicle;
      })
      .then((vehicle) => {
        setUpdateVehicle(vehicle);
      })
      .catch(() => {
        // handle error message
      });
  }

  return (
    <div className="dashboard-tables br-md">
      <div className="dashboard-description">
        <h2>Vozila</h2>
        <button
          className="btn-outlined-black text-hover-white font-sm"
          onClick={() => setIsSliderOpen(true)}
        >
          Dodaj Vozilo
        </button>
      </div>
      <table>
        <thead>
          <tr className="font-sm">
            <th></th>
            <th>VOZILO</th>
            <th>STATUS</th>
            <th>DODATO</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {availableVehicles.map((vehicle) => (
            <tr key={vehicle.vehicleId}>
              <td className="pt-1">
                <img
                  className="vehicle-image"
                  src={getVehicleImageUrl(vehicle.vehicleId)}
                  alt="electric vehicle image"
                />
              </td>
              <td className="vehicle-name">
                {vehicle.name[0] + vehicle.name.slice(1).toLowerCase()}
              </td>
              <td>
                <span
                  className={`vehicle-status vehicle-status-${
                    vehicle.isActive ? "active" : "inactive"
                  } text-white br-md`}
                >
                  {vehicle.isActive ? "DOSTUPNO" : "NEDOSTUPNO"}
                </span>
              </td>
              <td>{new Date(vehicle.createdAt).toLocaleDateString()}</td>
              <td>
                <a
                  className="edit-btn text-blue"
                  onClick={() => {
                    fetchVehicle(vehicle.vehicleId);
                    setIsUpdateVehicleSliderOpen(true);
                  }}
                >
                  Izmeni
                </a>
                {
                  /*
                                                  <a
                  className="edit-btn text-red"
                  onClick={() => deleteVehicle(vehicle.vehicleId)}
                >
                  Izbriši
                </a>

                  */
                }
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
