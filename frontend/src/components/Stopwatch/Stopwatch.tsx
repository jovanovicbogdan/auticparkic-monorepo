import { useEffect, useState } from "react";
import Ride, { Status } from "../../models/RideModel.ts";

type StopwatchProps = {
  action: Status;
  ride: Ride;
};

export default function Stopwatch({ action, ride }: StopwatchProps) {
  // elapsed time in seconds
  const [elapsedTime, setElapsedTime] = useState<number>(ride.elapsedTime);
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [hours, setHours] = useState<number>(0);
  const [minutes, setMinutes] = useState<number>(0);
  const [seconds, setSeconds] = useState<number>(0);

  useEffect(() => {
    if (isRunning) {
      setHours(Math.floor(elapsedTime / 3600));
      setMinutes(Math.floor((elapsedTime % 3600) / 60));
      setSeconds(elapsedTime % 60);
    }
  }, [elapsedTime, isRunning, ride.rideId]);

  useEffect(() => {
    switch (action) {
      case Status.CREATED:
        setIsRunning(false);
        break;
      case Status.RUNNING:
        setIsRunning(true);
        break;
      case Status.PAUSED:
        setIsRunning(false);
        break;
      case Status.STOPPED:
        setIsRunning(false);
        break;
      case Status.FINISHED:
        setIsRunning(false);
        break;
      default:
        break;
    }
  }, [action]);

  useEffect(() => {
    setElapsedTime(ride.elapsedTime);
    setHours(Math.floor(ride.elapsedTime / 3600));
    setMinutes(Math.floor((ride.elapsedTime % 3600) / 60));
    setSeconds(ride.elapsedTime % 60);
  }, [ride.elapsedTime]);

  return (
    <>
      <div className="stopwatch-time-components text-beige font-xxl">
        <span>{hours.toString().padStart(2, "0")}</span>
        <span className="delimeter">:</span>
        <span>{minutes.toString().padStart(2, "0")}</span>
        <span className="delimeter">:</span>
        <span>{seconds.toString().padStart(2, "0")}</span>
      </div>
    </>
  );
}
