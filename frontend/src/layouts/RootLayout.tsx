import { Outlet } from "react-router-dom";

export default function RootLayout() {
  return (
    <main>
      <Outlet />
    </main>
  );
}

// <div className="root-layout">
//   <header>
//     <nav>
//       <h1>React Router</h1>
//       <NavLink to="/">Home</NavLink>
//       <NavLink to="login">Login</NavLink>
//     </nav>
//   </header>
// </div>
