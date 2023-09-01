import { useCallback, useEffect, useState } from "react";
import api from "../../api/api.ts";
import Ride, { Status } from "../../models/RideModel.ts";
import Stopwatch from "../Stopwatch/Stopwatch.tsx";
import Form from "../Form/Form.tsx";
import { getVehicleImageUrl } from "../../models/VehicleModel.ts";

export default function ActiveRides() {
  // const [loading, setLoading] = useState<boolean>(false);
  const [showForm, setShowForm] = useState<boolean>(false);
  const [unfinishedRides, setUnfinishedRides] = useState<Ride[]>([]);

  function formatTime(dateTime: Date) {
    const hours = dateTime.getHours().toString().padStart(2, "0");
    const minutes = dateTime.getMinutes().toString().padStart(2, "0");
    const seconds = dateTime.getSeconds().toString().padStart(2, "0");

    return `${hours}:${minutes}:${seconds}`;
  }

  const loadRunningRides = useCallback(() => {
    const controller = new AbortController();

    api("/v1/rides/unfinished", "get", undefined, controller.signal)
      .then((res) => {
        if (res.status !== "ok") throw new Error("Could not load rides");
        return res.data as Ride[];
      })
      .then((rides) => {
        rides.forEach((ride) => {
          if (ride.status === Status.RUNNING) {
            ride.intervalId = setInterval(() => {
              const savedElapsedTime = localStorage.getItem(
                `elapsedTime-${ride.rideId}`
              );
              sendElapsedTime(
                ride.rideId,
                savedElapsedTime ? parseInt(savedElapsedTime) : 0
              );
            }, 5000);
          }
        });
        setUnfinishedRides(rides);
      })
      .catch(() => {
        // Show error message
      });

    return controller;
  }, []);

  useEffect(() => {
    const runningRidesController = loadRunningRides();
    return () => {
      runningRidesController.abort();
    };
  }, [loadRunningRides]);

  function sendElapsedTime(rideId: number, elapsedTime: number) {
    const urlSearchParams = new URLSearchParams();
    urlSearchParams.set("elapsedTime", elapsedTime.toString());

    const apiPath = `/v1/rides/${rideId}?${urlSearchParams.toString()}`;

    api(apiPath, "post")
      .then((res) => {
        if (res.status !== "ok") throw new Error("Could not send elapsed time");
      })
      .catch(() => {
        // handle error message
      });
  }

  function startRide(rideId: number) {
    const apiPath = `/v1/rides/${rideId}/start`;
    api(apiPath, "post", undefined)
      .then((res) => {
        if (res.status !== "ok") throw new Error("Could not start ride");
        return res.data as Ride;
      })
      .then((startedRide) => {
        const updatedRides = unfinishedRides.map((ride) => {
          if (ride.rideId === startedRide.rideId) {
            ride.intervalId = setInterval(() => {
              const savedElapsedTime = localStorage.getItem(
                `elapsedTime-${ride.rideId}`
              );
              sendElapsedTime(
                ride.rideId,
                savedElapsedTime ? parseInt(savedElapsedTime) : 0
              );
            }, 5000);
            ride.status = startedRide.status;
            return ride;
          }
          return ride;
        });
        setUnfinishedRides(updatedRides);
      })
      .catch(() => {
        // Show error message
      });
  }

  function restartRide(rideId: number) {
    const apiPath = `/v1/rides/${rideId}/restart`;
    api(apiPath, "post", undefined)
      .then((res) => {
        if (res.status !== "ok") throw new Error("Could not restart ride");
        return res.data as Ride;
      })
      .then((newRide) => {
        const updatedRides = unfinishedRides.map((ride) => {
          if (ride.rideId === rideId) {
            clearInterval(ride.intervalId);
            ride = newRide;
            return ride;
          }
          return ride;
        });

        setUnfinishedRides(updatedRides);
      })
      .catch(() => {
        // Show error message
      });
  }

  function pauseRide(rideId: number) {
    const savedElapsedTime = localStorage.getItem(`elapsedTime-${rideId}`);
    const params = new URLSearchParams();
    params.set("elapsedTime", savedElapsedTime ? savedElapsedTime : "0");

    const apiPath = `/v1/rides/${rideId}/pause?${params.toString()}`;
    api(apiPath, "post", undefined)
      .then((res) => {
        if (res.status !== "ok") throw new Error("Could not pause ride");
        return res.data as Ride;
      })
      .then((pausedRide) => {
        const updatedRides = unfinishedRides.map((ride) => {
          if (ride.rideId === pausedRide.rideId) {
            clearInterval(ride.intervalId);
            ride.status = pausedRide.status;
            return ride;
          }
          return ride;
        });
        setUnfinishedRides(updatedRides);
      })
      .catch(() => {
        // Show error message
      });
  }

  function stopRide(rideId: number) {
    const savedElapsedTime = localStorage.getItem(`elapsedTime-${rideId}`);
    const params = new URLSearchParams();
    params.set("elapsedTime", savedElapsedTime ? savedElapsedTime : "0");

    const apiPath = `/v1/rides/${rideId}/stop?${params.toString()}`;
    api(apiPath, "post", undefined)
      .then((res) => {
        if (res.status !== "ok") throw new Error("Could not stop ride");
        return res.data as Ride;
      })
      .then((stoppedRide) => {
        const updatedRides = unfinishedRides.map((ride) => {
          if (ride.rideId === stoppedRide.rideId) {
            clearInterval(ride.intervalId);
            ride.status = stoppedRide.status;
            ride.price = stoppedRide.price;
            return ride;
          }
          return ride;
        });
        setUnfinishedRides(updatedRides);
      })
      .catch(() => {
        // Show error message
      });
  }

  function finishRide(rideId: number) {
    const apiPath = `/v1/rides/${rideId}/finish`;
    api(apiPath, "post", undefined)
      .then((res) => {
        if (res.status !== "ok") throw new Error("Could not finish ride");
        return res.data as Ride;
      })
      .then((fRide) => {
        const finishedRide = unfinishedRides.find(
          (ride) => ride.rideId === fRide.rideId
        );
        if (finishedRide)
          setUnfinishedRides(
            unfinishedRides.filter(
              (ride) => ride.rideId !== finishedRide.rideId
            )
          );
      })
      .catch(() => {
        // Show error message
      });
  }

  return (
    <div>
      {showForm && (
        <Form
          unfinishedRides={unfinishedRides}
          setUnfinishedRides={setUnfinishedRides}
          setShowForm={setShowForm}
        />
      )}
      <div className="active-rides ml-3 mt-3">
        {unfinishedRides.map((ride) => (
          <div
            className="active-ride br-sm"
            id={ride.vehicleId.toString()}
            key={ride.rideId}
          >
            <div className="active-ride-image">
              <img
                src={getVehicleImageUrl(ride.vehicleId)}
                alt="vehicle"
                className="br-sm"
              />
            </div>
            <div className="active-ride-stopwatch mt-3">
              <p className="text-beige font-sm">
                Vožnja započeta u {formatTime(new Date(ride.createdAt))}
              </p>
              <Stopwatch action={ride.status} ride={ride} />
            </div>
            {ride.status === Status.STOPPED && (
              <div className="active-ride-info">
                <div className="active-ride-controls">
                  <div>
                    <button
                      className="btn-outlined-beige text-beige text-hover-black font-md"
                      onClick={() => restartRide(ride.rideId)}
                    >
                      Restartuj
                    </button>
                    <button
                      onClick={() => finishRide(ride.rideId)}
                      className="btn-outlined-beige text-beige text-hover-black font-md"
                    >
                      Završi
                    </button>
                  </div>
                </div>
                <div className="text-beige font-xl font-md mt-2">
                  Cena: {ride.price} RSD
                </div>
              </div>
            )}
            <div className="active-ride-controls">
              {ride.status === Status.CREATED && (
                <button
                  className="btn-outlined-beige text-beige text-hover-black font-md"
                  onClick={() => startRide(ride.rideId)}
                >
                  Kreni
                </button>
              )}
              {ride.status === Status.PAUSED && (
                <button
                  className="btn-outlined-beige text-beige text-hover-black font-md"
                  onClick={() => startRide(ride.rideId)}
                >
                  Nastavi
                </button>
              )}
              {ride.status === Status.RUNNING && (
                <div>
                  <button
                    className="btn-outlined-beige text-beige text-hover-black font-md"
                    onClick={() => pauseRide(ride.rideId)}
                  >
                    Pauziraj
                  </button>
                  <button
                    className="btn-outlined-beige text-beige text-hover-black font-md"
                    onClick={() => stopRide(ride.rideId)}
                  >
                    Zaustavi
                  </button>
                </div>
              )}
            </div>
          </div>
        ))}

        <button
          className="btn-outlined-beige new-ride-btn text-beige font-xl"
          onClick={() => setShowForm((showForm) => !showForm)}
        >
          +
        </button>
      </div>
    </div>
  );
}
