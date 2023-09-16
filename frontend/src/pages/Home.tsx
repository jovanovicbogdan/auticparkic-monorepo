// import ActiveRides from "../components/Ride/ActiveRides.tsx";

import { Client } from "@stomp/stompjs";
import { useState } from "react";

export default function Home() {
  const [stompClient, setStompClient] = useState<Client | null>(null);
  const [rideId, setRideId] = useState("");

  function connect() {
    if (stompClient === null) {
      const client = new Client({
        brokerURL: "ws://localhost:10000/ws",
        onConnect: () => {
          client.subscribe("/topic/public", (message) =>
            console.log(`Received: ${message.body}`)
          );
          client.publish({
            destination:
              "/app/rides.scheduleStreamingRidesElapsedTimeIfEligible",
            // body: "First Message",
          });
        },
      });
      client.activate();
      setStompClient(client);
    }
  }

  function disconnect() {
    if (stompClient) {
      stompClient
        .deactivate()
        .then(() => setStompClient(null))
        .catch(() => console.error("Error while disconnecting"));
    }
  }

  function startRide(rideId: string) {
    if (stompClient) {
      stompClient.publish({
        destination: "/app/rides.start",
        body: JSON.stringify({ rideId }),
      });
    }
  }

  function pauseRide(rideId: string) {
    if (stompClient) {
      stompClient.publish({
        destination: "/app/rides.pause",
        body: JSON.stringify({ rideId }),
      });
    }
  }

  return (
    <div>
      <input type="string" onChange={(e) => setRideId(e.target.value)} />
      <button onClick={() => connect()}>Connect</button>
      <button onClick={() => disconnect()}>Disconnect</button>
      <button onClick={() => startRide(rideId)}>Start Ride</button>
      <button onClick={() => pauseRide(rideId)}>Pause Ride</button>
    </div>
  );

  //   <div className="auticparkic">
  //     <ActiveRides />
  //   </div>
  // );
}
