import { motion } from "framer-motion";
import { ReactNode } from "react";

type SliderProps = {
  isSidebarOpen: boolean;
  children: ReactNode;
};

export default function Slider({ isSidebarOpen, children }: SliderProps) {
  return (
    <motion.div
      animate={{ right: isSidebarOpen ? 0 : "-100%" }}
      transition={{ ease: "linear", duration: 0.3 }}
      className="dashboard-slider"
    >
      {children}
    </motion.div>
  );
}
