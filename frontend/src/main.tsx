import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
  faCarSide,
  faUserGroup,
  faArrowLeft,
} from "@fortawesome/free-solid-svg-icons";
import "./assets/sass/index.scss";

library.add(faCarSide, faUserGroup, faArrowLeft);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
