const chatMessageContainer = document.querySelector("#chat-messages-container");
const userAccount = document.querySelector("#userAccount");
const chatroomElementContainer = document.querySelector("#chatroom-element-container");
let chatMessagesName = document.querySelector("#chat-messages-name");
const closeChatroomIcon = document.querySelector("#returnArrow");
const messageInput = document.querySelector('#message');
const messageForm = document.querySelector('#messageForm');
const fileInput = document.querySelector("#upload-files");
const chatArea = document.querySelector(".chat-area");
const chatroomList = document.querySelector(".chatroom-list");

let userId = null;
let recipientId = null;
let parkingLotName = null;
let currentChatroomId = null;
let parkingLotId = null;
let chatroomArray = null;
let unreadElementMap = new Map();

initChatPage();

async function initChatPage() {
    try {
        const token = tokenChecking();
        const payload = await getPayload(token);
        userId = payload.id;
        chatroomElementContainer.textContent = "";
        const response = await setChatroomData();
        displayMemberAccount(payload);
        chatroomArray = await getChatroomArray();
        displayChatroomElement(chatroomArray);
    } catch(error) {
        handleError(error);
    }
}

messageForm.addEventListener("click", sendMessage);
closeChatroomIcon.addEventListener("click", () => {
    chatArea.style.display = "none";
    chatMessageContainer.textContent = "";
    if (window.innerWidth < 600 && chatroomList.style.display === "none") {
        chatroomList.style.display = "flex";
    }
})

async function chatroomElementDoing(event) {
    const chatroomElement = event.currentTarget;
    const chatroomElementId = chatroomElement.id;
    const chatroomElementIdSplit = chatroomElementId.split("chatroom-")[1];
    const currentTime = getCurrentDateTime();
    if (chatroomElementIdSplit == currentChatroomId && chatArea.style.display == "flex") {
        return null;
    }
    chatMessageContainer.textContent = "";
    const parkingLotNameData = chatroomElement.getAttribute("parking-Lot-Name");
    chatMessagesName.textContent = parkingLotNameData;
    recipientId = chatroomElement.getAttribute("recipientId");
    parkingLotName = parkingLotNameData;
    parkingLotId = chatroomElement.getAttribute("parkingLotId");
    currentChatroomId = chatroomElementIdSplit;
    chatMessagesName.setAttribute('chatroom-id', currentChatroomId);
    chatroomElement.setAttribute("lastReadTime", currentTime);
    const messageObjects = await getChatmessage(currentChatroomId);
    await putChatroomLastRead(currentChatroomId, currentTime);
    if (messageObjects) {
        messageObjects.forEach(messageObject => {
            displayMessage(messageObject.chatroomId, messageObject.senderId, messageObject.message, messageObject.timestamp); 
        });
    }
    if (window.innerWidth < 600 && chatArea.style.display === "none") {
        chatroomList.style.display = "none";
    }
    chatArea.style.display = "flex";
    chatMessageContainer.scrollTop = chatMessageContainer.scrollHeight;
}

function getRecipientData() {
    const url = new URL(window.location.href);
    const pathname = decodeURI(url.pathname);
    if (pathname === "/chatroom") {
        return null;
    }
    const pathnameArray = pathname.split("/chatroom/")[1].split("/");
    if (pathnameArray.length < 4) {
        return null;
    }
    const recipientId = pathnameArray[0];
    const recipientAccount = pathnameArray[1];
    const parkingLotId = pathnameArray[2];
    const parkingLotNameArray = pathnameArray[3].split("%20");
    let parkingLotName = "";

    for (let i=0; i<parkingLotNameArray.length; i++) {
        parkingLotName += parkingLotNameArray[i];
    }

    window.history.replaceState(null, document.title, '/chatroom');

    return {
        "recipientId" : recipientId,
        "parkingLotId" : parkingLotId,
        "parkingLotName" : parkingLotName
    }
}

function displayMemberAccount(payload) {
    userAccount.textContent = payload.username;
}

function displayChatroomElement(chatroomArray) {
    if (chatroomArray) {
        const firstId = chatroomArray[0].id;
        for (let i=0; i<chatroomArray.length; i++) {
            console.log(chatroomArray[i])
            const chatroomElement = createChatroomElement(chatroomArray[i]);
            unreadMessageCheck(chatroomElement);
        }
        document.querySelector(`#chatroom-${firstId}`).click();
    } 
}

function createChatroomElement(data) {
    const chatroomId = data.id;
    const recipientAccount = data.recipientAccount;
    const parkingLotId = data.parkingLotId; 
    const parkingLotName = data.parkingLotName;
    const lastReadTime = data.lastRead;
    const lastReceivedMessageTime = data.lastReceivedMessage;
    let recipientId = null;

    if (data.recipientId != userId) {
        recipientId = data.recipientId;
    } else if (data.recipientId == userId) {
        recipientId = data.senderId;
    }
    const chatroomElement = document.createElement("div");
    chatroomElement.className = "chatroom-element";
    chatroomElement.id = "chatroom-" + chatroomId;
    chatroomElement.setAttribute("recipientId", recipientId);
    chatroomElement.setAttribute("parkingLotId", parkingLotId);
    if (!parkingLotName) {
        chatroomElement.setAttribute("parking-Lot-Name", "此停車場已刪除");
    } else {
        chatroomElement.setAttribute("parking-Lot-Name", parkingLotName);
    }
    chatroomElement.setAttribute("lastReadTime", lastReadTime);
    chatroomElement.setAttribute("lastReceivedMessageTime", lastReceivedMessageTime);
    const chatroomElementIcon = document.createElement("img");
    chatroomElementIcon.id = "icon";
    chatroomElementIcon.src = "/image/chatparkingimg.png";
    const chatroomName = document.createElement("div");
    chatroomName.className = "chatroom-name";  
    if (!parkingLotName) {
        chatroomName.textContent = "此停車場已刪除";
    } else {
        chatroomName.textContent = parkingLotName;
    } 
    

    chatroomElement.appendChild(chatroomElementIcon);
    chatroomElement.appendChild(chatroomName);
    chatroomElementContainer.prepend(chatroomElement);

    chatroomElement.addEventListener("click", async (event) => {
        await chatroomElementDoing(event);
    })
    
    return chatroomElement;
}

function getCurrentDateTime() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); 
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

function displayMessage(chatroomId, senderId, content, currentTime) {
    const chatAreaId = chatMessagesName.getAttribute("chatroom-id");
    console.log(chatroomId != chatAreaId);
    if (chatroomId != chatAreaId) {
        return null;
    }
    const messageContainer = document.createElement('div');
    let message;
    const timesDiv = document.createElement('div');
    timesDiv.classList.add('times-div');
    messageContainer.classList.add('message');

    if (senderId === userId) {
        messageContainer.classList.add('sender');
        timesDiv.classList.add("sender-time");
    } else {
        messageContainer.classList.add('receiver');
        timesDiv.classList.add("receiver-time");
    }

    if (typeof(content) == "string" && content.includes("d1hxt3hn1q2xo2")) {
        const img = document.createElement("img");
        message = document.createElement('div');
        message.style.overflow = "hidden";
        message.style.borderRadius = "5px";
        // img.src = URL.createObjectURL(content);
        img.src = content;
        img.style.width = "100%";
        img.style.height = "100%";
        img.style.verticalAlign = "bottom";
        img.onerror = function() {
            this.onerror = null;
            img.src = '/image/noimage.png';
        }
        img.onload = () => {
            chatMessageContainer.scrollTop = chatMessageContainer.scrollHeight;
        };
        message.appendChild(img);
    } else if (typeof(content) == "string") {
        message = document.createElement('p');
        message.textContent = content;
    } else {
        return null
    }
    
    timesDiv.textContent = currentTime ? currentTime : content.timestamp;
    messageContainer.appendChild(message);
    chatMessageContainer.appendChild(messageContainer);
    chatMessageContainer.appendChild(timesDiv);
    chatMessageContainer.scrollTop = chatMessageContainer.scrollHeight;
}

fileInput.addEventListener("change", () => {
    const files = fileInput.files;
    for (let i=0; i<files.length; i++) {
        if (!files[i].type.startsWith('image/')) {
            displayAlertMessage("此聊天室僅供通報使用，禁止上傳非圖片相關之檔案。");
            return null;
        }
    }
    const currentTime = getCurrentDateTime();
    const chatMessage = {
        chatroomId: currentChatroomId,
        senderId: userId,
        recipientId: recipientId,
        message: files,
        timestamp: currentTime
    };
    inputChatMessageToDB([chatMessage]);
    fileInput.value = '';
})

async function sendMessage() {
    const currentTime = getCurrentDateTime();
    const messageContent = messageInput.value.trim();
    const subscription = subscriptionMap.get(Number(currentChatroomId));
    // const subscription = null;
    if (messageContent && stompClient && subscription) {
        const chatMessage = {
            chatroomId: currentChatroomId,
            senderId: userId,
            recipientId: recipientId,
            message: messageInput.value.trim(),
            timestamp: currentTime
        };
        stompClient.publish({ destination: '/app/chatmessage', body: JSON.stringify([chatMessage]) });
        messageInput.value = '';
    } else if (messageContent) {
        const chatMessage = {
            chatroomId: currentChatroomId,
            senderId: userId,
            recipientId: recipientId,
            message: messageInput.value.trim(),
            timestamp: currentTime
        };
        inputChatMessageToDB([chatMessage]);
        displayMessage(chatMessage.currentChatroomId, chatMessage.senderId, chatMessage.message, chatMessage.timestamp);
        messageInput.value = '';
    }
    chatMessageContainer.scrollTop = chatMessageContainer.scrollHeight;
}

async function setChatroomData() {
    const token = localStorage.getItem("token");
    const recipientData = getRecipientData();
    const currentTime = getCurrentDateTime();

    if (!recipientData || recipientData == null) {
        return null;
    }

    const data = {
        "senderId": userId,
        "recipientId": recipientData.recipientId,
        "parkingLotId": recipientData.parkingLotId,
        "parkingLotName" : recipientData.parkingLotName,
        "lastRead" : currentTime,
        "lastActivity" : currentTime
    }

    const response = await fetchAPI("/api/chatroom", token, "POST", data);
    const updatedChatroomId = await handleResponse(response); 

    return updatedChatroomId;
}

function unreadMessageCheck(chatroomElement) {
    const laetReadTime = new Date(chatroomElement.getAttribute("lastreadtime"));
    const lastReceivedMessageTime = new Date(chatroomElement.getAttribute("lastreceivedmessagetime"));
    const chatroomIcon = chatroomElement.querySelector("#icon");
    const chatPageButton = document.querySelector("#chat-page-button");
    if (lastReceivedMessageTime > laetReadTime) {
        chatroomIcon.src = "/image/unreadchatimg.png";
        unreadElementMap.set(chatroomElement.id, chatroomElement);
    } else {
        chatroomIcon.src = "/image/chatparkingimg.png";
        unreadElementMap.delete(chatroomElement.id);
    }

    if (unreadElementMap.size > 0 ) {
        chatPageButton.classList.add("breathing");
    } else {
        chatPageButton.classList.remove("breathing");
    }
}

async function inputChatMessageToDB(contents) {
    const token = localStorage.getItem("token");
    const maxSize = 10 * 1024 * 1024;
    const alertMessage = "請選擇小於 10MB 的檔案，總檔案物超過20 MB!";
    const content = contents[0];
    let formData = new FormData();
    formData.append("chatMessage[0].chatroomId", content.chatroomId);
    formData.append("chatMessage[0].senderId", content.senderId);
    formData.append("chatMessage[0].recipientId", content.recipientId);
    formData.append("chatMessage[0].timestamp", content.timestamp);
    if (content.message instanceof FileList) {
        if (content.message.size > maxSize * 2) {
            displayAlertMessage(alertMessage);
            return null;
        }
        for(let i=0; i<content.message.length; i++) {
            if (content.message[i].size > maxSize) {
                displayAlertMessage(alertMessage);
                return null;
            }
            formData.append("chatMessage[0].img", content.message[i]);
        }
    } else if (typeof(content.message) == "string") {
        formData.append("chatMessage[0].message", content.message);
    } else {
        return null;
    }
   
    const response = await fetchAPI("/api/chatmessage", token, "POST", formData);
    const data = await handleResponse(response);
    // console.log(data)
}

async function getChatmessage(chatroomId) {
    const token = localStorage.getItem("token");
    const response = await fetchAPI(`/api/chatmessage/${chatroomId}`, token, "GET");
    const data = await handleResponse(response);
    return data;
}

async function putChatroomLastRead(currentChatroomId, currentTime) {
    const token = localStorage.getItem("token");
    const response = await fetchAPI(`/api/chatroom/${currentChatroomId}/${currentTime}`, token, "PUT");
    const data = await handleResponse(response);
    // console.log("updatelastread: "+data);
}

function applyLayout() {
  if (window.innerWidth < 600) {
    if (chatArea.style.display === "flex") {
        chatroomList.style.display = "none";
    } else {
        chatroomList.style.display = "flex";
    }
  } 

  if (window.innerWidth >= 600) {
    chatArea.style.display === "flex"
    chatroomList.style.display = "flex";
  }
}

applyLayout();

window.addEventListener('resize', applyLayout);

