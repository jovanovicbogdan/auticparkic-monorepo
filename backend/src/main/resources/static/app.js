const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:10000/ws'
});

stompClient.onConnect = (frame) => {
  setConnected(true);
  console.log('Connected: ' + frame);
  stompClient.subscribe('/topic/public', (elapsedTime) => {
    showElapsedTime(JSON.parse(elapsedTime.body).elapsedTime);
  });
};

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

function connect() {
  stompClient.activate();
}

function disconnect(intervalId) {
  stompClient.deactivate();
  setConnected(false);
  clearInterval(intervalId);
  console.log("Disconnected");
}

function sendName() {
  return setInterval(() => {
    stompClient.publish({
      destination: "/app/rides.getElapsedTime",
      body: JSON.stringify({'rideId': $("#ride-id").val()})
    });
  }, 1000);
}

function showElapsedTime(message) {
  $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
  let intervalId;
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect(intervalId));
  $("#send").click(() => intervalId = sendName());
});
