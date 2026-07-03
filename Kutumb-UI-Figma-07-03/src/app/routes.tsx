import { createBrowserRouter } from "react-router";
import Root from "./components/Root";
import KutumbHome from "./components/screens/KutumbHome";
import Karya from "./components/screens/Karya";
import Niyama from "./components/screens/Niyama";
import Vyaya from "./components/screens/Vyaya";
import Rina from "./components/screens/Rina";
import Samvaad from "./components/screens/Samvaad";
import Soochi from "./components/screens/Soochi";
import Smriti from "./components/screens/Smriti";
import Parichay from "./components/screens/Parichay";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Root,
    children: [
      { index: true,       Component: KutumbHome },
      { path: "karya",     Component: Karya },
      { path: "niyama",    Component: Niyama },
      { path: "vyaya",     Component: Vyaya },
      { path: "rina",      Component: Rina },
      { path: "samvaad",   Component: Samvaad },
      { path: "soochi",    Component: Soochi },
      { path: "smriti",    Component: Smriti },
      { path: "parichay",  Component: Parichay },
    ],
  },
]);
