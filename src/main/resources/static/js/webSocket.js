const path = window.location.pathname;

let subscriptionMap = new Map();
let stompClient;

const socketFactory = function() {
    return new SockJS(`/parkingLot-websocket`);
}

async function createStompClient() {
    const token = tokenChecking();
    const userId = await getUserId();
    const stompClient = new StompJs.Client({
        webSocketFactory: socketFactory,
        connectHeaders: {
            Authorization: `Bearer ${token}`
        },
        onConnect: async (frame) => {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/parkingLot', onParkingMessageReceived);
            stompClient.subscribe(`/user/${userId}/queue/chatroom`, (crframe) => {
                if (path.includes("/chatroom")) {
                    onChatroomReceived(crframe);
                } else {
                    const chatPageButton = document.querySelector("#chat-page-button");
                    chatPageButton.classList.add("breathing");
                }    
            });

            addChatRoomSubs();
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 10000, 
        heartbeatOutgoing: 10000,
    });

    stompClient.onStompError = function (frame) {
    console.log('Broker reported error: ' + frame.headers['message']);
    console.log('Additional details: ' + frame.body);
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    return stompClient;
};



async function getUserId() {
    const token = tokenChecking();
    const payload = await getPayload(token);

    return payload.id;
}

function onChatroomReceived(chatroomMessage) {
    const chatroomMessageToJSON = JSON.parse(chatroomMessage.body);
    const chatroomElement = getChatroomElement(chatroomMessageToJSON.id);

    if (chatroomElement) {
        const chatroomNameDiv = chatroomElement.querySelector(".chatroom-name");
        if (chatroomMessageToJSON.parkingLotId) {
            chatroomElement.setAttribute("parkingLotId", chatroomMessageToJSON.parkingLotId);
        }
        if (chatroomMessageToJSON.parkingLotName) {
            chatroomElement.setAttribute("parking-lot-name", chatroomMessageToJSON.parkingLotName);
            chatroomNameDiv.textContent = chatroomMessageToJSON.parkingLotName;
        }
        if (chatroomMessageToJSON.lastRead) {
            chatroomElement.setAttribute("lastReadTime", chatroomMessageToJSON.lastRead);
        }
        if (chatroomMessageToJSON.lastReceivedMessage) {
            chatroomElement.setAttribute("lastReceivedMessageTime", chatroomMessageToJSON.lastReceivedMessage);

        }
    } else {
        chatroomElement = createChatroomElement(chatroomMessageToJSON);
        document.querySelector(`#chatroom-${chatroomMessageToJSON.id}`).click();
    }
    unreadMessageCheck(chatroomElement);
}

async function unreadCheck() {
    const token = tokenChecking();
    const chatroomArray = await getChatroomArray();
    if (!token || !chatroomArray) {
        return null;
    }
    for (let i=0; i<chatroomArray.length; i++) {
        const lastReadTime = new Date(chatroomArray[i].lastRead);
        const lastReceivedMessageTime = new Date(chatroomArray[i].lastReceivedMessage);
        if (lastReceivedMessageTime > lastReadTime) {
            const chatPageButton = document.querySelector("#chat-page-button");
            chatPageButton.classList.add("breathing");
            return null;
        }
    }
}
unreadCheck();

async function getChatroomArray() {
    const token = localStorage.getItem("token");
    const response = await fetchAPI(`/api/chatroom`, token, "GET");
    const data = await handleResponse(response);
    return data;
}

function getChatroomElement(chatroomId) {
    const chatroomElements = chatroomElementContainer.querySelectorAll(".chatroom-element");
    if (chatroomElements) {
        for (let i=0; i<chatroomElements.length; i++) {
            const chatroomElement = document.querySelector(`#chatroom-${chatroomId}`);
            if (chatroomElement) {
                return chatroomElement;
            }
        }
    }
    return null;
}

async function addChatRoomSubs() {
    const chatroomArray = await getChatroomArray();
    if (chatroomArray) {
        for (let i=0; i<chatroomArray.length; i++) {
            if (!subscriptionMap.has(chatroomArray[i].id)) {
                let subscription = stompClient.subscribe(`/user/${chatroomArray[i].id}/queue/messages`, (msgframe) => {
                    if (path.includes("/chatroom")) {
                        onChatMessageReceived(msgframe);
                    } else {
                        const chatPageButton = document.querySelector("#chat-page-button");
                        chatPageButton.classList.add("breathing");
                    }    
                });
                subscriptionMap.set(chatroomArray[i].id, subscription);
            }
        }
    }
}

function onChatMessageReceived(chatMessage) {
    const chatMessageToJSON = JSON.parse(chatMessage.body);
    const chatroom = document.querySelector(`#chatroom-${chatMessageToJSON.chatroomId}`);
    const chatAreaId = chatMessagesName.getAttribute("chatroom-id");
    if (chatMessageToJSON.senderId != userId && chatMessageToJSON.recipientId == userId) {
        chatroom.setAttribute("lastreceivedmessagetime", chatMessageToJSON.timestamp);
    }
    displayMessage(chatMessageToJSON.chatroomId, chatMessageToJSON.senderId, chatMessageToJSON.message, chatMessageToJSON.timestamp);
    if (chatArea.style.display == "flex" && chatAreaId == chatMessageToJSON.chatroomId) {
        return null;
    } else {
        unreadMessageCheck(chatroom);
    }
}

function onParkingMessageReceived(parkingMessage) {
    const parkingMessageToJSON = JSON.parse(parkingMessage.body);
    if (typeof(parkingMessageToJSON) == "object") {
        handleParkingMapMessage(parkingMessageToJSON)
    } else if (typeof(parkingMessageToJSON) == "number") {
        handleParkingLotIdMessage(parkingMessageToJSON)
    }
}

function handleParkingMapMessage(parkingLotMapMessageToJSON) {
    if (typeof parkingLotGMPMap === 'undefined') {
        return null;
    }
    const parkingLotId = Number(Object.keys(parkingLotMapMessageToJSON)[0]);
    const parkingLotData = Object.values(parkingLotMapMessageToJSON)[0];
    
    if (parkingLotMap.get(parkingLotId) || parkingLotGMPMap.get(parkingLotId)) {
        parkingLotMap.set(parkingLotId, parkingLotData);
        const parkingLot = parkingLotMap.get(parkingLotId);
        const parkingLotGMP = parkingLotGMPMap.get(parkingLotId);
        const parkingLotMarker = parkingLotGMP.content;
        const priceTag = parkingLotMarker.querySelector("#priceLabel");
        const markerFoot = parkingLotMarker.querySelector("#markerFoot");
        priceTag.textContent = setParkingLotMarkerLabel(parkingLot);
        parkingLotMarker.style.backgroundColor = setColorByPrice(parkingLot.price);
        markerFoot.style.borderTop = "8px solid " + setColorByPrice(parkingLot.price);
        parkingLotInformationTable(parkingLot);
    } else {
        displayParkingLotMarker(parkingLotMapMessageToJSON);
    }
}

function handleParkingLotIdMessage(parkingLotIdMessageToJSON) {
    if (typeof parkingLotGMPMap === 'undefined' || typeof parkingLotMap === 'undefined') {
        return null;
    }
    let parkingLotGMP = parkingLotGMPMap.get(parkingLotIdMessageToJSON);
    let parkingLot = parkingLotMap.get(parkingLotIdMessageToJSON);
    if (!parkingLotGMP || !parkingLot) {
        return null;
    }
    
    parkingLotGMP.content.remove();
    parkingLotGMP.map = null;
    parkingLotGMPMap.delete(parkingLotIdMessageToJSON);
    parkingLotMap.delete(parkingLotIdMessageToJSON);
}

async function webSocketConnect() {
    stompClient = await createStompClient();
    stompClient.activate();
}

function webSocketDisConnect() {
    stompClient.deactivate();
}

function unsubscribeAll() {
    subscriptionMap.forEach(sub => {
        sub.unsubscribe();
    });
    subscriptionMap.clear();
}