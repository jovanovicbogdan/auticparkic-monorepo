type EmployeesProps = {
  setIsSliderOpen: (isSidebarOpen: boolean) => void;
};

export default function Employees({ setIsSliderOpen }: EmployeesProps) {
  return (
    <div className="dashboard-tables br-md">
      <div className="dashboard-description">
        <h2>Zaposleni</h2>
        <button
          disabled={true}
          className="btn-outlined-black text-hover-white font-sm"
          onClick={() => setIsSliderOpen(true)}
        >
          Dodaj Zaposlenog
        </button>
      </div>
      {/*

      <table>
        <thead>
          <tr className="font-sm">
            <th></th>
            <th>IME</th>
            <th>STATUS</th>
            <th>DODAT</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td className="pt-1">
              <img
                className="vehicle-image"
                src="/assets/images/electric-vehicle-1.jpg"
                alt="electric vehicle image"
              />
            </td>
            <td className="employee-name">Jamila</td>
            <td>
              <span className="employee-status employee-status-active br-md">
                AKTIVAN
              </span>
            </td>
            <td>{new Date("2023-04-22T14:23:44").toLocaleDateString()}</td>
            <td>
              <a
                className="edit-btn text-gray"
                onClick={() => alert("Izmeni radnika modal...")}
              >
                Izmeni
              </a>
            </td>
          </tr>
        </tbody>
      </table>
      */}
    </div>
  );
}
