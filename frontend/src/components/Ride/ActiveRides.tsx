import { useEffect, useState } from "react";
import Ride, { Status } from "../../models/RideModel.ts";
import Stopwatch from "../Stopwatch/Stopwatch.tsx";
import Form from "../Form/Form.tsx";
import { getVehicleImageUrl } from "../../models/VehicleModel.ts";
import { AnimatePresence } from "framer-motion";
import { Client } from "@stomp/stompjs";
import { WsConfig } from "../../config/ws.config.ts";

export default function ActiveRides() {
  // const [loading, setLoading] = useState<boolean>(false);
  const [showForm, setShowForm] = useState<boolean>(false);
  const [unfinishedRides, setUnfinishedRides] = useState<Ride[]>([]);
  const [stompClient, setStompClient] = useState<Client>(
    new Client({
      brokerURL: WsConfig.WS_URL,
      reconnectDelay: WsConfig.RECONNECT_DELAY,
      // heartbeatIncoming: 4000,
      // heartbeatOutgoing: 4000,
    })
  );

  // function formatTime(dateTime: Date) {
  //   const hours = dateTime.getHours().toString().padStart(2, "0");
  //   const minutes = dateTime.getMinutes().toString().padStart(2, "0");
  //   const seconds = dateTime.getSeconds().toString().padStart(2, "0");
  //
  //   return `${hours}:${minutes}:${seconds}`;
  // }

  useEffect(() => {
    stompClient.onConnect = () => {
      stompClient.subscribe("/topic/public", (message) => {
        setUnfinishedRides(JSON.parse(message.body) as Ride[]);
      });
      stompClient.publish({
        destination: "/app/rides.streamUnfinishedRidesData",
      });
    };

    stompClient.activate();

    return () => {
      stompClient
        .deactivate()
        .then(() => {
          // handle successful disconnection
        })
        .catch(() => console.error("Error while disconnecting"));
    };
  }, [stompClient]);

  function startRide(rideId: number) {
    stompClient.publish({
      destination: "/app/rides.start",
      body: JSON.stringify({ rideId }),
    });
  }

  function restartRide(rideId: number) {
    stompClient.publish({
      destination: "/app/rides.restart",
      body: JSON.stringify({ rideId }),
    });
  }

  function pauseRide(rideId: number) {
    stompClient.publish({
      destination: "/app/rides.pause",
      body: JSON.stringify({ rideId }),
    });
  }

  function stopRide(rideId: number) {
    stompClient.publish({
      destination: "/app/rides.stop",
      body: JSON.stringify({ rideId }),
    });
  }

  function finishRide(rideId: number) {
    stompClient.publish({
      destination: "/app/rides.finish",
      body: JSON.stringify({ rideId }),
    });
  }

  return (
    <div>
      <AnimatePresence>
        {showForm && (
          <Form
            unfinishedRides={unfinishedRides}
            setUnfinishedRides={setUnfinishedRides}
            setShowForm={setShowForm}
            stompClient={stompClient}
          />
        )}
      </AnimatePresence>
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
              {/*
                            <p className="text-beige font-sm">
                Vožnja započeta u {formatTime(new Date(ride.createdAt))}
              </p>

              */}
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
