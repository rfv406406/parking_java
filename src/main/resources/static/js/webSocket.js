const socketFactory = function() {
    return new SockJS('http://localhost:9999/parkingLot-websocket');
}

const client = new StompJs.Client({
    webSocketFactory: socketFactory,
    onConnect: (frame) => {
        console.log('Connected: ' + frame);
        client.subscribe('/topic/parkingLot', (parkingLotMapMessage) => {
            const parkingLotMapMessageToJSON = JSON.parse(parkingLotMapMessage.body);
            if (typeof(parkingLotMapMessageToJSON) == "object") {
                handleParkingMapMessage(parkingLotMapMessageToJSON)
            } else if (typeof(parkingLotMapMessageToJSON) == "number") {
                handleParkingLotIdMessage(parkingLotMapMessageToJSON)
            }
        });
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 10000, 
    heartbeatOutgoing: 10000,
});

function handleParkingMapMessage(parkingLotMapMessageToJSON) {
    const parkingLotId = Number(Object.keys(parkingLotMapMessageToJSON)[0]);
    const parkingLotData = Object.values(parkingLotMapMessageToJSON)[0];
    parkingLotMap.set(parkingLotId, parkingLotData);
    const parkingLot = parkingLotMap.get(parkingLotId);
    if (parkingLotGMPMap.get(parkingLotId)) {
        let parkingLotGMP = parkingLotGMPMap.get(parkingLotId);
        const parkingLotMarker = parkingLotGMP.content;
        const priceTag = parkingLotMarker.querySelector("#priceLabel");
        const markerFoot = parkingLotMarker.querySelector("#markerFoot");
        priceTag.textContent = setParkingLotMarkerLabel(parkingLot);
        parkingLotMarker.style.backgroundColor = setColorByPrice(parkingLot.price);
        markerFoot.style.borderTop = "8px solid " + setColorByPrice(parkingLot.price);
        parkingLotInformationTable(parkingLot);
    } else {
        displayParkingLotMarker(parkingLotMapMessageToJSON);
        setMarkerClusterer(parkingLotGMPArray);
    }
}

function handleParkingLotIdMessage(parkingLotMapMessageToJSON) {
    let parkingLotGMP = parkingLotGMPMap.get(parkingLotMapMessageToJSON);
    if (!parkingLotGMP) {
        return null;
    }
    parkingLotGMP.content.remove();
    parkingLotGMP.map = null;
    parkingLotGMPMap.delete(parkingLotMapMessageToJSON);
}

function webSocketConnect() {
    client.activate();
}

client.onStompError = function (frame) {
  console.log('Broker reported error: ' + frame.headers['message']);
  console.log('Additional details: ' + frame.body);
};

client.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};