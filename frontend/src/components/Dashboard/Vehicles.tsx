import Vehicle from "../../models/VehicleModel.ts";

type VehiclesProps = {
  vehicles: Vehicle[];
  setIsSliderOpen: (isSidebarOpen: boolean) => void;
};

export default function Vehicles({ vehicles, setIsSliderOpen }: VehiclesProps) {
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
          {vehicles.map((vehicle, index) => (
            <tr key={vehicle.vehicleId}>
              <td className="pt-1">
                <img
                  className="vehicle-image"
                  src={`/assets/images/electric-vehicle-${index + 1}.jpg`}
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
                  className="edit-btn text-gray"
                  onClick={() => alert("Izmeni vozilo modal...")}
                >
                  Izmeni
                </a>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
